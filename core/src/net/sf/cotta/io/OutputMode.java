package net.sf.cotta.io;

/** @noinspection JavaDoc*/
public class OutputMode {
  public static final OutputMode APPEND = new OutputMode(true);
  public static final OutputMode OVERWRITE = new OutputMode(false);
  private boolean isAppend;

  public OutputMode(boolean isAppend) {
    this.isAppend = isAppend;
  }

  public boolean isAppend() {
    return isAppend;
  }

  public boolean isOverwrite() {
    return !isAppend;
  }
}
