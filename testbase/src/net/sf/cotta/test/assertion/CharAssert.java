package net.sf.cotta.test.assertion;

public class CharAssert extends BaseAssert<Character, CharAssert> {
  public CharAssert(Character value) {
    super(value);
  }

  public CharAssert eq(int expected) {
    super.eq((char) expected);
    return this;
  }
}
