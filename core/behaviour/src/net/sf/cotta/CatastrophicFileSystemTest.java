package net.sf.cotta;

import net.sf.cotta.test.assertion.CodeBlock;

public class CatastrophicFileSystemTest extends CottaTestBase {
  public void testThrowExceptionsWhenDiskFull() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.diskFull();
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(TPath.parse("/tmp/txt.txt"));
      }
    });

  }

  public void testThrowExceptionIfFileIsLocked() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    final TPath path = TPath.parse("/text.txt");
    fileSystem.lockFile(path);
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    });
    fileSystem.unLockFile(path);
    fileSystem.createFile(path);
  }

  public void testUnlockFileAfterSeveralTries() throws Exception {
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    final TPath path = TPath.parse("/text.txt");
    fileSystem.lockFile(path, 2);
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    });
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(path);
      }
    });
    fileSystem.createFile(path);
  }

  public void testOnlyLockTheFileSpecified() throws Exception {
    final TPath pathOne = TPath.parse("/one.txt");
    final TPath pathTwo = TPath.parse("/two.txt");
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.lockFile(pathOne);
    fileSystem.createFile(pathTwo);
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createFile(pathOne);
      }
    });
  }

  public void testThrowExceptionOnDiskError() throws Exception {
    final TPath one = TPath.parse("/one.txt");
    TPath two = TPath.parse("/two.txt");
    final CatastrophicFileSystem fileSystem = new CatastrophicFileSystem();
    fileSystem.createFile(one);
    fileSystem.createFile(two);
    fileSystem.diskErrorFor(one);
    runAndCatch(TIoException.class, new CodeBlock() {
      public void execute() throws Exception {
        fileSystem.createInputStream(one);
      }
    });
    ensure.that(new TFile(fileSystem, two).load()).eq("");
  }

}
