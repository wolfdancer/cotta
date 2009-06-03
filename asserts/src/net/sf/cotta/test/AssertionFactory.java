package net.sf.cotta.test;

import net.sf.cotta.test.assertion.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The factory class that creates the assertion objects with the given value.  To use this class,
 * add the following static declaration in the base class of your test classes
 * <p/>
 * <code>public static AssertionFactory ensure = new AssertionFactory();</code>
 * <p/>
 * Now, in all your test classes that extend this base class, you can write your assertions
 * in the form of like
 * <p/>
 * <code>ensure.that(actual).eq(expected)</code>
 * <p/>
 * You can expand this assertion API by extending AssertionFactory class as well as the assertion classes
 *
 */
public class AssertionFactory {
  /**
   * Creates the assertion for the given int value
   * @param value int value to assert
   * @return IntegerAssert instance
   */
  public IntegerAssert that(int value) {
    return integer(value);
  }

  /**
   * Creates the assertion for the given int value
   * @param value int value to assert
   * @return IntegerAssert instance
   */
  public IntegerAssert integer(int value) {
    return new IntegerAssert(value);
  }

  /**
   * Creates the assertion for the given object
   * @param value object value to assert
   * @return ObjectAssert instance
   */
  public ObjectAssert that(Object value) {
    return value(value);
  }

  /**
   * Creates the assertion for the given object
   * @param value object value to assert
   * @return ObjectAssert instance
   */
  public ObjectAssert value(Object value) {
    return new ObjectAssert(value);
  }

  /**
   * Creates the assertion for the given code block
   * @param block CodeBlock instance to assert
   * @return CodeBlockAssert instance
   */
  public CodeBlockAssertion that(CodeBlock block) {
    return code(block);
  }

  /**
   * Creates the assertion for the given code block
   * @param block CodeBlock instance to assert
   * @return CodeBlockAssert instance
   */
  public CodeBlockAssertion code(CodeBlock block) {
    return new CodeBlockAssertion(block);
  }

  /**
   * Creates the ListAssert instance for the given array
   * @param value array value to assert
   * @param <T> base type of the array
   * @return ListAssert instance
   */
  public <T> ListAssert<T> that(T[] value) {
    return array(value);
  }

  /**
   * Creates the ListAssert instance for the given array
   * @param value array value to assert
   * @param <T> base type of the array
   * @return ListAssert instance
   */
  public <T> ListAssert<T> array(T[] value) {
    return new ListAssert<T>(value);
  }

  /**
   * Creates the ListAssert instance for the given list
   * @param value List value to assert
   * @param <T> element type of the list
   * @return ListAssert instance
   */
  public <T> ListAssert<T> that(List<T> value) {
    return list(value);
  }

  /**
   * Creates the ListAssert instance for the given list
   * @param value List value to assert
   * @param <T> element type of the list
   * @return ListAssert instance
   */
  public <T> ListAssert<T> list(List<T> value) {
    return new ListAssert<T>(value);
  }

  /**
   * Creates the ListAssert instance for the given iterable
   * instance after converting it to a list
   * @param value iterable to assert
   * @param <T> elemet type of the iterator
   * @return ListAssert instance
   */
  public <T> ListAssert<T> that(Iterable<T> value) {
    return that(value.iterator());
  }

  /**
   * Creates the ListAssert instance for the given iterator
   * instance after converting it to a list
   * @param value iterable to assert
   * @param <T> elemet type of the iterator
   * @return ListAssert instance
   */
  public <T> ListAssert<T> that(Iterator<T> value) {
    return new ListAssert<T>(toList(value));
  }

  private <T> List<T> toList(Iterator<T> value) {
    if (value == null) {
      return null;
    }
    List<T> list = new ArrayList<T>();
    while (value.hasNext()) {
      list.add(value.next());
    }
    return list;
  }

