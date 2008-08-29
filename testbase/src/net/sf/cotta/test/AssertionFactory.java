package net.sf.cotta.test;

import net.sf.cotta.test.assertion.*;

import java.util.List;
import java.util.Map;

public class AssertionFactory {
  public IntegerAssert that(int value) {
    return integer(value);
  }

  public IntegerAssert integer(int value) {
    return new IntegerAssert(value);
  }

  public <T> ObjectAssert<T> that(T value) {
    return object(value);
  }

  public <T extends ObjectAssert<?>> T that(T asserts) {
    return asserts;
  }

  public <T> ObjectAssert<T> object(T value) {
    return new ObjectAssert<T>(value);
  }

  public CodeBlockAssertion that(CodeBlock block) {
    return code(block);
  }

  public CodeBlockAssertion code(CodeBlock block) {
    return new CodeBlockAssertion(block);
  }

  public <T> ListAssert<T> that(T[] value) {
    return array(value);
  }

  public <T> ListAssert<T> array(T[] value) {
    return new ListAssert<T>(value);
  }

  public <T> ListAssert<T> that(List<T> value) {
    return list(value);
  }

  public <T> ListAssert<T> list(List<T> value) {
    return new ListAssert<T>(value);
  }

  public ByteListAssert that(byte[] value) {
    return bytes(value);
  }

  public ByteListAssert bytes(byte[] value) {
    return new ByteListAssert(value);
  }

  public StringAssert string(byte[] value) {
    return new StringAssert(value);
  }

  public LongAssert that(long value) {
    return longValue(value);
  }

  public LongAssert longValue(long value) {
    return new LongAssert(value);
  }

  public StringAssert that(String value) {
    return string(value);
  }

  public StringAssert string(String value) {
    return new StringAssert(value);
  }

  public ExceptionAssert that(Exception value) {
    return exception(value);
  }

  public ExceptionAssert exception(Exception value) {
    return new ExceptionAssert(value);
  }

  public BooleanAssert that(boolean value) {
    return booleanValue(value);
  }

  public BooleanAssert booleanValue(boolean value) {
    return new BooleanAssert(value);
  }

  public CharAssert that(char value) {
    return character(value);
  }

  public CharAssert character(char value) {
    return new CharAssert(value);
  }

  public CharAssert character(int value) {
    return new CharAssert((char) value);
  }

  public <T> SetAssert<T> set(List<T> list) {
    return new SetAssert<T>(list);
  }

  public <K, V> MapAssert<K, V> map(Map<K, V> value) {
    return new MapAssert<K, V>(value);
  }

  public <K, V> MapAssert<K, V> that(Map<K, V> value) {
    return map(value);
  }
}
