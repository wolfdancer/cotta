package net.sf.cotta;

/** @noinspection JavaDoc*/
public class TDirectoryNotFoundException extends TIoException {

  public TDirectoryNotFoundException(TPath path) {
    super(path, "");
  }

}
