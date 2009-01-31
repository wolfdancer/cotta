package net.sf.cotta.ftp.client.commonsNet;

import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TPath;
import net.sf.cotta.ftp.FtpTestCase;
import net.sf.cotta.test.assertion.CodeBlock;

import java.io.IOException;

public class CommonsNetFtpClientTest extends FtpTestCase {
  public void testRetrieveInputStream() throws IOException {
    final CommonsNetFtpClient netFtpClient = new CommonsNetFtpClient(ftpClient);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        netFtpClient.retrieve(TPath.parse("nothere"));
      }
    }).throwsException(TFileNotFoundException.class);
  }
}
