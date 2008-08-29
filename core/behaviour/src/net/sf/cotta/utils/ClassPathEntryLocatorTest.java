package net.sf.cotta.utils;

import net.sf.cotta.CottaTestBase;
import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;
import net.sf.cotta.test.assertion.CodeBlock;

public class ClassPathEntryLocatorTest extends CottaTestBase {
  public void testLocateClassInDirectory() throws Exception {
    TDirectory directory = new ClassPathEntryLocator(ClassPathEntryLocator.class).locateEntry().openAsDirectory();
    TFile file = directory.file("net/sf/cotta/utils/ClassPathEntryLocator.class");
    ensure.that(file.exists()).eq(true);
  }

  public void testLocateClassInJarFile() throws Exception {
    TDirectory directory = new ClassPathEntryLocator(String.class).locateEntry().openAsDirectory();
    ensure.that(directory.file("java/lang/String.class").exists()).eq(true);
  }

  public void testLocateResourc() throws Exception {
    TDirectory directory = new ClassPathEntryLocator("/test.zip").locateEntry().openAsDirectory();
    ensure.that(directory.file("test.zip").exists()).eq(true);
  }

  public void testRequireAbsolutePath() throws Exception {
    runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        new ClassPathEntryLocator("relative/path/file.zip");
      }
    });
  }
}
