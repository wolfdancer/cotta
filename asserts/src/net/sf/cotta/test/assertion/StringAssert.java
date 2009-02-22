package net.sf.cotta.test.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.matchers.JUnitMatchers;

import java.util.ArrayList;
import java.util.List;

public class StringAssert extends BaseAssert<String, StringAssert> {
  public StringAssert(String value) {
    super(value);
  }

  public StringAssert(byte[] value) {
    this(value == null ? null : new String(value));
  }

  public StringAssert isNotEmpty() {
    not(empty());
    return this;
  }

  public StringAssert isEmpty() {
    matches(empty());
    return this;
  }

  private BaseMatcher<String> empty() {
    return new BaseMatcher<String>() {
      public boolean matches(Object o) {
        String string = (String) o;
        return string != null && string.length() == 0;
      }

      public void describeTo(Description description) {
        description.appendText("empty string");
      }
    };
  }

  public StringAssert contains(String... expectedValues) {
    Matcher<String> matcher = expectedValues.length == 1 ?
            JUnitMatchers.containsString(expectedValues[0]) :
            allof(expectedValues);
    matches(matcher);
    return this;
  }

  private Matcher<String> allof(String[] expectedValues) {
    List<Matcher<? extends String>> matchers = new ArrayList<Matcher<? extends String>>(expectedValues.length);
    for (String item : expectedValues) {
      matchers.add(JUnitMatchers.containsString(item));
    }
    return new AllOf<String>(matchers);
  }

}
