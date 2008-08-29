package net.sf.cotta;

/** @noinspection JavaDoc*/
public class ByteArrayIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {
  private int position;
  private int bound;

  public ByteArrayIndexOutOfBoundsException(int position, int bound) {
    super("Position <" + position + "> is out of the bound <" + bound + ">");
    this.position = position;
    this.bound = bound;
  }

  public int getPosition() {
    return position;
  }

  public int getBound() {
    return bound;
  }
}
