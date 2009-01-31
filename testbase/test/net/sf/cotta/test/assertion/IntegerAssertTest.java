package net.sf.cotta.test.assertion;

import net.sf.cotta.test.TestCase;

public class IntegerAssertTest extends TestCase {
  public void testLessThan() {
    new IntegerAssert(5).lessThan(6);
  }

  public void testLessThanFailsWhenNotTrue() {
    final IntegerAssert assertion = new IntegerAssert(5);
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        assertion.lessThan(4);
      }
    }).throwsException(AssertionError.class).message().contains("4");
  }

  public void testGreaterThan() {
    new IntegerAssert(6).greaterThan(5);
  }

  public void testGreaterThanFailsWhenNotTrue() {
    final IntegerAssert assertion = new IntegerAssert(6);
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        assertion.greaterThan(6);
      }
    }).throwsException(AssertionError.class).message().contains("6");
  }
}
