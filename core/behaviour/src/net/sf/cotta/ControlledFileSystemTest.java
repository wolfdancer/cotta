package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ControlledFileSystemTest extends TestCase {
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

  public void testControlReadOnCreateInputStream() throws Exception {
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

  public void testControlReadOnFileExists() throws TIoException {
    final boolean expected = true;
    final TPath path = TPath.parse("/tmp/text.txt");
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystemMock = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        one(fileSystemMock).fileExists(path);
        will(returnValue(expected));
      }
    });

    FileSystem fileSystem = new ControlledFileSystem(fileSystemMock, controller);
    ensure.that(fileSystem.fileExists(path)).eq(expected);
    context.assertIsSatisfied();
  }

  public void testControlReadOnDirExists() throws TIoException {
    final boolean expected = true;
    final TPath path = TPath.parse("/tmp");
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystemMock = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        one(fileSystemMock).dirExists(path);
        will(returnValue(expected));
      }
    });

    FileSystem fileSystem = new ControlledFileSystem(fileSystemMock, controller);
    ensure.that(fileSystem.dirExists(path)).eq(expected);
    context.assertIsSatisfied();
  }

  public void testControlReadOnDirFileLength() throws TIoException {
    final long expected = 111;
    final TPath path = TPath.parse("/tmp");
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystemMock = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        one(fileSystemMock).fileLength(path);
        will(returnValue(expected));
      }
    });

    FileSystem fileSystem = new ControlledFileSystem(fileSystemMock, controller);
    ensure.that(fileSystem.fileLength(path)).eq(expected);
    context.assertIsSatisfied();
  }

  public void testControlReadOnDirFileLastModified() throws TIoException {
    final long expected = 1341234;
    final TPath path = TPath.parse("/tmp");
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystemMock = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        one(fileSystemMock).fileLastModified(path);
        will(returnValue(expected));
      }
    });

    FileSystem fileSystem = new ControlledFileSystem(fileSystemMock, controller);
    ensure.that(fileSystem.fileLastModified(path)).eq(expected);
    context.assertIsSatisfied();
  }

  public void testControlReadOnCompare() throws TIoException {
    final TPath path1 = TPath.parse("/tmp1");
    final TPath path2 = TPath.parse("/tmp2");
    final int expected = 1234;
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystem = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path1);
        one(controller).readOperationControl(path2);
        one(fileSystem).compare(path1, path2);
        will(returnValue(expected));
      }
    });
    FileSystem controlled = new ControlledFileSystem(fileSystem, controller);
    ensure.that(controlled.compare(path1, path2)).eq(expected);
    context.assertIsSatisfied();
  }

  public void testControlReadOnHashCode() throws TIoException {
    final TPath path = TPath.parse("/tmp");
    Mockery context = new Mockery();
    final ControlledFileSystem.Controller controller = context.mock(ControlledFileSystem.Controller.class);
    final FileSystem fileSystem = context.mock(FileSystem.class);
    final int value = 1234546;
    context.checking(new Expectations() {
      {
        one(controller).readOperationControl(path);
        System.out.println(one(fileSystem).hashCode(path));
        will(returnValue(value));
      }
    });
    FileSystem controlled = new ControlledFileSystem(fileSystem, controller);
    ensure.that(controlled.hashCode(path)).eq(value);
    context.assertIsSatisfied();
  }

}
