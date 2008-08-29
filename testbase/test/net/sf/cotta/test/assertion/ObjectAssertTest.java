package net.sf.cotta.test.assertion;

import net.sf.cotta.test.TestBase;

public class ObjectAssertTest extends TestBase {
  public void testDescribeAs() {
    Object instance = new Object();
    final ObjectAssert<Object> assertion = new ObjectAssert<Object>(instance).describedAs("description");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        assertion.sameAs(new Object());
      }
    }).throwsException(AssertionError.class).message().contains("description");
  }
}
