package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;

import java.util.Arrays;
import java.util.List;

public class ListAssert<T> extends BaseAssert<List<T>, ListAssert<T>> {
  public ListAssert(T[] value) {
    super(value == null ? null : Arrays.asList(value));
  }

  public ListAssert(List<T> value) {
    super(value);
  }

  public ListAssert<T> eq(T... expected) {
    eq(Arrays.asList(expected));
    return this;
  }

  public ListAssert<T> contains(T... expected) {
    iterableMatches(Matchers.hasItems(expected));
    return this;
  }

  private void iterableMatches(Matcher<Iterable<T>> matcher) {
    assertThat(value(), matcher);
  }

  public ListAssert<T> isEmpty() {
    matches(matcherIsEmpty());
    return this;
  }

  private BaseMatcher<List<T>> matcherIsEmpty() {
    return new BaseMatcher<List<T>>() {

      @SuppressWarnings({"unchecked"})
      public boolean matches(Object item) {
        return ((List<T>) item).isEmpty();
      }

      public void describeTo(Description description) {
        description.appendText("list should be empty");
      }
    };
  }

  public T hasOneItem() {
    hasSize(1);
    return value().get(0);
  }

  public ListAssert<T> hasSize(int expected) {
    isOfSize(expected);
    return this;
  }

  public ListAssert<T> isOfSize(final int expected) {
    matches(new BaseMatcher<List<T>>() {
      @SuppressWarnings({"unchecked"})
      public boolean matches(Object o) {
        return ((List<T>) o).size() == expected;
      }

      public void describeTo(Description description) {
        description.appendText("list should have size of <").appendValue(expected).appendText(">");
      }
    });
    return this;
  }

  public ListAssert<T> notEmpty() {
    matches(IsNot.not(matcherIsEmpty()));
    return this;
  }
}
