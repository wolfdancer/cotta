package net.sf.cotta.test.assertion;

import net.sf.cotta.test.AssertionFactory;

abstract public class TestCase extends junit.framework.TestCase {
  public static final AssertionFactory ensure = new AssertionFactory();

}
