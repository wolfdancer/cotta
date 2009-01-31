package net.sf.cotta.ftp;

import net.sf.cotta.TDirectory;
import net.sf.cotta.test.ForFixture;
import net.sf.cotta.test.TestCase;
import org.apache.commons.net.ftp.FTPClient;

@Ftp
public class FtpTestCase extends TestCase {
  @ForFixture
  public FTPClient ftpClient;
  @ForFixture
  public TDirectory rootDir;

}
