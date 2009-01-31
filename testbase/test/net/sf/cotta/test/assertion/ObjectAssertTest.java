package net.sf.cotta.test.assertion;

import net.sf.cotta.test.TestCase;

public class ObjectAssertTest extends TestCase {
  public void testDescribeAs() {
    Object instance = new Object();
    final ObjectAssert<Object> assertion = new ObjectAssert<Object>(instance).describedAs("description");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        assertion.sameAs(new Object());
      }
    }).throwsException(AssertionError.class).message().contains("description");
  }

  public void testOverrideEqualsToThrowException() {
    final Object instance = new Object();
    final ObjectAssert<Object> assertion = ensure.object(instance);
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        assertion.equals(instance);
      }
    }).throwsException(UnsupportedOperationException.class);
  }
}
