package net.sf.cotta.ftp;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestCase;
import net.sf.cotta.test.TestFixture;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FtpFixture implements TestFixture {
  private TestFtpServer ftpServer;

  public void setUp() {
    ftpServer = new TestFtpServer();
    ftpServer.start();
//    Thread.sleep(1000);
  }

  public void tearDown() {
    ftpServer.stop();
  }

  public void beforeMethod(TestCase testCase) throws IOException {
    TFileFactory fileFactory = new TFileFactory(new InMemoryFileSystem());
    ftpServer.cleanFileSystem(fileFactory);
    TDirectory rootDir = fileFactory.dir("/");
    testCase.inject(rootDir);
    FTPClient ftpClient = new FTPClient();
    ftpClient.connect("localhost", 8021);
    ftpClient.login("anonymous", "test@test.com");
    testCase.inject(ftpClient);
  }

  public void afterMethod(TestCase testCase) {
  }
}
