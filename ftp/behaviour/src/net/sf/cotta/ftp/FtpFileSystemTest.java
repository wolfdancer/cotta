package net.sf.cotta.ftp;

import net.sf.cotta.system.FileSystem;
import net.sf.cotta.TIoException;
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

  public void testTellFileExistsOrNot() throws TIoException {
    ensure.that(fileSystem.fileExists(_("/testFile"))).isFalse();
    ftpServerFileSystem.add(new FileEntry("/testFile"));
    ensure.that(fileSystem.fileExists(_("/testFile"))).isTrue();
  }

  public void testTellDirExistsOrNot() throws IOException {
    ensure.that(fileSystem.dirExists(_("/"))).isTrue();
    ensure.that(fileSystem.dirExists(_("/abc"))).isFalse();
    ftpClient.makeDirectory("abc");
    ensure.that(fileSystem.dirExists(_("/abc"))).isTrue();
    ftpClient.changeWorkingDirectory("abc");
    ftpClient.makeDirectory("def");
    ensure.that(fileSystem.dirExists(_("/abc/def"))).isTrue();
  }

  public void testLeaveZeroByteFileAfterFileCreated() throws IOException {
    fileSystem.createFile(_("hello"));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ftpClient.retrieveFile("/hello", outputStream);
    ensure.that(outputStream.toByteArray().length).eq(0);
  }

  public void testBeAbleToDeleteFile() throws IOException {
    fileSystem.createFile(_("hello"));
    fileSystem.deleteFile(_("/hello"));
    ensure.that(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToCreateDirectory() throws IOException {
    fileSystem.createDir(_("hello"));
    fileSystem.createDir(_("hello/world"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("world");
  }

  public void testNotCountFileWhenListingDir() throws IOException {
    fileSystem.createFile(_("hello"));
    ensure.that(fileSystem.list(_("/")).dirs()).isEmpty();
    fileSystem.createDir(_("abc"));
    ensure.that(fileSystem.list(_(".")).dirs()).eq(_("abc"));
  }

  public void testNotCountDirWhenListingFile() throws IOException {
    fileSystem.createDir(_("abc"));
    ensure.that(fileSystem.list(_("/")).files()).isEmpty();
    fileSystem.createFile(_("hello"));
    ensure.that(fileSystem.list(_(".")).files()).eq(_("hello"));
  }

  public void testBeAbleToDeleteDirectory() throws IOException {
    fileSystem.createDir(_("abc"));
    fileSystem.deleteDirectory(_("abc"));
    ensure.that(ftpClient.listNames().length).eq(0);
  }

  public void testBeAbleToMoveFile() throws IOException {
    fileSystem.createFile(_("abc"));
    fileSystem.createDir(_("hello"));
    fileSystem.moveFile(_("abc"), _("hello/abc"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("abc");
  }

  public void testBeAbleToMoveDirectory() throws IOException {
    fileSystem.createDir(_("abc"));
    fileSystem.createDir(_("hello"));
    fileSystem.moveDirectory(_("abc"), _("hello/abc"));
    ensure.that(ftpClient.listNames()[0]).eq("hello");
    ensure.that(ftpClient.listNames("hello")[0]).eq("abc");
  }

  public void testBeAbleToTellFileLength() throws IOException {
    fileSystem.createFile(_("hello"));
    ensure.that(fileSystem.fileLength(_("hello"))).eq(0);
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
