package net.sf.cotta.test.assertion;

public class BaseAssertTest extends TestCase {
  public void testNotSameAsSucceedsWhenInstancesAreNotTheSame() {
    BaseAssert<Object, BaseAssert> assertion = new BaseAssert<Object, BaseAssert>(1);
    assertion.notSameAs(2);
  }
}
