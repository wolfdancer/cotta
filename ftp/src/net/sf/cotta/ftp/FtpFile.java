package net.sf.cotta.ftp;

import net.sf.cotta.TPath;

public interface FtpFile {

  TPath getPath();

  FtpFileType getFileType();

  long getSize();
}
