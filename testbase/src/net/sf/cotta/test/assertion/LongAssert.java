package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class LongAssert extends BaseAssert<Long> {
  public LongAssert(Long value) {
    super(value);
  }

  public void eq(Integer expected) {
    super.eq(expected == null ? null : (long) expected.intValue());
  }

  public void ge(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {

      public boolean matches(Object o) {
        return (Long) o >= value;
      }

      public void describeTo(Description description) {
        description.appendText("greater or equal to ").appendValue(value);
      }
    });
  }

  public void lt(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {
      public boolean matches(Object o) {
        return (Long) o < value;
      }

      public void describeTo(Description description) {
        description.appendText("less than ").appendValue(value);
      }
    });
  }

  public void gt(final long value) {
    notNull();
    matches(new BaseMatcher<Long>() {
      public boolean matches(Object o) {
        return (Long) o > value;
      }

      public void describeTo(Description description) {
        description.appendText("greater than ").appendValue(value);
      }
    });
  }
}
