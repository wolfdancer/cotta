package net.sf.cotta.ftp;

import net.sf.cotta.test.ForFixture;
import net.sf.cotta.test.TestCase;
import org.apache.commons.net.ftp.FTPClient;
import org.mockftpserver.fake.filesystem.FileSystem;

@Ftp
public abstract class FtpTestCase extends TestCase {
  @ForFixture
  public FTPClient ftpClient;
  @ForFixture
  public FileSystem ftpServerFileSystem;
}