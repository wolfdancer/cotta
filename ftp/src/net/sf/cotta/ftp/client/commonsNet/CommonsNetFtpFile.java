package net.sf.cotta.ftp.client.commonsNet;

import net.sf.cotta.TPath;
import net.sf.cotta.ftp.FtpFile;
import net.sf.cotta.ftp.FtpFileType;
import org.apache.commons.net.ftp.FTPFile;

final class CommonsNetFtpFile implements FtpFile {

  private final TPath path;
  private final FTPFile ftpFile;

  public CommonsNetFtpFile(TPath path, FTPFile ftpFile) {
    this.path = path;
    this.ftpFile = ftpFile;
  }

  public final TPath getPath() {
    return path;
  }

  public final FtpFileType getFileType() {
    if (ftpFile.isDirectory()) {
      return FtpFileType.DIRECTORY;
    }
    if (ftpFile.isFile()) {
      return FtpFileType.FILE;
    }
    if (ftpFile.isSymbolicLink()) {
      return FtpFileType.SYMBOLIC_LINK;
    }
    return FtpFileType.UNKNOWN;
  }

  public final long getSize() {
    return ftpFile.getSize();
  }
}
