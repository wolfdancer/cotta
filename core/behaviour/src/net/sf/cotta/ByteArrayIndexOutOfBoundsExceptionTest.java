package net.sf.cotta;

import net.sf.cotta.test.TestBase;

public class ByteArrayIndexOutOfBoundsExceptionTest extends TestBase {
  public void testProvideProperMessage() throws Exception {
    ByteArrayIndexOutOfBoundsException exception = new ByteArrayIndexOutOfBoundsException(1, 9);
    ensure.that(exception).message().eq("Position <1> is out of the bound <9>");
  }
}
