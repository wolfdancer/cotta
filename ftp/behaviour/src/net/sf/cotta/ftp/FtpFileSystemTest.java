package net.sf.cotta.ftp;

import net.sf.cotta.FileSystem;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.ftp.client.commonsNet.CommonsNetFtpClient;
import net.sf.cotta.io.OutputMode;
import org.apache.commons.io.IOUtils;

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

  public void testTellFileExistsOrNot() throws TIoException {
    boolean exists = fileSystem.fileExists(_("/testFile"));
    ensure.booleanValue(exists).isFalse();
    rootDir.file("testFile").save("");
    exists = fileSystem.fileExists(_("/testFile"));
    ensure.booleanValue(exists).isTrue();
  }

  public void testTellDirExistsOrNot() throws IOException {
    ensure.booleanValue(fileSystem.dirExists(_("/"))).isTrue();
    ensure.booleanValue(fileSystem.dirExists(_("/abc"))).isFalse();
    ftpClient.makeDirectory("abc");
    ensure.booleanValue(fileSystem.dirExists(_("/abc"))).isTrue();
    ftpClient.changeWorkingDirectory("abc");
    ftpClient.makeDirectory("def");
    ensure.booleanValue(fileSystem.dirExists(_("/abc/def"))).isTrue();
  }

  public void testLeaveZeroByteFileAfterFileCreated() throws IOException {
    fileSystem.createFile(_("hello"));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ftpClient.retrieveFile("/hello", outputStream);
    ensure.integer(outputStream.toByteArray().length).eq(0);
  }

  public void testBeAbleToDeleteFile() throws IOException {
    fileSystem.createFile(_("hello"));
    fileSystem.deleteFile(_("/hello"));
    ensure.integer(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToCreateDirectory() throws IOException {
    fileSystem.createDir(_("hello/world"));
    ensure.string(ftpClient.listNames()[0]).eq("hello/");
    ensure.string(ftpClient.listNames("hello")[0]).eq("world/");
  }

  public void shoudlNotCountFileWhenListingDir() throws IOException {
    fileSystem.createFile(_("hello"));
    ensure.integer(fileSystem.listDirs(_("/")).length).eq(0);
    fileSystem.createDir(_("abc"));
    ensure.that(fileSystem.listDirs(_("."))).eq(_("abc"));
  }

  public void testNotCountDirWhenListingFile() throws IOException {
    fileSystem.createDir(_("abc"));
    ensure.integer(fileSystem.listFiles(_("/")).length).eq(0);
    fileSystem.createFile(_("hello"));
    ensure.that(fileSystem.listFiles(_("."))).eq(_("hello"));
  }

  public void testBeAbleToDeleteDirectory() throws IOException {
    fileSystem.createDir(_("abc"));
    fileSystem.deleteDirectory(_("abc"));
    ensure.integer(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToMoveFile() throws IOException {
    fileSystem.createFile(_("abc"));
    fileSystem.createDir(_("hello"));
    fileSystem.moveFile(_("abc"), _("hello/abc"));
    ensure.string(ftpClient.listNames()[0]).eq("hello/");
    ensure.string(ftpClient.listNames("hello")[0]).eq("abc");
  }

  public void testBeAbleToMoveDirectory() throws IOException {
    fileSystem.createDir(_("abc"));
    fileSystem.createDir(_("hello"));
    fileSystem.moveDirectory(_("abc"), _("hello/abc"));
    ensure.string(ftpClient.listNames()[0]).eq("hello/");
    ensure.string(ftpClient.listNames("hello")[0]).eq("abc/");
  }

  public void testBeAbleToTellFileLength() throws IOException {
    fileSystem.createFile(_("hello"));
    ensure.longValue(fileSystem.fileLength(_("hello"))).eq(0);
  }

  public void testBeAbleToDownloadAndUpload() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(_("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    InputStream inputStream = fileSystem.createInputStream(_("hello"));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, buffer);
    inputStream.close();
    ensure.that(buffer.toByteArray()).eq(new byte[]{1, 2, 3});
  }

  public void testAllowClosingOutputStreamTwice() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(_("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    outputStream.close();
  }

  public void testAllowClosingInputStreamTwice() throws IOException {
    OutputStream outputStream = fileSystem.createOutputStream(_("hello"), OutputMode.OVERWRITE);
    outputStream.write(new byte[]{1, 2, 3});
    outputStream.close();
    InputStream inputStream = fileSystem.createInputStream(_("hello"));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, buffer);
    inputStream.close();
    inputStream.close();
  }


  private TPath _(String pathString) {
    return TPath.parse(pathString);
  }


}
