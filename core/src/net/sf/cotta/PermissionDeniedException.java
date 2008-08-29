package net.sf.cotta;

/** @noinspection JavaDoc*/
public class PermissionDeniedException extends TIoException {

  public PermissionDeniedException(TPath path, String message) {
    super(path, message);
  }
}
