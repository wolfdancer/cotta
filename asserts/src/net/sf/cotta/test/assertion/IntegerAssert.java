package net.sf.cotta.test.assertion;

import net.sf.cotta.test.matcher.GreaterThanMatecher;
import net.sf.cotta.test.matcher.LessThanMatcher;

public class IntegerAssert extends BaseAssert<Integer, IntegerAssert> {
  public IntegerAssert(int value) {
    super(value);
  }

  public IntegerAssert lt(int expected) {
    matches(new LessThanMatcher<Integer, Integer>(expected));
    return this;
  }

  public IntegerAssert gt(int expected) {
    matches(new GreaterThanMatecher<Integer, Integer>(expected));
    return this;
  }

}
