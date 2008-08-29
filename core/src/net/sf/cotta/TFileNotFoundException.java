package net.sf.cotta;

/** @noinspection JavaDoc*/
public class TFileNotFoundException extends TIoException {
  public TFileNotFoundException(TPath path) {
    super(path, "");
  }

}
