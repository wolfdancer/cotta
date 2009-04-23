package net.sf.cotta;

import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.test.TestCase;

public class CatastrophicFileSystemTest extends TestCase {
  public void testThrowExceptionsWhenDiskFull() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.diskFull();
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(TPath.parse("/tmp/txt.txt"));
      }
    }).throwsException(TIoException.class);

  }

  public void testThrowExceptionIfFileIsLocked() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    final TPath path = TPath.parse("/text.txt");
    fileSystem.lockFile(path);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    }).throwsException(TIoException.class);
    fileSystem.unLockFile(path);
    fileSystem.createFile(path);
  }

  public void testUnlockFileAfterSeveralTries() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    final TPath path = TPath.parse("/text.txt");
    fileSystem.lockFile(path, 2);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    }).throwsException(TIoException.class);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    }).throwsException(TIoException.class);
    fileSystem.createFile(path);
  }

  public void testOnlyLockTheFileSpecified() throws Exception {
    final TPath pathOne = TPath.parse("/one.txt");
    final TPath pathTwo = TPath.parse("/two.txt");
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.lockFile(pathOne);
    fileSystem.createFile(pathTwo);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(pathOne);
      }
    }).throwsException(TIoException.class);
  }

  public void testThrowExceptionOnDiskError() throws Exception {
    final TPath one = TPath.parse("/one.txt");
    TPath two = TPath.parse("/two.txt");
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.createFile(one);
    fileSystem.createFile(two);
    fileSystem.diskErrorFor(one);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createInputStream(one);
      }
    }).throwsException(TIoException.class);
    ensure.that(new TFile(new TFileFactory(fileSystem), two).load()).eq("");
  }

}
