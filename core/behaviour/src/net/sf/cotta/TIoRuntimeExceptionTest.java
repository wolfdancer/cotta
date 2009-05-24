package net.sf.cotta;

public class TIoRuntimeExceptionTest extends TestCase {
  public void testWrapException() {
    TPath path = TPath.parse("/test");
    TIoException exception = new TIoException(path, "test");
    TIoRuntimeException runtimeException = new TIoRuntimeException(exception);
    ensure.that(runtimeException.getMessage()).eq(exception.getMessage());
    ensure.that(runtimeException.getPath()).eq(exception.getPath());
  }
}
