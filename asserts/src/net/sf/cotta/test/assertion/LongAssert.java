package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class LongAssert extends BaseAssert<Long, LongAssert> {
  public LongAssert(Long value) {
    super(value);
  }

  public LongAssert eq(Integer expected) {
    super.eq(expected == null ? null : (long) expected.intValue());
    return this;
  }

  public LongAssert ge(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {

      public boolean matches(Object o) {
        return (Long) o >= value;
      }

      public void describeTo(Description description) {
        description.appendText("greater or equal to ").appendValue(value);
      }
    });
    return this;
  }

  public LongAssert lt(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {
      public boolean matches(Object o) {
        return (Long) o < value;
      }

      public void describeTo(Description description) {
        description.appendText("less than ").appendValue(value);
      }
    });
    return this;
  }

  public LongAssert gt(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {
      public boolean matches(Object o) {
        return (Long) o > value;
      }

      public void describeTo(Description description) {
        description.appendText("greater than ").appendValue(value);
      }
    });
    return this;
  }
}
