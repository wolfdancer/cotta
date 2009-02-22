package net.sf.cotta.test.assertion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetAssert<T> extends BaseAssert<Set<T>, SetAssert<T>> {
  public SetAssert(Set<T> value) {
    super(value);
  }

  public SetAssert(List<T> value) {
    this(value == null ? null : new HashSet<T>(value));
  }

  public SetAssert<T> eq(T... values) {
    eq(new HashSet<T>(Arrays.asList(values)));
    return this;
  }
}
