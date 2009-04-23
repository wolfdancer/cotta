package net.sf.cotta.test;

import junit.framework.TestSuite;
import net.sf.cotta.TestCase;

public class TestLoaderTest extends TestCase {
  public void testLoadingTests() {
    TestLoader loader = new TestLoader(getClass());
    TestSuite suite = loader.loadTests();
    ensure.suite(suite).hasTest(getClass(), getName());
  }

}
