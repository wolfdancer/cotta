package net.sf.cotta.test.assertion;

public class IntegerAssertTest extends TestCase {
  public void testLessThan() {
    new IntegerAssert(5).lt(6);
  }

  public void testLessThanFailsWhenNotTrue() {
    final IntegerAssert assertion = new IntegerAssert(5);
    code(new CodeBlock() {
      public void execute() throws Exception {
        assertion.lt(4);
      }
    }).throwsException(AssertionError.class).message().contains("4");
  }

  public void testGreaterThan() {
    new IntegerAssert(6).gt(5);
  }

  public void testGreaterThanFailsWhenNotTrue() {
    final IntegerAssert assertion = new IntegerAssert(6);
    code(new CodeBlock() {
      public void execute() throws Exception {
        assertion.gt(6);
      }
    }).throwsException(AssertionError.class).message().contains("6");
  }
}
