package net.sf.cotta.test;

import junit.framework.TestSuite;
import net.sf.cotta.CottaTestBase;

public class TestLoaderTest extends CottaTestBase {
  public void testLoadingTests() {
    TestLoader loader = new TestLoader(getClass());
    TestSuite suite = loader.loadTests();
    ensure().suite(suite).hasTest(getClass(), getName());
  }

}
