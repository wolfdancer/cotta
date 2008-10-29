package net.sf.cotta;

import java.io.File;

/**
 * Entry instance that represents either a file or a directory.
 */
public class TEntry {
  protected TPath path;
  private TFileFactory factory;

  /**
   * Create an instance of TEntry
   * @param fileSystem file system backing the entry
   * @param path path for the entry
   * @deprecated use the other constructor for default encoding support through TFactory
   * @see #TEntry(TFileFactory, TPath)
   */
  public TEntry(FileSystem fileSystem, TPath path) {
    this(new TFileFactory(fileSystem), path);
  }

  public TEntry(TFileFactory factory, TPath path) {
    this.factory = factory;
    this.path = path;
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
    return new TDirectory(factory(), path.parent());
  }

  public String toCanonicalPath() {
    return filesystem().toCanonicalPath(path);
  }

  protected TFileFactory factory() {
    return factory;
  }

  protected FileSystem filesystem() {
    return factory.getFileSystem();
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
    return filesystem().toJavaFile(path);
  }

  public int hashCode() {
    int result;
    result = path.hashCode();
    result = 29 * result + filesystem().hashCode();
    return result;
  }

  public String path() {
    return filesystem().pathString(path);
  }
}
