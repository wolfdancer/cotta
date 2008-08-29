package net.sf.cotta.test.assertion;

public class ExceptionAssert extends ObjectAssert<Throwable> {
  public ExceptionAssert(Throwable value) {
    super(value);
  }

  @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
  public StringAssert message() {
    return new StringAssert(value().getMessage());
  }
}