  /**
   * Creates the ByteListAssert instance for the given byte array
   * @param value the byte array for assertion
   * @return ByteListAssert instance
   */
  public ByteListAssert that(byte[] value) {
    return bytes(value);
  }

  /**
   * Creates the ByteListAssert instance for the given byte array
   * @param value the byte array for assertion
   * @return ByteListAssert instance
   */
  public ByteListAssert bytes(byte[] value) {
    return new ByteListAssert(value);
  }

  /**
   * Creates the StringAssert instance for the given byte array
   * by coverting the byte array to String using default encoding
   * @param value the string value to assert
   * @return StringAssert instance
   * @see StringAssert#StringAssert(byte[])
   */
  public StringAssert string(byte[] value) {
    return new StringAssert(value);
  }

  /**
   * Creates the StringAssert instance for the given string
   * @param value the string value to assert
   * @return StringAssert instance
   */
  public StringAssert that(String value) {
    return string(value);
  }

  /**
   * Creates the StringAssert instance for the given string
   * @param value the string value to assert
   * @return StringAssert instance
   */
  public StringAssert string(String value) {
    return new StringAssert(value);
  }


  /**
   * Creates the LongAssert instance for the given long value
   * @param value long value to assert
   * @return LongAssert instance
   */
  public LongAssert that(long value) {
    return longValue(value);
  }

  /**
   * Creates the LongAssert instance for the given long value
   * @param value long value to assert
   * @return LongAssert instance
   */
  public LongAssert longValue(long value) {
    return new LongAssert(value);
  }

  /**
   * Creates the ExceptionAssert instance for the given exception
   * @param value Exception value to assert
   * @return ExceptionAssert instance
   */
  public ExceptionAssert that(Exception value) {
    return exception(value);
  }

  /**
   * Creates the ExceptionAssert instance for the given exception
   * @param value Exception value to assert
   * @return ExceptionAssert instance
   */
  public ExceptionAssert exception(Exception value) {
    return new ExceptionAssert(value);
  }

  /**
   * Creates the BooleanAssert instance for the given boolean value
   * @param value boolean value to assert
   * @return BooleanAssert instance
   */
  public BooleanAssert that(boolean value) {
    return booleanValue(value);
  }

  /**
   * Creates the BooleanAssert instance for the given boolean value
   * @param value boolean value to assert
   * @return BooleanAssert instance
   */
  public BooleanAssert booleanValue(boolean value) {
    return new BooleanAssert(value);
  }

  /**
   * Creates the CharAssert instance for the given char value
   * @param value char value to assert
   * @return CharAssert instance
   */
  public CharAssert that(char value) {
    return character(value);
  }

  /**
   * Creates the CharAssert instance for the given char value
   * @param value char value to assert
   * @return CharAssert instance
   */
  public CharAssert character(char value) {
    return new CharAssert(value);
  }

  /**
   * Creates the CharAssert instance for the given int value
   * after converting it to char
   * @param value int value to assert
   * @return CharAssert instance
   */
  public CharAssert character(int value) {
    return new CharAssert((char) value);
  }

  /**
   * Creates the SetAssert instance of the given list
   * @param value list to assert
   * @param <T> type of the list elements
   * @return SetAssert instance
   */
  public <T> SetAssert<T> set(List<T> value) {
    return new SetAssert<T>(value);
  }

  /**
   * Creates the MapAssert instance of the given map
   * @param value map to assert
   * @param <K> type of the keys in the map
   * @param <V> type of the values in the map
   * @return MapAssert instance
   */
  public <K, V> MapAssert<K, V> map(Map<K, V> value) {
    return new MapAssert<K, V>(value);
  }

  /**
   * Creates the MapAssert instance of the given map
   * @param value map to assert
   * @param <K> type of the keys in the map
   * @param <V> type of the values in the map
   * @return MapAssert instance
   */
  public <K, V> MapAssert<K, V> that(Map<K, V> value) {
    return map(value);
  }
}
