package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestCase;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class ClassPathEntryTest extends TestCase {
  private TFileFactory factory = new TFileFactory(new InMemoryFileSystem());

  public void testTakeDirectoryOnlyAsDirectory() throws Exception {
    TDirectory directory = factory.dir("directory");
    ClassPathEntry pathEntry = new ClassPathEntry(directory);
    ensure.that(pathEntry.type()).eq(ClassPathType.DIRECTORY);
    ensure.that(pathEntry.openAsDirectory()).eq(directory);
    ensure.that(pathEntry.path()).eq(directory.path());
  }

  public void testGetPathForJarFile() throws Exception {
    ClassPathEntry pathEntry = loadTestZipFileInResource();
    TFile file = new TFileFactory().file(pathEntry.path());
    ensure.that(file.name()).eq("test.zip");
    ensure.that(file.exists()).eq(true);
  }

  public void testTakeFileAndConvertItToDirectoryUnderJarFileSystem() throws Exception {
    ClassPathEntry pathEntry = loadTestZipFileInResource();
    ensure.that(pathEntry.type()).eq(ClassPathType.FILE);
    TDirectory directory = pathEntry.openAsDirectory();
    List<TFile> filesInZip = directory.listFiles();
    ensure.that(1).eq(filesInZip.size());
    ensure.that("test.txt").eq(filesInZip.get(0).name());
    pathEntry.closeResource();
  }

  private ClassPathEntry loadTestZipFileInResource() throws URISyntaxException {
    URL url = getClass().getResource("/test.zip");
    File zipFile = new File(new URI(url.toExternalForm()));
    TFile tFile = new TFileFactory().file(zipFile.getAbsolutePath());
    return new ClassPathEntry(tFile);
  }

  public void testCacheJarFileSystemWhenNotClosed() throws Exception {
    ClassPathEntry pathEntry = loadTestZipFileInResource();
    TDirectory directory = pathEntry.openAsDirectory();
    ensure.that(directory).sameAs(pathEntry.openAsDirectory());
    pathEntry.closeResource();
    ensure.that(directory).notSameAs(pathEntry.openAsDirectory());
  }

}
