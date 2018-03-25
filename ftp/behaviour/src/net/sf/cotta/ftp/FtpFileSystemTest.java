package net.sf.cotta.ftp;

import net.sf.cotta.system.FileSystem;
import net.sf.cotta.TPath;
import net.sf.cotta.ftp.client.commonsNet.CommonsNetFtpClient;
import net.sf.cotta.io.OutputMode;
import org.apache.commons.io.IOUtils;
import org.mockftpserver.fake.filesystem.FileEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FtpFileSystemTest extends FtpTestCase {

  private FileSystem fileSystem;

  public void beforeMethod() throws Exception {
    super.beforeMethod();
    fileSystem = new FtpFileSystem(new CommonsNetFtpClient(ftpClient));
  }

  public void testTellFileExistsOrNot() {
    ensure.that(fileSystem.fileExists(path("/testFile"))).isFalse();
    ftpServerFileSystem.add(new FileEntry("/testFile"));
    ensure.that(fileSystem.fileExists(path("/testFile"))).isTrue();
  }

  public void testTellDirExistsOrNot() throws IOException {
    ensure.that(fileSystem.dirExists(path("/"))).isTrue();
    ensure.that(fileSystem.dirExists(path("/abc"))).isFalse();
    ftpClient.makeDirectory("abc");
    ensure.that(fileSystem.dirExists(path("/abc"))).isTrue();
    ftpClient.changeWorkingDirectory("abc");
    ftpClient.makeDirectory("def");
    ensure.that(fileSystem.dirExists(path("/abc/def"))).isTrue();
  }

  public void testLeaveZeroByteFileAfterFileCreated() throws IOException {
    fileSystem.createFile(path("hello"));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ftpClient.retrieveFile("/hello", outputStream);
    ensure.that(outputStream.toByteArray().length).eq(0);
  }

  public void testBeAbleToDeleteFile() throws IOException {
    fileSystem.createFile(path("hello"));
    fileSystem.deleteFile(path("/hello"));
    ensure.that(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToCreateDirectory() throws IOException {
    fileSystem.createDir(path("hello"));
    fileSystem.createDir(path("hello/world"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("world");
  }

  public void testNotCountFileWhenListingDir() throws IOException {
    fileSystem.createFile(path("hello"));
    ensure.that(fileSystem.list(path("/")).dirs()).isEmpty();
    fileSystem.createDir(path("abc"));
    ensure.that(fileSystem.list(path(".")).dirs()).eq(path("abc"));
  }

  public void testNotCountDirWhenListingFile() throws IOException {
    fileSystem.createDir(path("abc"));
    ensure.that(fileSystem.list(path("/")).files()).isEmpty();
    fileSystem.createFile(path("hello"));
    ensure.that(fileSystem.list(path(".")).files()).eq(path("hello"));
  }

  public void testBeAbleToDeleteDirectory() throws IOException {
    fileSystem.createDir(path("abc"));
    fileSystem.deleteDirectory(path("abc"));
    ensure.that(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToMoveFile() throws IOException {
    fileSystem.createFile(path("abc"));
    fileSystem.createDir(path("hello"));
    fileSystem.moveFile(path("abc"), path("hello/abc"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("abc");
  }

  public void testBeAbleToMoveDirectory() throws IOException {
    fileSystem.createDir(path("abc"));
    fileSystem.createDir(path("hello"));
    fileSystem.moveDirectory(path("abc"), path("hello/abc"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("abc");
  }

  public void testBeAbleToTellFileLength() throws IOException {
    fileSystem.createFile(path("hello"));
    ensure.that(fileSystem.fileLength(path("hello"))).eq(0);
  }

  public void testBeAbleToDownloadAndUpload() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(path("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    InputStream inputStream = fileSystem.createInputStream(path("hello"));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, buffer);
    inputStream.close();
    ensure.that(buffer.toByteArray()).eq(new byte[]{1, 2, 3});
  }

  public void testAllowClosingOutputStreamTwice() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(path("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    outputStream.close();
  }

  public void testAllowClosingInputStreamTwice() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(path("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    InputStream inputStream = fileSystem.createInputStream(path("hello"));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, buffer);
    inputStream.close();
    inputStream.close();
  }


  private TPath path(String pathString) {
    return TPath.parse(pathString);
  }


}
