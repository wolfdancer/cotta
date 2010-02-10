package net.sf.cotta.physical;

import net.sf.cotta.*;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.utils.PlatformInfoUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class PhysicalFileSystemTest extends PhysicalFileSystemTestCase {

  public void testDirExistsReturnsFalseForFile() throws TIoException {
    TPath path = TPath.parse("tmp/test.txt");
    fileSystem.createDir(path.parent());
    fileSystem.createFile(path);
    ensure.that(fileSystem.dirExists(path)).eq(false);
    ensure.that(fileSystem.fileExists(path)).eq(true);
  }

  public void testFileExistsReturnsFalseForDirectory() throws TIoException {
    TPath path = TPath.parse("tmp/dir");
    fileSystem.createDir(path.parent());
    fileSystem.createDir(path);
    ensure.that(fileSystem.dirExists(path)).eq(true);
    ensure.that(fileSystem.fileExists(path)).eq(false);
  }

  public void testDeleteThrowsExceptionWhenFailed() {
    final TPath path = TPath.parse("tmp/dir");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.deleteDirectory(path);
      }
    }).throwsException(TIoException.class);
  }

  public void testListingThrowsExceptionIfDirDoesNotExist() {
    final TPath path = TPath.parse("tmp/dir");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.list(path);
      }
    }).throwsException(TIoException.class);
  }

  public void testMoveFileThrowsExceptionWhenFailed() {
    final TPath from = TPath.parse("tmp/from.txt");
    final TPath to = TPath.parse("tmp/to.txt");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.moveFile(from, to);
      }
    }).throwsException(TIoException.class);
  }

  public void testMoveDirectoryThrowsExceptionWhenFailed() {
    final TPath from = TPath.parse("tmp/from.txt");
    final TPath to = TPath.parse("tmp/to.txt");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.moveDirectory(from, to);
      }
    }).throwsException(TIoException.class);
  }

  public void testMoveDirectory() throws TIoException {
    final TPath from = TPath.parse("tmp/from.txt");
    final TPath to = TPath.parse("tmp/to.txt");
    fileSystem.createDir(from.parent());
    fileSystem.createDir(from);
    fileSystem.moveDirectory(from, to);
    ensure.that(fileSystem.dirExists(from)).eq(false);
    ensure.that(fileSystem.dirExists(to)).eq(true);
  }

  public void testCreateAndDeleteDirectory() throws Exception {
    TPath tmp = TPath.parse("tmp");
    fileSystem.createDir(tmp);
    ensure.that(fileSystem.dirExists(tmp)).eq(true);
    fileSystem.deleteDirectory(tmp);
    ensure.that(fileSystem.dirExists(tmp)).eq(false);
  }

  public void testListEmptyDirectory() throws Exception {
    TPath tmp = TPath.parse("tmp");
    fileSystem.createDir(tmp);
    PathContent content = fileSystem.list(tmp);
    ensure.that(content.files()).isEmpty();
    ensure.that(content.dirs()).isEmpty();
  }

  public void testCreateDirectory() throws Exception {
    TPath path = TPath.parse("tmp/tmp");
    fileSystem.createDir(path);
    ensure.that(fileSystem.dirExists(TPath.parse("tmp/tmp"))).eq(true);
    PathContent content = fileSystem.list(TPath.parse("tmp"));
    ensure.that(content.dirs()).eq(path);
    ensure.that(content.files()).isEmpty();
  }

  public void testCreateFileAndItsParent() throws Exception {
    TPath fileToCreate = TPath.parse("tmp/test.txt");
    fileSystem.createDir(fileToCreate.parent());
    fileSystem.createFile(fileToCreate);
    ensure.that(fileSystem.fileExists(fileToCreate)).eq(true);
    ensure.that(fileSystem.dirExists(fileToCreate.parent())).eq(true);
    PathContent content = fileSystem.list(fileToCreate.parent());
    ensure.that(content.files()).eq(fileToCreate);
  }

  public void testCreateParentDirectoryWhenCreatingOutputStream() throws Exception {
    TPath fileToCreate = TPath.parse("tmp/ttt.txt");
    OutputStream stream = fileSystem.createOutputStream(fileToCreate, OutputMode.APPEND);
    registerResource(stream);
    stream.write("test".getBytes());
    stream.close();
    ensure.that(fileSystem.fileExists(fileToCreate));
  }

  public void testCreateOutputStreamThrowsExceptionWhenNotFound() throws IOException {
    final TPath fileToCreate = TPath.parse("tmp/ttt.txt");
    fileSystem.createDir(fileToCreate.parent());
    fileSystem.createDir(fileToCreate);
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createOutputStream(fileToCreate, OutputMode.OVERWRITE);
      }
    }).throwsException(TIoException.class);
  }

  public void testMoveFile() throws Exception {
    String content = "move file directly";
    TPath source = TPath.parse("tmp/source.txt");
    TPath dest = TPath.parse("tmp/target.txt");
    new TFile(new TFileFactory(fileSystem), source).save(content);
    fileSystem.moveFile(source, dest);
    ensure.that(fileSystem.fileExists(source)).eq(false);
    ensure.that(new TFile(new TFileFactory(fileSystem), dest).load()).eq(content);
  }

  public void testGetFilePath() throws Exception {
    TPath path = TPath.parse("tmp/source.txt");
    String expected = ".\\tmp\\source.txt";
    if (!PlatformInfoUtil.isWindows()) {
      expected = expected.replace('\\', '/');
    }
    ensure.that(fileSystem.pathString(path)).eq(expected);
  }

  public void testGetFileLength() throws Exception {
    TPath path = TPath.parse("tmp/source.txt");
    String content = "my content";
    new TFile(new TFileFactory(fileSystem), path).save(content);
    ensure.that(fileSystem.fileLength(path)).eq(content.getBytes().length);
  }

  public void testGetFileLastModified() throws TIoException {
    TPath path = TPath.parse("tmp/source.txt");
    String content = "content";
    Date timeBeforeCreation = new Date();
    new TFile(new TFileFactory(fileSystem), path).save(content);
    ensure.that(fileSystem.fileLastModified(path) / 1000).ge(timeBeforeCreation.getTime() / 1000);
  }

  public void testThrowExceptionInCaseListReturnsNull() throws Exception {
    final TPath path = TPath.parse("tmp/directory");
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.list(path);
      }
    }).throwsException(TIoException.class);
  }

  public void testGetJavaFile() {
    File file = new File("./test/test");
    ensure.that(fileSystem.toJavaFile(TPath.parse("test/test"))).eq(file);
  }

  public void testComparingPath() {
    TPath one = TPath.parse("/one/two/test1.txt");
    TPath two = TPath.parse("/one/two/test2.txt");
    ensure.that(fileSystem.compare(one, two)).eq(one.toPathString().compareTo(two.toPathString()));
  }

  public void testHashCode() {
    TPath path = TPath.parse("one/two/path.txt");
    ensure.that(fileSystem.hashCode(path)).eq(new File(path.toPathString()).hashCode());
  }

  public void testCreateFileFailure() throws TIoException {
    final TPath path = TPath.parse("tmp/test.txt");
    fileSystem.createDir(TPath.parse("tmp"));
    fileSystem.createFile(path);
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    }).throwsException(TIoException.class);
  }

  public void testDeleteFailure() throws TIoException {
    final TPath path = TPath.parse("tmp/test.txt");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.deleteFile(path);
      }
    }).throwsException(TIoException.class);
  }
}
