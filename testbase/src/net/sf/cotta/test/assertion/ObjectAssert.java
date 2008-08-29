package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.matchers.CombinableMatcher;

public class ObjectAssert<T> extends Assert {
  private String description = "";
  private T value;

  public ObjectAssert(T value) {
    this.value = value;
  }

  public ObjectAssert<T> describedAs(String description) {
    this.description = description;
    return this;
  }

  public T value() {
    return this.value;
  }

  public ObjectAssert<T> eq(T expected) {
    matches(IsEqual.equalTo(expected));
    return this;
  }

  public void matches(Matcher<T> matcher) {
    assertThat(description, value(), matcher);
  }

  public void sameAs(T actual) {
    matches(Matchers.sameInstance(actual));
  }

  public void notNull() {
    matches(IsNot.not(new IsNull<T>()));
  }

  public void not(Matcher<T> matcher) {
    matches(Matchers.not(matcher));
  }

  public void isNull() {
    matches(Matchers.<T>nullValue());
  }

  @SuppressWarnings({"unchecked"})
  public <K> K isA(Class<K> expectedClass) {
    matches(instanceOf(expectedClass));
    return (K) expectedClass;
  }

  private <K> Matcher<T> instanceOf(final Class<K> expectedClass) {
    return new BaseMatcher<T>() {
      public boolean matches(Object item) {
        return expectedClass.isInstance(item);
      }

      public void describeTo(Description description) {
        description.appendText("an instance of ").appendText(expectedClass.getName());
      }
    };
  }

  public void javaEquals(T expected) {
    matches(new CombinableMatcher<T>(IsEqual.equalTo(expected))
        .and(hashEq(expected))
    );
  }

  private Matcher<T> hashEq(final T expected) {
    return new BaseMatcher<T>() {
      public boolean matches(Object o) {
        return value().hashCode() == expected.hashCode();
      }

      public void describeTo(Description description) {
        description.appendText("same hash code");
      }
    };
  }
}
