package net.sf.cotta.acceptance;

import net.sf.cotta.CottaTestBase;
import net.sf.cotta.FileSystem;
import net.sf.cotta.TDirectory;
import net.sf.cotta.TDirectoryNotFoundException;
import net.sf.cotta.TFile;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.io.InputManager;
import net.sf.cotta.io.InputProcessor;
import net.sf.cotta.io.IoManager;
import net.sf.cotta.io.IoProcessor;
import net.sf.cotta.io.LineProcessor;
import net.sf.cotta.io.OutputManager;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.io.OutputProcessor;
import net.sf.cotta.test.assertion.CodeBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public abstract class TfsTestBase extends CottaTestBase {

  protected abstract FileSystem fileSystem();

  public void testWorkForRegularOperations() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TDirectory directory = ensureThatDirectoryCanBeCreated(factory, "tmp");
    ensureThatFileCanBeCreated(directory);
    ensureThatListDirReturnsAListOfExistingSubDirectory(factory.dir("tmp"));
    ensureThatListFilesReturnsAListOfExistingFiles(factory.dir("tmp"));
    try {
      directory.delete();
      fail("TIoException should have occurred");
    } catch (TIoException e) {
      ensure.exception(e).message().contains("./tmp");
    }
  }

  private TDirectory ensureThatDirectoryCanBeCreated(TFileFactory factory, String directoryPath) throws TIoException {
    TDirectory directory = factory.dir(directoryPath);
    ensure.string(directory.name()).eq(directoryPath);
    ensure.that(directory.exists()).eq(false);
    directory.ensureExists();
    ensure.that(directory.exists()).eq(true);
    return directory;
  }

  private void ensureThatFileCanBeCreated(TDirectory directory) throws TIoException {
    TFile file = directory.file("test.txt");
    ensure.that(file.name()).eq("test.txt");
    ensure.that(file.exists()).eq(false);
    file.create();
    ensure.that(file.exists()).eq(true);
  }

  private void ensureThatListDirReturnsAListOfExistingSubDirectory(TDirectory directory) throws TIoException {
    TDirectory subDirectory = directory.dir("tmp-dir");
    ensure.that(subDirectory.name()).eq("tmp-dir");
    ensure.that(subDirectory.exists()).eq(false);
    subDirectory.ensureExists();

    ensure.that(subDirectory.exists()).eq(true);

    ensureThatContainsSubDirectory(directory, "tmp-dir");
  }

  private void ensureThatContainsSubDirectory(TDirectory directory, String subDirectoryName) throws TIoException {
    TDirectory[] subDirectories = directory.listDirs();
    ensure.that(subDirectories.length).eq(1);
    ensure.that(subDirectories[0].exists()).eq(true);
    ensure.that(subDirectories[0].name()).eq(subDirectoryName);
  }

  private void ensureThatListFilesReturnsAListOfExistingFiles(TDirectory directory) throws TIoException {
    TFile[] files = directory.listFiles();
    ensure.that(files.length).eq(1);
    ensure.that(files[0].exists()).eq(true);
    ensure.that(files[0].name()).eq("test.txt");
  }

  public void testAllowUserToStartWithAPathString() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TDirectory directory = factory.dir("tmp\\InMemoryFileTest\\subdirectory");
    ensure.that(directory.name()).eq("subdirectory");
    ensure.that(directory.parent().name()).eq("InMemoryFileTest");
    directory.ensureExists();
    TDirectory anotherDirectoryWithSamePath = factory.dir("tmp\\InMemoryFileTest\\subdirectory");
    ensure.that(anotherDirectoryWithSamePath.exists()).eq(true);

    TDirectory parent = factory.dir("tmp\\InMemoryFileTest");
    ensure.that(parent.exists()).eq(true);
    TDirectory sameChild = parent.dir("subdirectory");
    ensure.that(sameChild.exists()).eq(true);
    ensure.that(sameChild).javaEquals(directory);
  }

  public void testMakeSureRootDirectoryAlwaysExists() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    ensureEquals(factory.dir("/").exists(), true);
    ensureEquals(factory.dir("C:/").exists(), true);
    ensureEquals(factory.dir("/tmp").parent().exists(), true);
    ensureEquals(factory.file("/tmp").parent().exists(), true);
  }

  public void testHaveAConceptOfCurrentWorkingDirectory() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TDirectory directory = factory.dir("dir");
    ensureEquals(directory.parent().name(), ".");
    ensureEquals(directory.parent().exists(), true);
    TFile file = factory.file("test.txt");
    ensureEquals(file.parent().name(), ".");
    ensureEquals(file.parent().exists(), true);
  }

  public void testProvideCreationDeletionAndStatusCheckingOperationOnFiles() throws TIoException {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp\\FileTest\\test.txt");
    ensure.that(file.exists()).eq(false);
    ensure.that(file.parent()).eq(factory.dir("tmp\\FileTest"));
    ensure.that(file.parent().exists()).eq(false);
    file.create();
    ensure.that(file.exists()).eq(true);
    ensure.that(file.parent().exists()).eq(true);
    file.delete();
    ensure.that(file.exists()).eq(false);
    ensure.that(file.parent().exists()).eq(true);
  }

  public void testHandleTheCaseWhereFileAndDirectoryAreOfTheSameName() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp\\directory");
    TDirectory directory = factory.dir("tmp\\directory");
    directory.ensureExists();
    ensure.that(file.exists()).eq(false);
    try {
      file.create();
      fail("TIoException should have been thrown");
    } catch (TIoException e) {
      ensure.that(e).message().contains("directory");
    }
    TDirectory directoryWithFileName = directory.dir("file.txt");
    directory.file("file.txt").create();
    ensure.that(directoryWithFileName.exists()).eq(false);
    try {
      directoryWithFileName.ensureExists();
      fail("TIoException should have been thrown");
    } catch (TIoException e) {
      ensure.that(e).message().contains("directory");
    }
  }

  public void testSaveAndLoadFileContentDirectly() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    file.save("expected");
    ensure.that(file.lastModified()).gt(0);
    ensureEquals(factory.file("tmp/test.txt").load(), "expected");
  }

  public void testProvideInputStream() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    file.save("");
    InputStream stream = file.io().inputStream();
    registerToClose(resource(stream));
    ensureEquals(stream.read(), -1);
  }

  public void testProvideOutputStreamBasedOnMode() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    OutputStream stream = file.io().outputStream(OutputMode.OVERWRITE);
    registerToClose(resource(stream));
    stream.write("this is a line".getBytes());
    stream.close();
    ensure.that(file.parent().exists()).eq(true);
    TFile[] actual = file.parent().listFiles();
    ensure.that(actual.length).eq(1);
    ensure.that(actual[0]).eq(file);
    ensureEquals(file.load(), "this is a line");
    file.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.printWriter(OutputMode.APPEND).print("-appended");
      }
    });
    ensure.that(file.load()).eq("this is a line-appended");
    file.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.printWriter(OutputMode.OVERWRITE).print("new content");
      }
    });
    final StringBuffer buffer = new StringBuffer();
    file.open(new LineProcessor() {
      public void process(String line) {
        buffer.append(line);
      }
    });
    ensure.that(buffer.toString()).eq("new content");
  }

  public void testProvideReaderAndWriter() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    Writer writer = file.io().writer(OutputMode.APPEND);
    registerToClose(resource(writer));
    writer.write("line\n");
    writer.close();
    Reader reader = file.io().reader();
    registerToClose(resource(reader));
    ensure.character(reader.read()).eq('l');
  }

  public void testBeAbleToAppendToFile() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    Writer writer = file.io().writer(OutputMode.APPEND);
    registerToClose(resource(writer));
    writer.write("one");
    writer.close();
    Writer anotherWriter = file.io().writer(OutputMode.APPEND);
    registerToClose(resource(anotherWriter));
    anotherWriter.write("two");
    anotherWriter.close();
    ensure.that(file.load()).eq("onetwo");
  }

  public void testThrowExceptionIfDirectoryToListDirIsNotFound() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    final TDirectory directory = factory.dir("tmp/directory");
    runAndCatch(TDirectoryNotFoundException.class, new CodeBlock() {
      public void execute() throws Exception {
        directory.listDirs();
      }
    });
  }

  public void testThrowExceptionIfDirectoryToListFileIsNotFound() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    final TDirectory directory = factory.dir("tmp/directory");
    runAndCatch(TDirectoryNotFoundException.class, new CodeBlock() {
      public void execute() throws Exception {
        directory.listFiles();
      }
    });
  }

  public void testSupportMoveOperation() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile source = factory.file("tmp/source.txt");
    String content = "content for the source";
    source.save(content);
    long length = source.length();
    long modified = source.lastModified();
    TFile destination = factory.file("tmp/destination.txt");
    source.moveTo(destination);
    ensure.that(source.exists()).eq(false);
    ensure.that(destination.exists()).eq(true);
    ensure.that(destination.load()).eq(content);
    ensure.that(destination.length()).eq(length);
    ensure.that(destination.lastModified()).eq(modified);
  }

  public void testThrowExceptionIfSourceNotFoundInMove() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile source = factory.file("tmp/source.txt");
    try {
      source.moveTo(factory.file("tmp/dest.txt"));
      fail("TFileNotFound should have been thrown");
    } catch (TFileNotFoundException e) {
      ensure.that(e.getPath()).eq(TPath.parse("tmp/source.txt"));
    }
  }

  public void testThrowExceptionIfDestinationExistsInMove() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile source = factory.file("tmp/source.txt");
    source.save("content");
    TFile dest = factory.file("tmp/dest.txt");
    dest.save("another content");
    try {
      source.moveTo(dest);
      fail("TIoException should have been thrown");
    } catch (TIoException e) {
      ensure.that(e.getPath()).eq(TPath.parse("tmp/dest.txt"));
    }
  }

  public void testHaveRegularFileOperation() throws Exception {
    FileSystem fileSystem = fileSystem();
    TFileFactory factory = new TFileFactory(fileSystem);
    String pathString = "tmp/content.txt";
    TFile file = factory.file(pathString);
    String content = "my content";
    file.save(content);
    ensure.that(file.path()).eq(fileSystem.pathString(TPath.parse(pathString)));
    ensure.that(file.length()).eq(content.getBytes().length);
  }

  public void testHaveRegularDirectoryOperation() throws Exception {
    FileSystem fileSystem = fileSystem();
    TFileFactory factory = new TFileFactory(fileSystem);
    String pathString = "tmp/directory";
    final TDirectory directory = factory.dir(pathString);
    ensure.that(directory.exists()).eq(false);
    ensure.that(directory.path()).eq(fileSystem.pathString(TPath.parse(pathString)));
    runAndCatch(TDirectoryNotFoundException.class, new CodeBlock() {
      public void execute() throws Exception {
        directory.listDirs();
      }
    });
    runAndCatch(TDirectoryNotFoundException.class, new CodeBlock() {
      public void execute() throws Exception {
        directory.listFiles();
      }
    });

    directory.ensureExists();
    ensure.that(directory.listDirs().length).eq(0);
    ensure.that(directory.listFiles().length).eq(0);
  }

  public void testCopyDirectoryAndFile() throws TIoException {
    FileSystem fileSystem = fileSystem();
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory source = factory.dir("tmp/source");
    source.file("one.txt").save("oneoneone");
    source.dir("sub").file("two.txt").save("two.txt");
    TDirectory target = factory.dir("tmp/target");
    source.mergeTo(target);
    ensure.that(target.file("one.txt").load()).eq("oneoneone");
    ensure.that(target.dir("sub").file("two.txt").load()).eq("two.txt");
  }

  public void testMoveDirectoryAndFile() throws TIoException {
    FileSystem fileSystem = fileSystem();
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory source = factory.dir("tmp/source");
    source.file("one.txt").save("oneoneone");
    source.dir("sub").file("two.txt").save("two.txt");
    TDirectory target = factory.dir("tmp/target");
    source.moveTo(target);
    ensure.that(target.file("one.txt").load()).eq("oneoneone");
    ensure.that(target.dir("sub").file("two.txt").load()).eq("two.txt");
    ensure.that(source.exists()).eq(false);
  }

  public void testHandlingNegativeOneInContentByte() throws TIoException {
    FileSystem fileSystem = fileSystem();
    TFileFactory factory = new TFileFactory(fileSystem);
    TFile file = factory.file("tmp/content.txt");
    file.write(new OutputProcessor() {
      public void process(OutputManager outputManager) throws IOException {
        outputManager.outputStream().write(255);
      }
    });
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ensure.that(inputManager.inputStream().read()).eq(255);
      }
    });
  }

}
