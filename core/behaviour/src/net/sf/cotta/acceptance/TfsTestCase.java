package net.sf.cotta.acceptance;

import net.sf.cotta.system.FileSystem;
import net.sf.cotta.*;
import net.sf.cotta.io.*;
import net.sf.cotta.test.assertion.CodeBlock;

import java.io.*;
import java.util.List;

public abstract class TfsTestCase extends TestCase {

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
      ensure.that(e).message().contains("./tmp");
    }
  }

  private TDirectory ensureThatDirectoryCanBeCreated(TFileFactory factory, String directoryPath) throws TIoException {
    TDirectory directory = factory.dir(directoryPath);
    ensure.that(directory.name()).eq(directoryPath);
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
    List<TDirectory> subDirectories = directory.list().dirs();
    ensure.that(subDirectories.size()).eq(1);
    ensure.that(subDirectories.get(0).exists()).eq(true);
    ensure.that(subDirectories.get(0).name()).eq(subDirectoryName);
  }

  private void ensureThatListFilesReturnsAListOfExistingFiles(TDirectory directory) throws TIoException {
    List<TFile> files = directory.list().files();
    ensure.that(files.size()).eq(1);
    ensure.that(files.get(0).exists()).eq(true);
    ensure.that(files.get(0).name()).eq("test.txt");
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
    ensure.that(sameChild).eqWithHash(directory);
  }

  public void testMakeSureRootDirectoryAlwaysExists() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    ensure.that(factory.dir("/").exists()).eq(true);
    ensure.that(factory.dir("/tmp").parent().exists()).eq(true);
    ensure.that(factory.file("/tmp").parent().exists()).eq(true);
  }

  public void testHaveAConceptOfCurrentWorkingDirectory() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TDirectory directory = factory.dir("dir");
    ensure.that(directory.parent().name()).eq(".");
    ensure.that(directory.parent().exists()).eq(true);
    TFile file = factory.file("test.txt");
    ensure.that(file.parent().name()).eq(".");
    ensure.that(file.parent().exists()).eq(true);
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
    ensure.that(factory.file("tmp/test.txt").load()).eq("expected");
  }

  public void testProvideInputStream() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    file.save("");
    InputStream stream = file.inputStream();
    registerResource(stream);
    int expected = -1;
    ensure.that(stream.read()).eq(expected);
  }

  public void testProvideOutputStreamBasedOnMode() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    OutputStream stream = file.outputStream(OutputMode.OVERWRITE);
    registerResource(stream);
    stream.write("this is a line".getBytes());
    stream.close();
    ensure.that(file.parent().exists()).eq(true);
    List<TFile> actual = file.parent().list().files();
    ensure.that(actual.size()).eq(1);
    ensure.that(actual.get(0)).eq(file);
    ensure.that(file.load()).eq("this is a line");
    file.append(new OutputProcessor() {
      public void process(OutputManager io) throws IOException {
        io.printWriter().print("-appended");
      }
    });
    ensure.that(file.load()).eq("this is a line-appended");
    file.write(new OutputProcessor() {
      public void process(OutputManager io) throws IOException {
        io.printWriter().print("new content");
      }
    });
    final StringBuffer buffer = new StringBuffer();
    file.read(new LineProcessor() {
      public void process(String line) {
        buffer.append(line);
      }
    });
    ensure.that(buffer.toString()).eq("new content");
  }

  public void testProvideReaderAndWriter() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    Writer writer = new OutputStreamWriter(file.outputStream(OutputMode.APPEND));
    registerResource(writer);
    writer.write("line\n");
    writer.close();
    Reader reader = new InputStreamReader(file.inputStream());
    registerResource(reader);
    ensure.character(reader.read()).eq('l');
  }

  public void testBeAbleToAppendToFile() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    TFile file = factory.file("tmp/test.txt");
    Writer writer = new OutputStreamWriter(file.outputStream(OutputMode.APPEND));
    registerResource(writer);
    writer.write("one");
    writer.close();
    Writer anotherWriter = new OutputStreamWriter(file.outputStream(OutputMode.APPEND));
    registerResource(anotherWriter);
    anotherWriter.write("two");
    anotherWriter.close();
    ensure.that(file.load()).eq("onetwo");
  }

  public void testThrowExceptionIfDirectoryToListDirIsNotFound() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    final TDirectory directory = factory.dir("tmp/directory");
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().dirs();
      }
    }).throwsException(TDirectoryNotFoundException.class);
  }

  public void testThrowExceptionIfDirectoryToListFileIsNotFound() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem());
    final TDirectory directory = factory.dir("tmp/directory");
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().files();
      }
    }).throwsException(TDirectoryNotFoundException.class);
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
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().dirs();
      }
    }).throwsException(TDirectoryNotFoundException.class);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().files();
      }
    }).throwsException(TDirectoryNotFoundException.class);

    directory.ensureExists();
    ensure.that(directory.list().dirs()).isEmpty();
    ensure.that(directory.list().files()).isEmpty();
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
      public void process(OutputManager manager) throws IOException {
        manager.outputStream().write(255);
      }
    });
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ensure.that(inputManager.inputStream().read()).eq(255);
      }
    });
  }

}
