package net.sf.cotta;

import java.io.IOException;

/**
 * Runtime exception used to wrap TIoException in the place where you cannot declare exception
 */
public class TIoRuntimeException extends RuntimeException {
  private TPath path;

  public TIoRuntimeException(TIoException e) {
    super(e.getMessage(), e);
    this.path = e.getPath();
  }

  public TIoRuntimeException(String message, TPath path, IOException cause) {
    super(message + "<" + path + ">");
    this.path = path;
    initCause(cause);
  }

  public TPath getPath() {
    return path;
  }
}
