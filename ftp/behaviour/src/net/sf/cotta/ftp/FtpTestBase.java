package net.sf.cotta.ftp;

import net.sf.cotta.TDirectory;
import net.sf.cotta.test.ForFixture;
import net.sf.cotta.test.TestBase;
import org.apache.commons.net.ftp.FTPClient;

@Ftp
public class FtpTestBase extends TestBase {
  @ForFixture
  public FTPClient ftpClient;
  @ForFixture
  public TDirectory rootDir;

}
