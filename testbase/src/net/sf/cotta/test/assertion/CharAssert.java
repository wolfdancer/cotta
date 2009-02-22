package net.sf.cotta.test.assertion;

public class CharAssert extends BaseAssert<Character> {
  public CharAssert(Character value) {
    super(value);
  }

  public CharAssert eq(int expected) {
    super.eq(new Character((char) expected));
    return this;
  }
}
