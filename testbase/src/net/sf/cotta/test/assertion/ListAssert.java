package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.List;

public class ListAssert<T> extends ObjectAssert<List<T>> {
  public ListAssert(T[] value) {
    super(value == null ? null : Arrays.asList(value));
  }

  public ListAssert(List<T> value) {
    super(value);
  }

  public void eq(T... expected) {
    eq(Arrays.asList(expected));
  }

  public void contains(T... expected) {
    iterableMatches(Matchers.hasItems(expected));
  }

  private void iterableMatches(Matcher<Iterable<T>> matcher) {
    assertThat(value(), matcher);
  }

  public void isEmpty() {
    matches(new BaseMatcher<List<T>>() {

      @SuppressWarnings({"unchecked"})
      public boolean matches(Object item) {
        return ((List<T>) item).isEmpty();
      }

      public void describeTo(Description description) {
        description.appendText("list should be empty");
      }
    });
  }

  public T hasOneItem() {
    hasSize(1);
    return value().get(0);
  }

  public void hasSize(final int expected) {
    matches(new BaseMatcher<List<T>>() {
      @SuppressWarnings({"unchecked"})
      public boolean matches(Object o) {
        return ((List<T>)o).size() == expected;
      }

      public void describeTo(Description description) {
        description.appendText("list should have size of <").appendValue(expected).appendText(">");
      }
    });
  }
}
