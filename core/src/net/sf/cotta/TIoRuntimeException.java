package net.sf.cotta;

public class TIoRuntimeException extends RuntimeException {
  public TIoRuntimeException(TIoException e) {
    super(e.getMessage(), e);
  }
}
