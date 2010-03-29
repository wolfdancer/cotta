package net.sf.cotta.zip;

import net.sf.cotta.*;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.system.FileSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZipFileSystem that operates on a Zip file.  All the entries are loaded into the memory and the contents
 * are retrieved on demand.
 */
public class ZipFileSystem implements FileSystem, TResource {
  private ZipFile file;
  private InMemoryFileSystem entrySystem;
  private TFileFactory factory;

  public ZipFileSystem(File jarFile) throws IOException {
    entrySystem = new InMemoryFileSystem();
    entrySystem.setFileInitialCapacity(0);
    entrySystem.setFileSizeIncrement(0);
    factory = new TFileFactory(entrySystem);
    load(jarFile);
  }

  private void load(File jarFile) throws IOException {
    this.file = new ZipFile(jarFile);
    for (Enumeration enumeration = file.entries(); enumeration.hasMoreElements();) {
      ZipEntry entry = (ZipEntry) enumeration.nextElement();
      String pathString = "/" + entry.getName();
      if (entry.isDirectory()) {
        factory.dir(pathString).ensureExists();
      } else {
        factory.file(pathString).create();
      }
    }
  }

  public boolean fileExists(TPath path) {
    return entrySystem.fileExists(path);
  }

  public boolean dirExists(TPath path) {
    return entrySystem.dirExists(path);
  }

  public InputStream createInputStream(TPath path) throws TIoException {
    if (!entrySystem.fileExists(path)) {
      throw new TFileNotFoundException(path);
    }
    ZipEntry entry = entry(path);
    try {
      return file.getInputStream(entry);
    } catch (IOException e) {
      throw new TIoException(path, "Error opening entry", e);
    }
  }

  private ZipEntry entry(TPath path) {
    String pathString = path.toPathString();
    if (pathString.startsWith("/")) {
      pathString = pathString.substring(1);
    }
    return file.getEntry(pathString);
  }

  public void createDir(TPath path) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public PathContent list(TPath path) {
    return entrySystem.list(path);
  }

  public void createFile(TPath path) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public void deleteFile(TPath path) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public void deleteDirectory(TPath path) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public void moveFile(TPath source, TPath destination) {
    throw new UnsupportedOperationException();
  }

  public void moveDirectory(TPath path, TPath path1) throws TIoException {
    throw new UnsupportedOperationException();
  }

  public String pathString(TPath path) {
    StringBuffer buffer = new StringBuffer(file.getName());
    buffer.append("[").append(path.toPathString()).append("]");
    return buffer.toString();
  }

  public long fileLength(TPath path) {
    return entry(path).getSize();
  }

  public long fileLastModified(TPath path) {
    return entry(path).getTime();
  }

  public int compare(TPath path1, TPath path2) {
    return path1.compareTo(path2);
  }

  public boolean equals(TPath path1, TPath path2) {
    return path1.equals(path2);
  }

  public int hashCode(TPath path) {
    return path.hashCode();
  }

  public File toJavaFile(TPath path) {
    throw new UnsupportedOperationException("ZipFileSystem");
  }

  public String toCanonicalPath(TPath path) {
    return "Zip://" + pathString(path);
  }

  public FileChannel createInputChannel(TPath path) throws TIoException {
    throw new UnsupportedOperationException("not implemented for zip file");
  }

  public void close() throws TIoException {
    try {
      file.close();
    } catch (IOException e) {
      throw new TIoException(TPath.parse("/"), "Cannot close jar file", e);
    }
  }

  public static FileSystem readOnlyZipFileSystem(File jarFile) throws TIoException {
    ZipFileSystem zipFileSystem;
    try {
      zipFileSystem = new ZipFileSystem(jarFile);
    } catch (IOException e) {
      throw new TIoException(TPath.parse("/"), "Error opening zip file <" + jarFile.getAbsolutePath() + ">", e);
    }
    return ControlledFileSystem.readOnlyFileSystem(zipFileSystem);
  }
}
