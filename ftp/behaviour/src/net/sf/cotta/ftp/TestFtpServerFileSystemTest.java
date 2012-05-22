package net.sf.cotta.ftp;

import net.sf.cotta.TFileFactory;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.test.assertion.CodeBlock;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TestFtpServerFileSystemTest extends FtpTestCase {
  public void testBeAtRootAndNoFileInitially() throws InterruptedException, IOException {
    String workingDirectory = ftpClient.printWorkingDirectory();
    ensure.that(workingDirectory).eq("/");
    ensure.that(ftpClient.listNames()).eq();
  }

  public void testBeAbleToListFiles() throws IOException {
    ftpServerFileSystem.add(new FileEntry("/testFile"));
    String[] listedNames = ftpClient.listNames();
    ensure.that(listedNames).eq("testFile");
  }

  public void testBeAbleToListDirectories() throws IOException {
    ftpServerFileSystem.add(new DirectoryEntry("/testDir"));
    ensure.that(ftpClient.listNames()).eq("testDir");
  }

  public void testBeAbleToMakeDirectory() throws IOException {
    ensure.that(ftpClient.makeDirectory("testDir")).isTrue();
    ensure.that(((DirectoryEntry) ftpServerFileSystem.listFiles("/").get(0)).getPath()).eq("/testDir");
  }

  public void testBeAbleToRemoveDirectory() throws IOException {
    ftpClient.makeDirectory("testDir");
    ftpClient.removeDirectory("testDir");
    ensure.that(ftpServerFileSystem.listFiles("/")).eq();
  }

  public void testBeAbleToChangeWorkingDirectory() throws IOException {
    ftpClient.makeDirectory("testDir");
    ftpClient.cwd("testDir");
    String workingDirectory = ftpClient.printWorkingDirectory();
    ensure.that(workingDirectory).eq("/testDir");
  }

  public void testBeAbleToStoreFile() throws IOException {
    String fileContent = "Doogie Howser";
    ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
    ftpClient.storeFile("testFile", new ByteArrayInputStream(fileContent.getBytes()));
    InputStream inputStream = ((FileEntry) ftpServerFileSystem.getEntry("/testFile")).createInputStream();
    try {
      ensure.that(IOUtils.toString(inputStream)).eq(fileContent);
    } finally {
      inputStream.close();
    }
  }

  public void testFileNotFoundOnServer() throws IOException {
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        TFileFactory factory = new TFileFactory(new FtpFileSystem(ftpClient));
        factory.file("notexist").load();
      }
    }).throwsException(TFileNotFoundException.class);
  }

  public void testBeAbleToRetrieveFile() throws IOException {
    String fileContent = "Hello, world!";
    ftpServerFileSystem.add(new FileEntry("/testFile", fileContent));
    ByteArrayOutputStream fileContentReadBuffer = new ByteArrayOutputStream();
    ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
    ftpClient.retrieveFile("testFile", fileContentReadBuffer);
    ensure.that(fileContentReadBuffer.toString()).eq(fileContent);
  }

  public void testBeAbleToRenameFile() throws IOException {
    ftpServerFileSystem.add(new FileEntry("/testFile"));
    ftpClient.rename("testFile", "renamedTestFile");
    ensure.that(((FileEntry) ftpServerFileSystem.listFiles("/").get(0)).getPath()).eq("/renamedTestFile");
  }

  public void testBeAbleToRenameDirectory() throws IOException {
    ftpServerFileSystem.add(new DirectoryEntry("/testDir"));
    ftpClient.rename("testDir", "renamedTestDir");
    ensure.that(((DirectoryEntry) ftpServerFileSystem.listFiles("/").get(0)).getPath()).eq("/renamedTestDir");
  }

  public void testBeAbleToChangeToParentDirectory() throws IOException {
    ftpClient.makeDirectory("TestChangeToPaqrentDirectory");
    ftpClient.cwd("TestChangeToPaqrentDirectory");
    ftpClient.changeToParentDirectory();
    ensure.that(ftpClient.printWorkingDirectory()).eq("/");
  }

  private byte[] createTestFileContent() {
    ByteArrayOutputStream fileContentBuffer = new ByteArrayOutputStream();
    for (int i = 1; i < 1000; i++) {
      fileContentBuffer.write(i % 255);
    }
    return fileContentBuffer.toByteArray();
  }
}
