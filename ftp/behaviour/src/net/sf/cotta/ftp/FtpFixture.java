package net.sf.cotta.ftp;

import net.sf.cotta.test.TestCase;
import net.sf.cotta.test.TestFixture;
import org.apache.commons.net.ftp.FTPClient;
import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.command.CommandNames;
import org.mockftpserver.core.command.ReplyCodes;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.command.DeleCommandHandler;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.IOException;

public class FtpFixture implements TestFixture {
  private FakeFtpServer ftpServer;

  public void setUp() {
    ftpServer = new FakeFtpServer();
    ftpServer.setServerControlPort(8021);
    ftpServer.addUserAccount(new UserAccount("anonymous", "test@test.com", "/"));
    ftpServer.start();
  }

  public void tearDown() {
    ftpServer.stop();
  }

  public void beforeMethod(TestCase testCase) throws IOException {
    UnixFakeFileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.setCreateParentDirectoriesAutomatically(true);
    ftpServer.setFileSystem(fileSystem);
    // fake ftp server doesn't delete directories
    ftpServer.setCommandHandler(CommandNames.DELE, new DeleCommandHandler() {
      @Override
      protected void handle(Command command, Session session) {
        verifyLoggedIn(session);
        String path = getRealPath(session, command.getRequiredParameter(0));

        this.replyCodeForFileSystemException = ReplyCodes.READ_FILE_ERROR;
        if (getFileSystem().isDirectory(path)) {
          // User must have write permission to the parent directory
          verifyWritePermission(session, getFileSystem().getParent(path));

          getFileSystem().delete(path);
          sendReply(session, ReplyCodes.DELE_OK, "dele", list(path));
        } else {
          super.handle(command, session);
        }
      }
    });
    fileSystem.add(new DirectoryEntry("/"));
    testCase.inject(fileSystem);
    FTPClient ftpClient = new FTPClient();
    ftpClient.connect("localhost", 8021);
    ftpClient.login("anonymous", "test@test.com");
    testCase.inject(ftpClient);
  }

  public void afterMethod(TestCase testCase) {
  }
}
