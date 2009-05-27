package net.sf.cotta;

import net.sf.cotta.io.OutputMode;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * The file system that handles the implementation of the file operations
 */
public interface FileSystem {
  boolean fileExists(TPath path) throws TIoException;

  void createFile(TPath path) throws TIoException;

  void deleteFile(TPath path) throws TIoException;

  boolean dirExists(TPath path) throws TIoException;

  void createDir(TPath path) throws TIoException;

  /**
   * List the content of the path
   *
   * @param path path
   * @return path content, which contains a list of paths for the files and a list of paths for the directories
   * @throws TIoException exception from the system.  For example, when a direcotry does not exist, a physical system will throw the exception
   */
  PathContent list(TPath path) throws TIoException;

  InputStream createInputStream(TPath path) throws TIoException;

  OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException;

  FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException;

  void deleteDirectory(TPath path) throws TIoException;

  void moveFile(TPath source, TPath destination) throws TIoException;

  void moveDirectory(TPath source, TPath destination) throws TIoException;

  String pathString(TPath path);

  long fileLength(TPath path) throws TIoException;

  File toJavaFile(TPath path);

  String toCanonicalPath(TPath path);

  FileChannel createInputChannel(TPath path) throws TIoException;

  long fileLastModified(TPath path) throws TIoException;

  /**
   * Compares the two path
   *
   * @param path1 path one
   * @param path2 path two
   * @return the comparing result used for sort
   */
  int compare(TPath path1, TPath path2);

  /**
   * Check if two paths are equal.  This is needed for cases like on Windows system "c:\a\b\c.txt" is the same as "C:\A\B\C.TXT" even
   * though the case is different
   *
   * @param path1 path one
   * @param path2 path two
   * @return true if the two paths are equal according to the file system
   */
  boolean equals(TPath path1, TPath path2);

  /**
   * Returns the hash code for the path
   *
   * @param path path
   * @return hash code
   */
  int hashCode(TPath path);

}
