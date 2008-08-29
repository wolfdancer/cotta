package net.sf.cotta;

import java.io.IOException;

/**
 * The exception that wraps java.io.IOException.  This should be the exception
 * used throughout the client of Cotta, because it will be converted to
 * extend RuntimeException in the future.
 */
public class TIoException extends IOException {
  private TPath path;

  public TIoException(TPath path, String message) {
    this(path, message, null);
  }

  public TIoException(TPath path, String message, IOException cause) {
    super(message + "<" + path + ">");
    this.path = path;
    initCause(cause);
  }

  public TPath getPath() {
    return path;
  }
}
