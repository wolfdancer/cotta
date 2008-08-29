package net.sf.cotta;

/** @noinspection JavaDoc*/
public interface TResource {
  public static final TResource NULL = new TResource() {
    public void close() {
    }
  };

  public void close() throws TIoException;
}
