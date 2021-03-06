package net.sf.cotta.zip;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.TestCase;
import net.sf.cotta.io.Input;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.system.FileSystem;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.utils.ClassPathEntry;
import net.sf.cotta.utils.ClassPathEntryLocator;
import net.sf.cotta.utils.ClassPathType;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ZipFileSystemTest extends TestCase {
  private FileSystem zip;
  private File workingZipFile;
  private static final String TEST_TXT_CONTENT = "sub";
  private static final int READ_BUFFER_SIZE = 16;

  public void beforeMethod() throws Exception {
    super.beforeMethod();
    zip = filesystem();
  }

  public void testListRootDirectory() throws Exception {
    List<TPath> paths = zip.list(TPath.parse("/")).dirs();
    ensure.that(paths).eq(TPath.parse("/test"));
  }

  public void testRetrieveAnyDirectory() throws Exception {
    List<TPath> paths = zip.list(TPath.parse("/test")).files();
    ensure.that(paths).eq(TPath.parse("/test/test.txt"));
    ensure.that(zip.list(TPath.parse("/test")).dirs()).isEmpty();
  }

  private FileSystem filesystem() throws TIoException {
    ClassPathEntryLocator classPathEntryLocator = new ClassPathEntryLocator("/test.zip");
    final ClassPathEntry pathEntry = classPathEntryLocator.locateEntry();
    // "This test only works if behaviour class is not in a jar"
    ensure.that(pathEntry.type()).eq(ClassPathType.DIRECTORY);
    registerResource(new Closeable() {
      public void close() throws TIoException {
        pathEntry.closeResource();
      }
    });
    TDirectory tdirectory = pathEntry.openAsDirectory();
    File directory = new File(tdirectory.path());
    String workzip = "work.zip";
    tdirectory.file("test.zip").copyTo(tdirectory.file(workzip));
    workingZipFile = new File(directory, workzip);
    return ZipFileSystem.readOnlyZipFileSystem(workingZipFile);
  }

  public void testListRootFiles() throws Exception {
    List<TPath> tPath = zip.list(TPath.parse("/")).files();
    ensure.that(tPath).eq(TPath.parse("/test.txt"));
  }

  public void testKnowIfAnEntryExists() throws Exception {
    ensure.that(zip.dirExists(TPath.parse("/test"))).eq(true);
    ensure.that(zip.fileExists(TPath.parse("/test/test.txt"))).eq(true);
  }

  public void testReadFileEntryContent() throws Exception {
    TPath path = TPath.parse("/test/test.txt");
    InputStream stream = zip.createInputStream(path);
    StringBuffer buffer = new StringBuffer();
    loadContent(buffer, new InputStreamReader(stream));
    ensure.that(buffer.toString()).eq(TEST_TXT_CONTENT);
  }

  private void loadContent(StringBuffer content, Reader reader) throws IOException {
    char[] buffer = new char[READ_BUFFER_SIZE];
    int read = 0;
    while (read != -1) {
      content.append(buffer, 0, read);
      read = reader.read(buffer, 0, buffer.length);
    }
  }

  public void testThrowExceptionIfEntryNotExists() throws Exception {
    TPath path = TPath.parse("/nothere.txt");
    try {
      zip.createInputStream(path);
      fail("exception should have been thrown");
    } catch (TFileNotFoundException e) {
      ensure.that(e.getPath()).eq(path);
    }
  }

  public void testGetPathString() throws Exception {
    TPath path = TPath.parse("/test/test.txt");
    ensure.that(zip.pathString(path)).eq(workingZipFile.getPath() + "[" + path.toPathString() + "]");
  }

  public void testGetLength() throws Exception {
    TPath path = TPath.parse("/test/test.txt");
    ensure.that(zip.fileLength(path)).eq(TEST_TXT_CONTENT.getBytes().length);
  }

  public void testToUri() throws IOException {
    TPath path = TPath.parse("/test/test.txt");
    InputStream inputStream = zip.toUri(path).toURL().openStream();
    String content = Input.with(inputStream).load();
    ensure.that(content).eq(TEST_TXT_CONTENT);
  }

  public void testNotImplementCreateDir() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.createDir(TPath.parse("/test"));
      }
    });
  }

  public void testNotImplementCreateFile() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.createFile(TPath.parse("/test"));
      }
    });
  }

  public void testNotImplementDeleteFile() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.deleteFile(TPath.parse("/test"));
      }
    });
  }

  public void testNotImplementDeleteDir() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.deleteDirectory(TPath.parse("/test"));
      }
    });
  }

  public void testNotCreateOutputStream() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.createOutputStream(TPath.parse("/test"), OutputMode.APPEND);
      }
    });
  }

  public void testNotImplementMoveFile() throws Exception {
    ensureOperationNotSupported(new CodeBlock() {
      public void execute() throws Exception {
        final ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
        registerToClose(zipFileSystem);
        zipFileSystem.moveFile(TPath.parse("/test"), TPath.parse("/target"));
      }
    });
  }

  public void testComparePath() throws IOException {
    ZipFileSystem zipFileSystem = new ZipFileSystem(workingZipFile);
    registerToClose(zipFileSystem);
    TPath one = TPath.parse("one");
    TPath two = TPath.parse("two");
    ensure.that(zipFileSystem.compare(one, two)).eq("one".compareTo("two"));
  }

  private void registerToClose(final ZipFileSystem zipFileSystem) {
    registerResource(new Closeable() {
      public void close() throws TIoException {
        zipFileSystem.close();
      }
    });
  }

  private void ensureOperationNotSupported(CodeBlock CodeBlock) throws Exception {
    ensure.code(CodeBlock).throwsException(UnsupportedOperationException.class);
  }

}
