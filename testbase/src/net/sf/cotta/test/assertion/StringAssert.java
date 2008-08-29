package net.sf.cotta.test.assertion;

import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import static org.junit.matchers.StringContains.containsString;

import java.util.ArrayList;
import java.util.List;

public class StringAssert extends ObjectAssert<String> {
  public StringAssert(String value) {
    super(value);
  }

  public StringAssert(byte[] value) {
    this(value == null ? null : new String(value));
  }

  public void contains(String... expectedValues) {
    Matcher<String> matcher = expectedValues.length == 1 ?
        containsString(expectedValues[0]) :
        allof(expectedValues);
    matches(matcher);
  }

  private Matcher<String> allof(String[] expectedValues) {
    List<Matcher<? extends String>> matchers = new ArrayList<Matcher<? extends String>>(expectedValues.length);
    for (String item : expectedValues) {
      matchers.add(containsString(item));
    }
    return new AllOf<String>(matchers);
  }

}
