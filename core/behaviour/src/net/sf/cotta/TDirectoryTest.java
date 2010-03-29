package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.memory.ListingOrder;
import net.sf.cotta.physical.PhysicalFileSystemTestCase;
import net.sf.cotta.system.FileSystem;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.zip.ZipFileSystem;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TDirectoryTest extends PhysicalFileSystemTestCase {
  public void testExistAfterCreate() throws Exception {
    //Given
    TFileFactory factory = factory();
    //When
    TDirectory directory = factory.dir("test");
    directory.ensureExists();
    //Ensure
    ensure.that(directory.exists()).eq(true);
    TDirectory anotherDirectory = factory.dir("test");
    ensure.that(anotherDirectory.exists()).eq(true);
  }

  public void testTakePathForDir() throws Exception {
    TFileFactory factory = factory();
    TDirectory dir = factory.dir("test");
    TDirectory subOne = dir.dir("one/two.txt");
    TDirectory subTwo = dir.dir(TPath.parse("one/two.txt"));
    TDirectory subThree = dir.dir("one/two.txt");
    ensure.that(subOne).eq(subTwo);
    ensure.that(subOne).eq(subThree);
  }

  private TFileFactory factory() {
    return factory(ListingOrder.NULL);
  }

  private TFileFactory factory(ListingOrder order) {
    return new TFileFactory(new InMemoryFileSystem(order));
  }

  public void testBeCreatedWithCorrectNameAndNotExists() throws Exception {
    TDirectory directory = factory().dir("directory");
    ensure.that(directory.exists()).eq(false);
    ensure.that(directory.name()).eq("directory");
  }

  public void testNotCareIfDirectoryAlreadyExists() throws Exception {
    TDirectory directory = factory().dir("test");
    directory.ensureExists();
    directory.ensureExists();
  }

  public void testBeInstantiateANoneExistingFile() throws Exception {
    TDirectory directory = factory().dir("test");
    TFile file = directory.file("test.txt");
    ensure.that(file.exists()).eq(false);
  }

  public void testBeAbleToInstantiateASubDirectory() throws TIoException {
    TDirectory directory = factory().dir("parent").dir("test").dir("sub");
    ensure.that(directory.exists()).eq(false);
  }

  public void testInstantiateAFileGivenRelativePath() throws Exception {
    TFileFactory factory = factory();
    TDirectory directory = factory.dir("one/two");
    ensure.that(directory.file(TPath.parse("../three/txt.txt"))).eq(factory.file("one/three/txt.txt"));
    ensure.that(directory.file("../three/txt.txt")).eq(factory.file("one/three/txt.txt"));
  }

  public void testSupportAbsolutePathForFile() {
    TFileFactory factory = factory();
    TDirectory directory = factory.dir("one/two");
    ensure.that(directory.file("/one/two/txt.txt")).eq(factory.file("/one/two/txt.txt"));
  }

  public void testSupportAbsolutePathForDir() {
    TFileFactory factory = factory();
    TDirectory directory = factory.dir("one/two");
    ensure.that(directory.dir("/one/two")).eq(factory.dir("/one/two"));
  }

  public void testInstantiateADirectoryGivenRelativePath() throws Exception {
    TFileFactory factory = factory();
    TDirectory directory = factory.dir("one/two");
    ensure.that(directory.dir(TPath.parse("../three/four"))).eq(factory.dir("one/three/four"));
  }

  public void testBeAbleToListSubDirectories() throws Exception {
    //Given
    TDirectory root = factory().dir("root");
    root.ensureExists();
    root.dir("one").ensureExists();
    root.dir("two").ensureExists();
    //When
    List<TDirectory> subDirectories = root.list().dirs();
    //Ensure
    ensure.that(subDirectories).isOfSize(2);
  }

  public void testBeAbleToListDirsOrdered() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TDirectory a = root.dir("a").ensureExists();
    TDirectory b = root.dir("b").ensureExists();
    List<TDirectory> actual = root.list().ordered().dirs();
    ensure.that(actual).eq(a, b);
  }

  public void testBeAbleToListDirsOrderedWithFilter() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TDirectory a = root.dir("a").ensureExists();
    TDirectory b = root.dir("b").ensureExists();
    root.dir("c").ensureExists();
    List<TDirectory> actual = root.list(new TDirectoryFilter() {
      public boolean accept(TDirectory directory) {
        return !directory.name().equals("c");
      }
    }).ordered().dirs();
    ensure.that(actual).eq(a, b);
  }

  public void testBeAbleToListInMemoryFilesInDirectory() throws Exception {
    TDirectory root = factory(ListingOrder.AToZ).dir("root");
    TFile one = root.file("one.txt").create();
    TFile two = root.file("two.txt").create();
    ensure.that(root.list().files()).eq(one, two);
  }

  public void testBeAbleToListFilesSorted() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TFile a = root.file("a.txt").create();
    TFile b = root.file("b.txt").create();
    ensure.that(root.list().ordered().files()).eq(a, b);
  }

  public void testListFilesOrderedWithFilter() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TFile a = root.file("a.txt").create();
    TFile b = root.file("b.txt").create();
    root.file("c.zip").create();
    ensure.that(root.list(new TFileFilter() {
      public boolean accept(TFile file) {
        return file.extname().equals("txt");
      }
    }).ordered().files()).eq(a, b);
  }

  public void testListShouldReturnEntriesInCurrentDir() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TFile a = root.file("a.txt").create();
    TFile b = root.file("b.txt").create();
    TDirectory c = root.dir("c").ensureExists();
    ensure.that(root.list()).eq(c, b, a);
  }

  public void testListOrderedShouldReturnEntriesInCurrentDir() throws TIoException {
    TDirectory root = factory(ListingOrder.ZToA).dir("root");
    TFile a = root.file("a.txt").create();
    TFile b = root.file("b.txt").create();
    TDirectory c = root.dir("c").ensureExists();
    ensure.that(root.list().ordered()).eq(c, a, b);
  }

  public void testBeEqualToAnotherDirectoryWithTheSamePathAndFactory() throws Exception {
    TFileFactory factory = factory();
    TDirectory one = factory.dir("/tmp/one/two");
    TDirectory two = factory.dir("/tmp/one/two");
    ensure.that(one).eqWithHash(two);
  }

  public void testKnowItsParent() throws Exception {
    TDirectory directory = factory().dir("/tmp/one");
    ensure.that(directory.parent().name()).eq("tmp");
  }

  public void testThrowExceptionIfDirectionNotEmpty() throws Exception {
    TFile file = factory().file("/tmp/test.txt");
    file.create();
    try {
      file.parent().delete();
      fail("TIoException should have been thrown");
    } catch (TIoException e) {
      ensure.that(e).message().contains("/tmp", "not empty");
    }
  }

  public void testDeleteAll() throws Exception {
    TDirectory directory = factory().dir("/tmp/directory");
    directory.file("file.txt").create();
    TDirectory subdir = directory.dir("sub");
    subdir.ensureExists();
    subdir.file("file2.txt").create();
    directory.deleteAll();
    ensure.that(directory.exists()).eq(false);
  }

  public void testEqualIfPathAndFileSystemEqual() throws Exception {
    FileSystem fileSystem = new InMemoryFileSystem();
    TDirectory directory1 = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/test"));
    TDirectory directory2 = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/test"));
    ensure.that(directory1.equals(directory2)).eq(true);
  }

  public void testThrowExceptionIfDirectoryNotFoundInListingDirectories() throws Exception {
    FileSystem fileSystem = new InMemoryFileSystem();
    final TDirectory directory = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/test"));
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().dirs();
      }
    }).throwsException(TDirectoryNotFoundException.class);
  }

  public void testThrowExceptionIfDirectoryNotFoundInListingFiles() throws Exception {
    FileSystem fileSystem = new InMemoryFileSystem();
    final TDirectory directory = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/test"));
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        directory.list().files();
      }
    }).throwsException(TDirectoryNotFoundException.class);
  }

  public void testGetPath() throws Exception {
    FileSystem fileSystem = new InMemoryFileSystem();
    TPath path = TPath.parse("/tmp/test");
    TDirectory directory = new TDirectory(new TFileFactory(fileSystem), path);
    ensure.that(directory.path()).eq(fileSystem.pathString(path));
  }

  public void testCopyDirectoryFiles() throws TIoException {
    FileSystem fileSystem = new InMemoryFileSystem();
    TPath path = TPath.parse("/tmp/from");
    TDirectory source = new TDirectory(new TFileFactory(fileSystem), path);
    source.file("source.txt").save("source");
    TDirectory target = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/to"));
    source.mergeTo(target);
    ensure.that(target.file("source.txt").load()).eq("source");
  }

  public void testCopySubDirectory() throws TIoException {
    FileSystem fileSystem = new InMemoryFileSystem();
    TPath path = TPath.parse("/tmp/from");
    TDirectory source = new TDirectory(new TFileFactory(fileSystem), path);
    source.dir("sub-directory").ensureExists();
    TDirectory target = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/to"));
    source.mergeTo(target);
    ensure.that(target.dir("sub-directory").exists()).eq(true);
  }

  public void testCopyFilesInSubDirectory() throws TIoException {
    FileSystem fileSystem = new InMemoryFileSystem();
    TPath path = TPath.parse("/tmp/from");
    TDirectory source = new TDirectory(new TFileFactory(fileSystem), path);
    source.dir("subdirectory").file("source.txt").save("subsource");
    TDirectory target = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/to"));
    source.mergeTo(target);
    ensure.that(target.dir("subdirectory").file("source.txt").load()).eq("subsource");
  }

  public void testMoveDirectory() throws TIoException {
    FileSystem fileSystem = new InMemoryFileSystem();
    TPath path = TPath.parse("/tmp/from");
    TDirectory source = new TDirectory(new TFileFactory(fileSystem), path);
    source.dir("subdirectory").file("source.txt").save("subsource");
    TDirectory target = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/tmp/to"));
    source.moveTo(target);
    ensure.that(target.dir("subdirectory").file("source.txt").load()).eq("subsource");
    ensure.that(source.exists()).eq(false);
  }

  public void testGetFileObject() {
    final File expected = new File("test");
    final TPath path = TPath.parse("test");
    Mockery context = new Mockery();
    final FileSystem fileSystem = context.mock(FileSystem.class);
    context.checking(new Expectations() {
      {
        one(fileSystem).toJavaFile(path);
        will(returnValue(expected));
      }
    });
    TDirectory directory = new TDirectory(new TFileFactory(fileSystem), path);
    ensure.that(directory.toJavaFile()).sameAs(expected);
    context.assertIsSatisfied();
  }

  public void testCreateZipFile() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory directory = factory.dir("tmp/source");
    directory.ensureExists();
    TFile zipFile = directory.parent().file("target.zip");
    directory.zipTo(zipFile);
    ensure.that(zipFile).fileExtists();
    File javaFile = zipFile.toJavaFile();
    ZipFileSystem zipFileSystem = new ZipFileSystem(javaFile);
    registerResource(zipFileSystem);
    TFileFactory zipFileFactory = new TFileFactory(zipFileSystem);
    TDirectory root = zipFileFactory.dir("/");
    ensure.that(root.exists()).eq(true);
    ensure.that(root.list().dirs()).isEmpty();
  }

  public void testCreateZipFileForOneFile() throws Exception {
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory directory = factory.dir("tmp/source");
    TFile file = directory.file("Cotta.txt");
    file.save("Something...");

    TFile zipFile = directory.parent().file("another.zip");
    directory.zipTo(zipFile);
    File javaFile = zipFile.toJavaFile();
    ZipFileSystem zipFileSystem = new ZipFileSystem(javaFile);
    registerResource(zipFileSystem);
    TFileFactory zipFileFactory = new TFileFactory(zipFileSystem);
    TDirectory root = zipFileFactory.dir("/");
    List<TFile> actualList = root.list().files();
    ensure.that(actualList.size()).eq(1);
    ensure.that(actualList.get(0).name()).eq("Cotta.txt");
  }

  public void testCreateDirectory() throws IOException {
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory directory = factory.dir("tmp/source");
    directory.dir("subdir").ensureExists();

    TFile zipFile = directory.parent().file("zip.zip");
    directory.zipTo(zipFile);
    ZipFileSystem zipFileSystem = new ZipFileSystem(zipFile.toJavaFile());
    registerResource(zipFileSystem);
    TFileFactory zipFileFactory = new TFileFactory(zipFileSystem);
    TDirectory root = zipFileFactory.dir("/");
    ensure.that(root.list().files()).isEmpty();
    TDirectory actual = ensure.that(root.list().dirs()).hasOneItem();
    ensure.that(actual.name()).eq("subdir");
  }

  public void testKeepFilesUnderTheDirectory() throws IOException {
    TFileFactory factory = new TFileFactory(fileSystem);
    TDirectory directory = factory.dir("tmp/source");
    directory.file("subdir/file.txt").save("content");
    TFile zip = directory.parent().file("result.zip");
    directory.zipTo(zip);
    ZipFileSystem zipFileSystem = new ZipFileSystem(zip.toJavaFile());
    registerResource(zipFileSystem);
    TFileFactory zipTFileFactory = new TFileFactory(zipFileSystem);
    TDirectory root = zipTFileFactory.dir("/");
    ensure.that(root.dir("subdir").list().dirs()).isEmpty();
    ensure.that(root.list().files()).isEmpty();
    ensure.that(root.dir("subdir").list().files()).contains(root.file("subdir/file.txt"));
  }

  public void testListFilesByFilter() throws Exception {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    TFile expected = factory.file("/directory/one.txt").create();
    factory.file("/directory/two.txt").create();
    TDirectory directory = factory.dir("/directory");
    List<TFile> files = directory.list(new TFileFilter() {
      public boolean accept(TFile file) {
        return file.name().equals("one.txt");
      }
    }).files();
    ensure.that(files.size()).eq(1);
    ensure.that(files.get(0)).eq(expected);
  }

  public void testLisDirstByFilter() throws Exception {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    TDirectory expected = factory.dir("/directory/one").ensureExists();
    factory.dir("/directory/two").ensureExists();
    TDirectory directory = factory.dir("/directory");
    List<TDirectory> dirs = directory.list(new TDirectoryFilter() {
      public boolean accept(TDirectory directory) {
        return directory.name().equals("one");
      }
    }).dirs();
    ensure.that(dirs.size()).eq(1);
    ensure.that(dirs.get(0)).eq(expected);
  }

  public void testExposePathBehaviours() throws Exception {
    TDirectory directory = new TDirectory(new TFileFactory(new InMemoryFileSystem()), TPath.parse("/one/two"));
    TDirectory subDirectory = directory.dir(TPath.parse("three/four"));
    ensure.that(subDirectory.isChildOf(directory)).eq(true);
    ensure.that(subDirectory.pathFrom(directory)).eq(subDirectory.toPath().pathFrom(directory.toPath()));
  }

  public void testProvideVisitAbility() throws Exception {
    final TDirectory directory = factory().dir("/one/two").ensureExists();
    Mockery context = new Mockery();
    final FileVisitor fileVisitor = context.mock(FileVisitor.class);
    context.checking(new Expectations() {
      {
        one(fileVisitor).visit(directory);
      }
    });
    directory.visit(fileVisitor);
    context.assertIsSatisfied();
  }

  public void testToCanonicalDir() {
    String pathString = "/one/two/../three";
    TDirectory directory = TFileFactory.physical().dir(pathString);
    ensure.that(directory.toPath()).eq(TPath.parse(pathString));
    ensure.that(directory.toCanonicalDir()).eq(directory.factory().dir(directory.toCanonicalPath()));
  }
}
