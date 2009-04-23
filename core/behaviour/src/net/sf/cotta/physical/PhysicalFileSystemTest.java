package net.sf.cotta.physical;

import net.sf.cotta.TFile;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.test.assertion.CodeBlock;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

public class PhysicalFileSystemTest extends PhysicalFileSystemTestCase {

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
    ensure.that(fileSystem.listDirs(tmp).length).eq(0);
  }

  public void testCreateDirectory() throws Exception {
    fileSystem.createDir(TPath.parse("tmp/tmp"));
    ensure.that(fileSystem.dirExists(TPath.parse("tmp/tmp"))).eq(true);
    TPath[] sub = fileSystem.listDirs(TPath.parse("tmp"));
    ensure.that(sub.length).eq(1);
    ensure.that(sub[0]).eq(TPath.parse("tmp/tmp"));
  }

  public void testCreateFileAndItsParent() throws Exception {
    TPath fileToCreate = TPath.parse("tmp/test.txt");
    fileSystem.createDir(fileToCreate.parent());
    fileSystem.createFile(fileToCreate);
    ensure.that(fileSystem.fileExists(fileToCreate)).eq(true);
    ensure.that(fileSystem.dirExists(fileToCreate.parent())).eq(true);
    TPath[] files = fileSystem.listFiles(fileToCreate.parent());
    ensure.that(files.length).eq(1);
    ensure.that(files[0]).eq(fileToCreate);
  }

  public void testCreateParentDirectoryWhenCreatingOutputStream() throws Exception {
    TPath fileToCreate = TPath.parse("tmp/ttt.txt");
    OutputStream os = fileSystem.createOutputStream(fileToCreate, OutputMode.APPEND);
    registerResource(os);
    os.write("test".getBytes());
    os.close();
    ensure.that(fileSystem.fileExists(fileToCreate));
  }

  public void testAlwaysEqualToEachOther() throws Exception {
    ensure.that(new PhysicalFileSystem().equals(new PhysicalFileSystem())).eq(true);
    ensure.that(new PhysicalFileSystem().hashCode() == new PhysicalFileSystem().hashCode()).eq(true);
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
    ensure.that(fileSystem.pathString(path)).eq(".\\tmp\\source.txt");
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
    ensure.that(fileSystem.fileLastModified(path)).ge(timeBeforeCreation.getTime());
  }

  public void testThrowExceptionInCaseListReturnsNull() throws Exception {
    final TPath path = TPath.parse("tmp/directory");
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.listDirs(path);
      }
    });
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.listFiles(path);
      }
    });
  }

  public void testReturnAnEmptyArrayForEmptyDirectory() throws Exception {
    TPath path = TPath.parse("tmp/empty");
    fileSystem.createDir(path);
    ensure.that(fileSystem.listDirs(path).length).eq(0);
  }

  public void testGetJavaFile() {
    File file = new File("./test/test");
    ensure.that(fileSystem.toJavaFile(TPath.parse("test/test"))).eq(file);
  }

}
