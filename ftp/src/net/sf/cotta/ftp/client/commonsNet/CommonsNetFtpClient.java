package net.sf.cotta.ftp.client.commonsNet;

import net.sf.cotta.TPath;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.ftp.FtpClient;
import net.sf.cotta.ftp.FtpFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.URI;

public final class CommonsNetFtpClient implements FtpClient {

  private FTPClient ftpClient;

  public CommonsNetFtpClient(FTPClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  public final FtpFile[] list(TPath path) throws IOException {
    FTPFile[] rawFtpFiles = ftpClient.listFiles(path.toPathString());
    FtpFile[] ftpFiles = new FtpFile[rawFtpFiles.length];
    for (int i = 0; i < rawFtpFiles.length; i++) {
      FTPFile rawFtpFile = rawFtpFiles[i];
      ftpFiles[i] = new CommonsNetFtpFile(path.join(rawFtpFile.getName()), rawFtpFile);
    }
    return ftpFiles;
  }

  public final OutputStream store(TPath path) throws IOException {
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    return wrapOutputStream(ftpClient.storeFileStream(path.toPathString()));
  }

  public final void delete(TPath path) throws IOException {
    ftpClient.deleteFile(path.toPathString());
  }

  public final void makeDirectory(TPath path) throws IOException {
    ftpClient.makeDirectory(path.toPathString());
  }

  public final void rename(TPath source, TPath destination) throws IOException {
    ftpClient.rename(source.toPathString(), destination.toPathString());
  }

  public final InputStream retrieve(TPath path) throws IOException {
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    InputStream inputStream = ftpClient.retrieveFileStream(path.toPathString());
    if (inputStream == null) {
      throw new TFileNotFoundException(path);
    }
    return wrapInputStream(inputStream);
  }

  public final OutputStream append(TPath path) throws IOException {
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    return wrapOutputStream(ftpClient.appendFileStream(path.toPathString()));
  }

  private InputStream wrapInputStream(InputStream inputStream) {
    final PendingCommandTerminator terminator = new PendingCommandTerminator(ftpClient);
    return new FilterInputStream(inputStream) {
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          terminator.terminate();
        }
      }
    };
  }

  private OutputStream wrapOutputStream(OutputStream outputStream) throws IOException {
    final PendingCommandTerminator terminator = new PendingCommandTerminator(ftpClient);
    return new FilterOutputStream(outputStream) {
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          terminator.terminate();
        }
      }
    };
  }

  private class PendingCommandTerminator {
    private final FTPClient ftpClient;
    private boolean terminated;

    public PendingCommandTerminator(FTPClient ftpClient) {
      this.ftpClient = ftpClient;
    }

    public void terminate() throws IOException {
      if (!this.terminated) {
        ftpClient.completePendingCommand();
        this.terminated = true;
      }
    }
  }
}
