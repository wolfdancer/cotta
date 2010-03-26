package net.sf.cotta.system;

import net.sf.cotta.PathContent;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

/**
 * The DirectoryIndex stores directory structure information for FileSystems
 * which store such info in memory (e.g. InMemoryFileSystem)
 */
public interface DirectoryIndex<F extends FileContent> {
  boolean fileExists(TPath path);

  boolean dirExists(TPath path);

  PathContent list(TPath path);

  String pathString(TPath path);

  int compare(TPath path1, TPath path2);

  boolean equals(TPath path1, TPath path2);

  int hashCode(TPath path);

  F fileContent(TPath path);

  F createFile(TPath path) throws TIoException;

  void deleteFile(TPath path) throws TFileNotFoundException;

  void moveFile(TPath source, TPath dest) throws TIoException;

  void createDir(TPath path) throws TIoException;

  void deleteDir(TPath path) throws TIoException;

  void moveDir(TPath source, TPath dest) throws TIoException;
}
