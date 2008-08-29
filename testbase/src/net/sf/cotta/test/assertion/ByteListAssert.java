package net.sf.cotta.test.assertion;

import java.util.ArrayList;
import java.util.List;

public class ByteListAssert extends ListAssert<Byte> {

  public ByteListAssert(byte[] value) {
    this(asList(value));
  }

  private static List<Byte> asList(byte[] value) {
    if (value == null) {
      return null;
    }
    List<Byte> bytes = new ArrayList<Byte>(value.length);
    for (byte item : value) {
      bytes.add(item);
    }
    return bytes;
  }

  public ByteListAssert(List<Byte> value) {
    super(value);
  }

  public void eq(int... expected) {
    List<Byte> bytes = new ArrayList<Byte>(expected.length);
    for (int value : expected) {
      bytes.add((byte) value);
    }
    eq(bytes);
  }

  public void eq(byte... expected) {
    List<Byte> list = new ArrayList<Byte>(expected.length);
    for (byte value : expected) {
      list.add(value);
    }
    eq(list);
  }
}
