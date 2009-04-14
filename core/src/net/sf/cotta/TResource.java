package net.sf.cotta;

import java.io.Closeable;

public interface TResource extends Closeable {
  public static final TResource NULL = new TResource() {
    public void close() {
    }
  };

  public void close() throws TIoException;
}
