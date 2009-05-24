package net.sf.cotta;

import net.sf.cotta.io.OutputMode;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * A file system that delegates all file operations to a controller
 *
 * @see net.sf.cotta.ControlledFileSystem.Controller
 */
public class ControlledFileSystem implements FileSystem {
  private FileSystem fileSystem;
  protected Controller controller;

  public ControlledFileSystem(FileSystem fileSystem, Controller controller) {
    this.controller = controller;
    this.fileSystem = fileSystem;
  }

  public boolean fileExists(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.fileExists(path);
  }

  public void createFile(TPath path) throws TIoException {
    controller.writeOperationControl(path);
    fileSystem.createFile(path);
  }

  public void deleteFile(TPath path) throws TIoException {
    controller.writeOperationControl(path);
    fileSystem.deleteFile(path);
  }

  public boolean dirExists(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.dirExists(path);
  }

  public void createDir(TPath path) throws TIoException {
    controller.writeOperationControl(path);
    fileSystem.createDir(path);
  }

  public TPath[] listDirs(TPath path) throws TIoException {
    return fileSystem.listDirs(path);
  }

  public TPath[] listFiles(TPath path) throws TIoException {
    return fileSystem.listFiles(path);
  }

  public InputStream createInputStream(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.createInputStream(path);
  }

  public FileChannel createInputChannel(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.createInputChannel(path);
  }

  public OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException {
    controller.writeOperationControl(path);
    return fileSystem.createOutputStream(path, mode);
  }

  public FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException {
    controller.writeOperationControl(path);
    return fileSystem.createOutputChannel(path, outputStream);
  }

  public void deleteDirectory(TPath path) throws TIoException {
    controller.writeOperationControl(path);
    fileSystem.deleteDirectory(path);
  }

  public void moveFile(TPath source, TPath destination) throws TIoException {
    controller.writeOperationControl(source);
    controller.writeOperationControl(destination);
    fileSystem.moveFile(source, destination);
  }

  public void moveDirectory(TPath source, TPath destination) throws TIoException {
    controller.writeOperationControl(source);
    controller.writeOperationControl(destination);
    fileSystem.moveFile(source, destination);
  }

  public String pathString(TPath path) {
    return fileSystem.pathString(path);
  }

  public long fileLength(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.fileLength(path);
  }

  public long fileLastModified(TPath path) throws TIoException {
    controller.readOperationControl(path);
    return fileSystem.fileLastModified(path);
  }

  public int compare(TPath path1, TPath path2) {
    readControl(path1);
    readControl(path2);
    return fileSystem.compare(path1, path2);
  }

  public boolean equals(TPath path1, TPath path2) {
    readControl(path1);
    readControl(path2);
    return fileSystem.equals(path1, path2);
  }

  public int hashCode(TPath path) {
    readControl(path);
    return fileSystem.hashCode(path);
  }

  private void readControl(TPath path) {
    try {
      controller.readOperationControl(path);
    } catch (TIoException e) {
      throw new TIoRuntimeException(e);
    }
  }

  public File toJavaFile(TPath path) {
    return fileSystem.toJavaFile(path);
  }

  public String toCanonicalPath(TPath path) {
    return fileSystem.toCanonicalPath(path);
  }

  public static FileSystem pathControlledFileSystem(FileSystem fileSystem, final TPath pathAllowed) {
    return new ControlledFileSystem(fileSystem, new PermissionController() {
      public boolean writeAllowed(TPath path) {
        return path.isChildOf(pathAllowed) || path.equals(pathAllowed);
      }

      public StringBuffer describe(StringBuffer buffer) {
        return buffer.append("only files under <").append(pathAllowed.toPathString()).append(">is allowed");
      }
    });
  }

  public static FileSystem readOnlyFileSystem(FileSystem fileSystem) {
    return new ControlledFileSystem(fileSystem, new PermissionController() {

      public boolean writeAllowed(TPath path) {
        return false;
      }

      public StringBuffer describe(StringBuffer buffer) {
        return buffer.append("read only file system");
      }
    });
  }

  public static abstract class PermissionController implements Controller {
    public abstract boolean writeAllowed(TPath path);

    public void writeOperationControl(TPath path) throws PermissionDeniedException {
      if (!writeAllowed(path)) {
        StringBuffer buffer = new StringBuffer("permission denied:<").append(path.toPathString()).append(">");
        throw new PermissionDeniedException(path, describe(buffer).toString());
      }
    }

    public void readOperationControl(TPath path) {
    }

    public abstract StringBuffer describe(StringBuffer buffer);
  }

  public static interface Controller {
    public void writeOperationControl(TPath path) throws TIoException;

    public void readOperationControl(TPath path) throws TIoException;
  }

}
