package net.sf.cotta;

import java.io.File;

public class TEntry {
  protected TPath path;
  protected FileSystem fileSystem;

  public TEntry(FileSystem fileSystem, TPath path) {
    this.path = path;
    this.fileSystem = fileSystem;
  }

  public boolean isChildOf(TDirectory directory) {
    return path.isChildOf(directory.toPath());
  }

  public TPath pathFrom(TDirectory directory) {
    return path.pathFrom(directory.toPath());
  }

  public String name() {
    return path.lastElementName();
  }

  public TDirectory parent() {
    return new TDirectory(fileSystem, path.parent());
  }

  public String toCanonicalPath() {
    return fileSystem.toCanonicalPath(path);
  }

  public TPath toPath() {
    return path;
  }

  /**
   * Converts the instance to java.io.File.  This is used to integrate with the system
   * that uses java.io.File
   *
   * @return The java.io.File instance of the current file
   * @throws RuntimeException if the underlying file system is not a normal file system.
   */
  public File toJavaFile() {
    return fileSystem.toJavaFile(path);
  }

  public int hashCode() {
    int result;
    result = path.hashCode();
    result = 29 * result + fileSystem.hashCode();
    return result;
  }

  public String path() {
    return fileSystem.pathString(path);
  }
}
