package net.sf.cotta.test.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class GreaterThanMatecher<T extends Comparable<K>, K> extends BaseMatcher<T> {
  private T value;

  public GreaterThanMatecher(T value) {
    this.value = value;
  }

  @SuppressWarnings({"unchecked"})
  public boolean matches(Object o) {
    return value.compareTo((K) o) < 0;
  }

  public void describeTo(Description description) {
    description.appendText("greater than ").appendValue(value);
  }
}
