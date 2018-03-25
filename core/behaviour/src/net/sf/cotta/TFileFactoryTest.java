package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.physical.PhysicalFileSystemTestCase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TFileFactoryTest extends PhysicalFileSystemTestCase {

  public void testEncodingForConstructor() {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    ensure.that(factory.defaultEncoding()).isNotEmpty();

    TFileFactory factoryWithEncoding = new TFileFactory(new InMemoryFileSystem(), "encoding");
    ensure.that(factoryWithEncoding.defaultEncoding()).eq("encoding");
  }

  public void testNotLoadFromAnHttpUrl() throws Exception {
    final URL url = new URL("http://cotta.sourceforge.net");
    ensure.that(TFileFactory.canConvertUrl(url)).eq(false);
  }

  public void testFailConvertingFromHttpUrl() throws Exception {
    final URL url = new URL("http://cotta.sourceforge.net");
    ensure.code(() -> TFileFactory.fileFromUrl(url)).throwsException(IllegalArgumentException.class);
  }

  public void testSupportFileUrl() throws Exception {
    File file = new File("/tmp/directory/file.txt");
    URL url = file.toURI().toURL();
    ensure.that(TFileFactory.canConvertUrl(url)).eq(true);
  }

  public void testConvertFromFileUrl() throws Exception {
    File file = new File("/tmp/directory/file.txt");
    URL url = file.toURI().toURL();
    TFile tfile = TFileFactory.fileFromUrl(url);
    ensure.that("file.txt").eq(tfile.name());
  }

  public void testSupportJarUrl() {
    final URL url = String.class.getResource("String.class");
    ensure.that(TFileFactory.canConvertUrl(url)).eq(true);
  }

  public void testLoadTFileFromResourceUrl() {
    //Given
    URL url = getClass().getResource("/" + String.class.getName().replace('.', '/') + ".class");
    //When
    TFile file = TFileFactory.fileFromUrl(url);
    //Ensure
    ensure.that(file.extname()).eq("jar");
    ensure.that(file.basename().equals("rt") || file.basename().equals("classes")).eq(true);
    ensure.that(file.exists()).eq(true);
  }

  public void testHandleResourceUrlWithSpaceInIt() throws MalformedURLException {
    //Given
    URL url = getClass().getResource("/" + String.class.getName().replace('.', '/') + ".class");
    int index = url.getFile().indexOf('!');
    ensure.that(url.getFile()).contains("!");
    URL urlWithSpaceInJarFilePath = new URL(url.getProtocol(), url.getHost(), url.getPort(),
        "file://C:/Documents and Settings/user/.m2/repository/selenium/selenium.jar" + url.getFile().substring(index));
    //When
    TFile file = TFileFactory.fileFromUrl(urlWithSpaceInJarFilePath);
    //Ensure
    ensure.that(file.name()).eq("selenium.jar");
  }

  public void testCreateTFileDirectlyFromJavaFile() throws Exception {
    //Given
    TFile file = new TFileFactory(fileSystem()).dir("tmp").file("content.txt");
    file.save("content");
    //When
    TFile actual = TFileFactory.physicalFile(new File("tmp", "content.txt"));
    //Ensure
    ensure.that(actual.load()).eq("content");
  }

  public void testCreateTDirectoryDirectlyFromJavaFile() throws TIoException {
    //Given
    TDirectory directory = new TFileFactory(fileSystem()).dir("tmp").dir("child");
    directory.ensureExists();
    //When
    TDirectory actual = TFileFactory.physicalDir(new File("tmp", "child"));
    //Ensure
    ensure.that(actual.exists()).eq(true);
  }

  public void testStaticPhysicalFactoryMethod() {
    TFileFactory physical = TFileFactory.physical();
    ensure.that(physical).sameAs(TFileFactory.physical());
  }

  public void testInMemoryFactoryMethod() {
    TFileFactory inMemory = TFileFactory.inMemory();
    ensure.that(inMemory.getFileSystem()).isA(InMemoryFileSystem.class);
  }
}
