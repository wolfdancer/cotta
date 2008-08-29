package net.sf.cotta.ftp;

import junit.framework.TestSuite;
import net.sf.cotta.test.TestLoader;

public class AllTests {
  public static TestSuite suite() {
    return new TestLoader(AllTests.class).loadTests();
  }
}
