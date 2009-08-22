package net.sf.cotta;

import java.io.File;

/**
 * Entry instance that represents either a file or a directory.
 */
abstract public class TEntry implements Comparable<TEntry> {
  protected TPath path;
  private TFileFactory factory;

  /**
   * Create an instance of TEntry
   *
   * @param fileSystem file system backing the entry
   * @param path       path for the entry
   * @see #TEntry(TFileFactory, TPath)
   * @deprecated use the other constructor for default encoding support through TFactory
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

  public TFileFactory factory() {
    return factory;
  }

  protected FileSystem filesystem() {
    return factory.getFileSystem();
  }

  public TPath toPath() {
    return path;
  }

  /**
   * Alias of path() which returns system specific path string
   * For example, on physical system the path separator will be OS specific
   *
   * @return path string
   * @see #path()
   */
  public String toPathString() {
    return path();
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

  /**
   * Ssystem specific path string
   * For example, on physical system the path separator will be OS specific
   *
   * @return path string
   * @see #toPathString()
   */
  public String path() {
    return filesystem().pathString(path);
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
    buffer.append("<").append(path()).append(">");
    if (path.isRelative()) {
      buffer.append(" relative to <");
      buffer.append(filesystem().toCanonicalPath(TPath.parse("./")));
      buffer.append(">");
    }
    return buffer.toString();
  }

  public abstract boolean exists();

  public int hashCode() {
    return filesystem().hashCode(path);
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final TEntry entry = (TEntry) o;

    return filesystem().equals(entry.filesystem()) && filesystem().equals(path, entry.toPath());
  }

  public int compareTo(TEntry that) {
    if (that == null) {
      throw new IllegalArgumentException("Cannot compare to a null object");
    }
    int compareTypeCode = getTypeCode(getClass()) - getTypeCode(that.getClass());
    if (compareTypeCode != 0) {
      return compareTypeCode;
    }
    return filesystem().compare(toPath(), that.toPath());
  }

  private int getTypeCode(Class<? extends TEntry> type) {
    if (type.equals(TDirectory.class)) {
      return 0;
    }
    return 1;
  }
}
