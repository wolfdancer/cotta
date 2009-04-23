package net.sf.cotta;

import net.sf.cotta.io.OutputMode;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * @noinspection JavaDoc
 */
public interface FileSystem {
  boolean fileExists(TPath path) throws TIoException;

  void createFile(TPath path) throws TIoException;

  void deleteFile(TPath path) throws TIoException;

  boolean dirExists(TPath path) throws TIoException;

  void createDir(TPath path) throws TIoException;

  TPath[] listDirs(TPath path) throws TIoException;

  TPath[] listFiles(TPath path) throws TIoException;

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
}
