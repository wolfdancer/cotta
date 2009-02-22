package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;

/**
 * Basic assertion class to be extended by all.  Type T is the type of the value
 * under assertion, and type A is the assertion class (to be used for returning self)
 *
 * @param <T> Type of the value under assertion
 * @param <A> Type of the current assertion class
 */
public class BaseAssert<T, A> extends Assert {
  private String description = "";
  private T value;

  public BaseAssert(T value) {
    this.value = value;
  }

  @SuppressWarnings({"unchecked"})
  protected A self() {
    return (A) this;
  }

  public A describedAs(String description) {
    this.description = description;
    return self();
  }

  public T value() {
    return this.value;
  }

  @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException("equals method is not supported by assertion, you probably wanted to use eq method");
  }

  public A eq(T expected) {
    matches(IsEqual.equalTo(expected));
    return self();
  }

  public A matches(Matcher<T> matcher) {
    assertThat(description, value(), matcher);
    return self();
  }

  public A sameAs(T actual) {
    matches(Matchers.sameInstance(actual));
    return self();
  }

  public A notNull() {
    matches(IsNot.not(new IsNull<T>()));
    return self();
  }

  public A not(Matcher<T> matcher) {
    matches(Matchers.not(matcher));
    return self();
  }

  public A isNull() {
    matches(Matchers.<T>nullValue());
    return self();
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

  public A eqWithHash(T expected) {
    matches(JUnitMatchers.both(IsEqual.equalTo(expected)).and(hashEq(expected)));
    return self();
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
