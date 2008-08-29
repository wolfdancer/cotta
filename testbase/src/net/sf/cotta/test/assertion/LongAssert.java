package net.sf.cotta.test.assertion;

public class LongAssert extends ObjectAssert<Long> {
  public LongAssert(Long value) {
    super(value);
  }

  public void eq(Integer expected) {
    super.eq(expected == null ? null : new Long(expected));
  }
}
