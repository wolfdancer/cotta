package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ControlledFileSystemTest extends CottaTestCase {
  public void testNotAllowUpdateOnNotPermittedDirectories() throws Exception {
    FileSystem fileSystem = fileSystemForTmp();
    fileSystem.createDir(TPath.parse("tmp/sub"));
    try {
      fileSystem.createDir(TPath.parse("/tmp/sub"));
      fail("exception should have been thrown");
    } catch (PermissionDeniedException e) {
      ensure.that(e).message().contains("/tmp/sub", "./tmp");
    }
  }

  public void testAllowUpdateToTheDirectoryItsself() throws Exception {
    FileSystem fileSystem = fileSystemForTmp();
    fileSystem.createDir(TPath.parse("tmp"));
  }

  public void testCreateReadOnlyFileSystem() throws Exception {
    Mockery context = new Mockery();
    FileSystem fileSystemMock = context.mock(FileSystem.class);
    FileSystem fileSystem = ControlledFileSystem.readOnlyFileSystem(fileSystemMock);
    try {
      fileSystem.createDir(TPath.parse("/"));
      fail("Permission denied acception should have been thrown");
    } catch (PermissionDeniedException e) {
      ensure.that(e).message().contains("read only");
    }
    context.assertIsSatisfied();
  }

  private FileSystem fileSystemForTmp() {
    return ControlledFileSystem.pathControlledFileSystem(new InMemoryFileSystem(), TPath.parse("tmp"));
  }

  public void testNotMoveFileIfSourceIsNotUnderControl() throws Exception {
    FileSystem fileSystem = fileSystemForTmp();
    TPath source = TPath.parse("/tmp/source.txt");
    try {
      fileSystem.moveFile(source, TPath.parse("tmp/dest.txt"));
      fail("should have thrown PermissionDeniedException");
    } catch (PermissionDeniedException e) {
      ensure.that(e.getPath()).eq(source);
    }
  }

  public void testNotMoveFileIfDestIsNotUnderControl() throws Exception {
    FileSystem fileSystem = fileSystemForTmp();
    TPath dest = TPath.parse("/tmp/dest.txt");
    try {
      fileSystem.moveFile(TPath.parse("tmp/source.txt"), dest);
      fail("should have thrown PermissionDeniedException");
    } catch (PermissionDeniedException e) {
      ensure.that(e.getPath()).eq(dest);
    }
  }

  public void testControlReadOnFileExists() throws Exception {
    final TPath path = TPath.parse("/tmp/text.txt");
    final InputStream expected = new ByteArrayInputStream("".getBytes());
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystemMock = context.mock(FileSystem.class);

    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        one(fileSystemMock).createInputStream(path);
        will(returnValue(expected));
      }
    });

    FileSystem fileSystem = new ControlledFileSystem(fileSystemMock, controller);
    ensure.that(fileSystem.createInputStream(path)).sameAs(expected);
    context.assertIsSatisfied();
  }

}
