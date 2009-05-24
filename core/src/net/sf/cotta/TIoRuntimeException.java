package net.sf.cotta;

/**
 * Runtime exception used to wrap TIoException in the place where you cannot declare exception
 */
public class TIoRuntimeException extends RuntimeException {
  public TIoRuntimeException(TIoException e) {
    super(e.getMessage(), e);
  }

  public TPath getPath() {
    return ((TIoException) getCause()).getPath();
  }
}
