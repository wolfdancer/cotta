package net.sf.cotta.ftp;

public final class FtpFileType {

  public static final FtpFileType FILE = new FtpFileType();
  public static final FtpFileType DIRECTORY = new FtpFileType();
  public static final FtpFileType SYMBOLIC_LINK = new FtpFileType();
  public static final FtpFileType UNKNOWN = new FtpFileType();

  private FtpFileType() {
  }
}
