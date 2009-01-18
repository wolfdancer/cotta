package net.sf.cotta.physical;

import net.sf.cotta.FileSystem;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.io.OutputMode;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * File system that represents the physical file.
 */
public class PhysicalFileSystem implements FileSystem {
  public boolean fileExists(TPath path) {
    File file = file(path);
    return file.exists() && file.isFile();
  }

  private File file(TPath path) {
    return new File(path.toSystemPathString());
  }

  public void createFile(TPath path) throws TIoException {
    try {
      if (!file(path).createNewFile()) {
        throw new TIoException(path, "file creation failed");
      }
    } catch (IOException e) {
      throw new TIoException(path, "file creation failed", e);
    }
  }

  private void ensureParentExists(TPath path) throws TIoException {
    if (!dirExists(path.parent())) {
      createDir(path.parent());
    }
  }

  public void deleteFile(TPath path) throws TIoException {
    File file = file(path);
    if (!file.delete()) {
      throw new TIoException(path, "Deleting file failed:" + file.getAbsolutePath());
    }
  }

  public boolean dirExists(TPath path) {
    File file = file(path);
    return file.isDirectory() && file.exists();
  }

  public void createDir(TPath path) throws TIoException {
    if (!file(path).mkdirs()) {
      throw new TIoException(path, "Creating directory failed");
    }
  }

  public TPath[] listDirs(TPath path) throws TIoException {
    File[] files = file(path).listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    });
    if (files == null) {
      throw new TIoException(path, "listing dirs");
    }
    return convert(path, files);
  }

  private TPath[] convert(TPath parent, File[] files) {
    TPath[] paths = new TPath[files.length];
    for (int i = 0; i < files.length; i++) {
      paths[i] = parent.join(files[i].getName());
    }
    return paths;
  }

  public TPath[] listFiles(TPath path) throws TIoException {
    File[] files = file(path).listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.isFile();
      }
    });
    if (files == null) {
      throw new TIoException(path, "list files");
    }
    return convert(path, files);
  }

  public InputStream createInputStream(TPath path) throws TIoException {
    try {
      return new FileInputStream(file(path));
    } catch (FileNotFoundException e) {
      throw new TIoException(path, "Creating inputstream failed", e);
    }
  }

  public OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException {
    ensureParentExists(path);
    try {
      return new FileOutputStream(file(path), mode.isAppend());
    } catch (FileNotFoundException e) {
      throw new TIoException(path, "Creating outputstream failed", e);
    }
  }

  public FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException {
    return ((FileOutputStream) outputStream).getChannel();
  }

  public void deleteDirectory(TPath path) throws TIoException {
    if (!file(path).delete()) {
      throw new TIoException(path, "Deleting directory failed");
    }
  }

  public void moveFile(TPath source, TPath destination) throws TIoException {
    if (!file(source).renameTo(file(destination))) {
      throw new TIoException(source, "Moving file failed, target <" + destination.toPathString() + ">");
    }
  }

  public void moveDirectory(TPath source, TPath destination) throws TIoException {
    if (!file(source).renameTo(file(destination))) {
      throw new TIoException(source, "Moving file failed, target <" + destination.toPathString() + ">");
    }
  }

  public String pathString(TPath path) {
    return file(path).getPath();
  }

  public long fileLength(TPath path) {
    return file(path).length();
  }

  public long fileLastModified(TPath path) {
    return file(path).lastModified();
  }

  public File toJavaFile(TPath path) {
    return new File(pathString(path));
  }

  public String toCanonicalPath(TPath path) {
    try {
      return toJavaFile(path).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public FileChannel createInputChannel(TPath path) throws TIoException {
    return ((FileInputStream) createInputStream(path)).getChannel();
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o != null && getClass() == o.getClass();
  }

  public int hashCode() {
    return getClass().hashCode();
  }
}
