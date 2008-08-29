package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestBase;

// testused to do nothing but boost coverage to deprecated classes
@SuppressWarnings({"deprecation"})
public class CoverageOrientedTest extends TestBase {
  public void testHandleClassPath() throws Exception {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    new net.sf.cotta.utils.ClassPath(factory.file("one/two.txt"));
    new net.sf.cotta.utils.ClassPath(factory.dir("one/two"));
  }

  public void testHandleClassPathLocator() throws Exception {
    new net.sf.cotta.utils.ClassPathLocator(getClass());
  }
}
