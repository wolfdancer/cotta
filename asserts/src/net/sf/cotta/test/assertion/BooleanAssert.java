package net.sf.cotta.test.assertion;

public class BooleanAssert extends BaseAssert<Boolean, BooleanAssert> {
  public BooleanAssert(Boolean value) {
    super(value);
  }

  public BooleanAssert isFalse() {
    eq(false);
    return this;
  }

  public BooleanAssert isTrue() {
    eq(true);
    return this;
  }
}
