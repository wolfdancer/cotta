package net.sf.cotta.ftp;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.io.InputManager;
import net.sf.cotta.io.InputProcessor;
import net.sf.cotta.io.OutputManager;
import net.sf.cotta.io.OutputProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class TestFtpServerFileSystemTest extends FtpTestCase {
  public void testBeAtRootAndNoFileInitially() throws InterruptedException, IOException {
    String workingDirectory = ftpClient.printWorkingDirectory();
    ensure.string(workingDirectory).eq("/");
    String[] listedNames = ftpClient.listNames();
    ensure.array(listedNames).eq();
  }

  public void testBeAbleToListFiles() throws IOException {
    rootDir.file("testFile").ensureExists();
    String[] listedNames = ftpClient.listNames();
    ensure.array(listedNames).eq("testFile");
  }

  public void testBeAbleToListDirectories() throws IOException {
    rootDir.file("testDir").ensureExists();
    String[] listedNames = ftpClient.listNames();
    ensure.array(listedNames).eq("testDir");
  }

  public void testBeAbleToMakeDirectory() throws IOException {
    boolean success = ftpClient.makeDirectory("testDir");
    ensure.booleanValue(success).isTrue();
    TDirectory[] listedDirs = rootDir.listDirs();
    ensure.array(listedDirs).eq(rootDir.dir("testDir"));
  }

  public void testBeAbleToRemoveDirectory() throws IOException {
    ftpClient.makeDirectory("testDir");
    ftpClient.removeDirectory("testDir");
    ensure.array(rootDir.listDirs()).eq();
  }

  public void testBeAbleToChangeWorkingDirectory() throws IOException {
    ftpClient.makeDirectory("testDir");
    ftpClient.cwd("testDir");
    String workingDirectory = ftpClient.printWorkingDirectory();
    ensure.string(workingDirectory).eq("/testDir");
  }

  public void testBeAbleToStoreFile() throws IOException {
    final byte[] fileContent = createTestFileContent();
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    ftpClient.storeFile("testFile", new ByteArrayInputStream(fileContent));
    rootDir.file("testFile").read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        byte[] fileContentRead = IOUtils.toByteArray(inputManager.inputStream());
        ensure.bytes(fileContentRead).eq(fileContentRead);
      }
    });
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
    final byte[] fileContent = createTestFileContent();
    rootDir.file("testFile").write(new OutputProcessor() {
      public void process(OutputManager outputManager) throws IOException {
        IOUtils.write(fileContent, outputManager.outputStream());
      }
    });
    ByteArrayOutputStream fileContentReadBuffer = new ByteArrayOutputStream();
    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    ftpClient.retrieveFile("testFile", fileContentReadBuffer);
    ensure.bytes(fileContentReadBuffer.toByteArray()).eq(fileContent);
  }

  public void testBeAbleToRenameFile() throws IOException {
    rootDir.file("testFile").save("");
    ftpClient.rename("testFile", "renamedTestFile");
    ensure.array(rootDir.listFiles()).eq(rootDir.file("renamedTestFile"));
  }

  public void testBeAbleToRenameDirectory() throws IOException {
    rootDir.dir("testDir").ensureExists();
    ftpClient.rename("testDir", "renamedTestDir");
    ensure.array(rootDir.listDirs()).eq(rootDir.dir("renamedTestDir"));
  }

  public void testBeAbleToChangeToParentDirectory() throws IOException {
    ftpClient.makeDirectory("TestChangeToPaqrentDirectory");
    ftpClient.cwd("TestChangeToPaqrentDirectory");
    ftpClient.changeToParentDirectory();
    ensure.string(ftpClient.printWorkingDirectory()).eq("/");
  }

  private byte[] createTestFileContent() {
    ByteArrayOutputStream fileContentBuffer = new ByteArrayOutputStream();
    for (int i = 1; i < 1000; i++) {
      fileContentBuffer.write(i % 255);
    }
    return fileContentBuffer.toByteArray();
  }
}
