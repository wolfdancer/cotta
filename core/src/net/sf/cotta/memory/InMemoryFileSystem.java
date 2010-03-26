package net.sf.cotta.memory;

import net.sf.cotta.FileSystem;
import net.sf.cotta.PathContent;
import net.sf.cotta.PathSeparator;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.system.ContentManager;
import net.sf.cotta.system.DirectoryIndex;
import net.sf.cotta.system.FileContent;
import net.sf.cotta.system.HashBasedDirectoryIndex;
import net.sf.cotta.system.TreeBasedDirectoryIndex;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * A file system whose directory structure and file contents are stored in memory.
 * It can be backed by a hash-based directory index or a tree-based one, with hash-based
 * being the default.
 *
 * @see net.sf.cotta.memory.InMemoryFileSystemBuilder for more flexibility in building an instance.
 */
public class InMemoryFileSystem implements FileSystem, ContentManager<InMemoryFileContent> {
  public enum IndexType { HASH_BASED, TREE_BASED }

  static final PathSeparator DEFAULT_PATH_SEPARATOR = PathSeparator.Unix;
  static final ListingOrder DEFAULT_LISTING_ORDER = ListingOrder.NULL;
  static final IndexType DEFAULT_INDEX_TYPE = IndexType.HASH_BASED;

  private int fileInitialCapacity = 0;
  private int fileSizeIncrement = 16;
  private final DirectoryIndex<InMemoryFileContent> dirIndex;

  public InMemoryFileSystem() {
    this(DEFAULT_PATH_SEPARATOR);
  }

  public InMemoryFileSystem(ListingOrder order) {
    this(DEFAULT_PATH_SEPARATOR, order);
  }

  public InMemoryFileSystem(PathSeparator separator) {
    this(separator, DEFAULT_LISTING_ORDER);
  }

  public InMemoryFileSystem(PathSeparator separator, ListingOrder order) {
    this(separator, order, DEFAULT_INDEX_TYPE);
  }

  /**
   * Constructor used by the other constructors or by {@link net.sf.cotta.memory.InMemoryFileSystemBuilder}
   * @param separator the desired path separator
   * @param order the desired listing order
   * @param index the desired directory index type
   */
  InMemoryFileSystem(PathSeparator separator, ListingOrder order, IndexType index) {
    if (index == IndexType.HASH_BASED) {
      this.dirIndex = new HashBasedDirectoryIndex<InMemoryFileContent>(separator, order, this);
    }
    else if (index == IndexType.TREE_BASED) {
      this.dirIndex = new TreeBasedDirectoryIndex<InMemoryFileContent>(separator, order, this);
    }
    else {
      throw new IllegalArgumentException("unrecognized index type: " + index);
    }
  }

  public void setFileInitialCapacity(int value) {
    this.fileInitialCapacity = value;
  }

  public void setFileSizeIncrement(int value) {
    this.fileSizeIncrement = value;
  }

  public InMemoryFileContent createFileContent() {
    return new InMemoryFileContent(fileInitialCapacity, fileSizeIncrement);
  }

  public boolean fileExists(TPath path) {
    return dirIndex.fileExists(path);
  }

  public void createFile(TPath path) throws TIoException {
    dirIndex.createFile(path).setContent("");
  }

  public void createDir(TPath path) throws TIoException {
    dirIndex.createDir(path);
  }

  public void deleteFile(TPath path) throws TFileNotFoundException {
    dirIndex.deleteFile(path);
  }

  public boolean dirExists(TPath path) {
    return dirIndex.dirExists(path);
  }

  public PathContent list(TPath path) {
    return dirIndex.list(path);
  }

  public InputStream createInputStream(TPath path) throws TIoException {
    return retrieveFileContent(path).inputStream();
  }

  private FileContent retrieveFileContent(TPath path) throws TFileNotFoundException {
    FileContent content = dirIndex.fileContent(path);
    if (content == null) {
      throw new TFileNotFoundException(path);
    }
    return content;
  }

  public OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException {
    InMemoryFileContent content = dirIndex.fileContent(path);
    if (content == null) {
      content = dirIndex.createFile(path);
    }
    if (mode.isOverwrite()) {
      content.setContent("");
    }
    return content.outputStream();
  }

  public FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException {
    return null;
  }

  public void deleteDirectory(TPath path) throws TIoException {
    dirIndex.deleteDir(path);
  }

  public void moveFile(TPath source, TPath destination) throws TIoException {
    dirIndex.moveFile(source, destination);
  }

  public void moveDirectory(TPath source, TPath destination) throws TIoException {
    dirIndex.moveDir(source, destination);
  }

  public String pathString(TPath path) {
    return dirIndex.pathString(path);
  }

  public long fileLength(TPath path) {
    return dirIndex.fileContent(path).getContentBuffer().size();
  }

  public long fileLastModified(TPath path) {
    return dirIndex.fileContent(path).lastModified();
  }

  public int compare(TPath path1, TPath path2) {
    return dirIndex.compare(path1, path2);
  }

  public boolean equals(TPath path1, TPath path2) {
    return dirIndex.equals(path1, path2);
  }

  public int hashCode(TPath path) {
    return dirIndex.hashCode(path);
  }

  public File toJavaFile(TPath path) {
    throw new UnsupportedOperationException("InMemoryFileSystem");
  }

  public String toCanonicalPath(TPath path) {
    return "memory://" + pathString(path);
  }

  public FileChannel createInputChannel(TPath path) throws TFileNotFoundException {
    return retrieveFileContent(path).inputChannel();
  }

}
