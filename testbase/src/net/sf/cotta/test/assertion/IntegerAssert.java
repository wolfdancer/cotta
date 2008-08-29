package net.sf.cotta.test.assertion;

import net.sf.cotta.test.matcher.GreaterThanMatecher;
import net.sf.cotta.test.matcher.LessThanMatcher;

public class IntegerAssert extends ObjectAssert<Integer> {
  public IntegerAssert(int value) {
    super(value);
  }

  public IntegerAssert lessThan(int expected) {
    matches(new LessThanMatcher<Integer, Integer>(expected));
    return this;
  }

  public IntegerAssert greaterThan(int expected) {
    matches(new GreaterThanMatecher<Integer, Integer>(expected));
    return this;
  }

}
