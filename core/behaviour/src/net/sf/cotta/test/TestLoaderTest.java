package net.sf.cotta.test;

import junit.framework.TestSuite;
import net.sf.cotta.CottaTestCase;

public class TestLoaderTest extends CottaTestCase {
  public void testLoadingTests() {
    TestLoader loader = new TestLoader(getClass());
    TestSuite suite = loader.loadTests();
    ensure.suite(suite).hasTest(getClass(), getName());
  }

}
