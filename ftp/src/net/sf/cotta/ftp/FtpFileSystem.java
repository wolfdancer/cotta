package net.sf.cotta.ftp;

import net.sf.cotta.FileSystem;
import net.sf.cotta.PathContent;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.ftp.client.commonsNet.CommonsNetFtpClient;
import net.sf.cotta.io.OutputMode;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class FtpFileSystem implements FileSystem {

  private final FtpClient ftpClient;
  private static final TPath ROOT_DIR = TPath.parse("/");
  private static final TPath CURRENT_DIR = TPath.parse(".");

  public FtpFileSystem(FtpClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  public FtpFileSystem(FTPClient ftpClient) {
    this(new CommonsNetFtpClient(ftpClient));
  }

  public final boolean fileExists(TPath path) {
    return pathExists(path);
  }

  public final void createFile(TPath path) throws TIoException {
    try {
      OutputStream outputStream = ftpClient.store(path);
      try {
        outputStream.write(new byte[0]);
      } finally {
        outputStream.close();
      }
    } catch (IOException e) {
      throw new TIoException(path, "Create file failed", e);
    }
  }

  public final void deleteFile(TPath path) throws TIoException {
    try {
      ftpClient.delete(path);
    } catch (IOException e) {
      throw new TIoException(path, "Delete file failed", e);
    }
  }

  public boolean dirExists(TPath path) {
    return isConstantPath(path) || pathExists(path);
  }

  public final void createDir(TPath path) throws TIoException {
    try {
      ftpClient.makeDirectory(path);
    } catch (IOException e) {
      throw new TIoException(path, "Create dir failed", e);
    }
  }

  public final InputStream createInputStream(final TPath path) throws TIoException {
    try {
      return ftpClient.retrieve(path);
    } catch (TIoException e) {
      throw e;
    } catch (IOException e) {
      throw new TIoException(path, "Failed to create input stream", e);
    }
  }

  public final OutputStream createOutputStream(TPath path, OutputMode mode) throws TIoException {
    try {
      if (mode.isOverwrite()) {
        return ftpClient.store(path);
      }
      if (mode.isAppend()) {
        return ftpClient.append(path);
      }
      throw new FtpFileSystemException("Failed to create output stream, as mode unkown");
    } catch (IOException e) {
      throw new TIoException(path, "Failed to create output stream", e);
    }
  }

  public final FileChannel createOutputChannel(TPath path, OutputStream outputStream) throws TIoException {
    throw new UnsupportedOperationException("FtpFileSystem");
  }

  public final void deleteDirectory(TPath path) throws TIoException {
    try {
      ftpClient.delete(path);
    } catch (IOException e) {
      throw new TIoException(path, "Delete directory faild", e);
    }
  }

  public final void moveFile(TPath source, TPath destination) throws TIoException {
    try {
      ftpClient.rename(source, destination);
    } catch (IOException e) {
      throw new TIoException(source, "Moving file failed, target <" + destination.toPathString() + ">", e);
    }
  }

  public final void moveDirectory(TPath source, TPath destination) throws TIoException {
    try {
      ftpClient.rename(source, destination);
    } catch (IOException e) {
      throw new TIoException(source, "Moving directory failed, target <" + destination.toPathString() + ">", e);
    }
  }

  public final String pathString(TPath path) {
    return path.toPathString();
  }

  public final long fileLength(TPath path) {
    FtpFile[] ftpFiles = listFtpDirectory(path.parent());
    for (FtpFile ftpFile : ftpFiles) {
      if (ftpFile.getPath().equals(path)) {
        return ftpFile.getSize();
      }
    }
    throw new FtpFileSystemException("get file length failed, as we can not find it on its parent directory");
  }

  public final File toJavaFile(TPath path) {
    throw new UnsupportedOperationException("FtpFileSystem");
  }

  public final String toCanonicalPath(TPath path) {
    throw new UnsupportedOperationException("FtpFileSystem");
  }

  public final FileChannel createInputChannel(TPath path) throws TIoException {
    throw new UnsupportedOperationException("FtpFileSystem");
  }

  public long fileLastModified(TPath path) {
    throw new UnsupportedOperationException("FtpFileSystem");
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

  private boolean isConstantPath(TPath path) {
    return path.equals(ROOT_DIR) || path.equals(CURRENT_DIR);
  }

  private boolean pathExists(TPath path) {
    FtpFile[] existingFiles = listFtpDirectory(path.parent());
    if (existingFiles == null) {
      return false;
    }
    for (FtpFile existingFile : existingFiles) {
      if (existingFile.getPath().equals(path)) {
        return true;
      }
    }
    return false;
  }

  public PathContent list(TPath path) throws TIoException {
    FtpFile[] ftpFiles = listFtpDirectory(path);
    PathContent content = new PathContent(ftpFiles.length);
    for (FtpFile ftpFile : ftpFiles) {
      if (FtpFileType.DIRECTORY.equals(ftpFile.getFileType())) {
        content.addDirectoryPath(ftpFile.getPath());
      } else if (FtpFileType.FILE.equals(ftpFile.getFileType())) {
        content.addFilePath(ftpFile.getPath());
      }
    }
    return content;
  }

  private FtpFile[] listFtpDirectory(TPath path) {
    try {
      return ftpClient.list(path);
    } catch (IOException e) {
      throw new FtpFileSystemException(e);
    }
  }

  private class FtpFileSystemException extends RuntimeException {

    public FtpFileSystemException(IOException cause) {
      super(cause);
    }

    public FtpFileSystemException(String message) {
      super(message);
    }
  }
}
