package net.sf.cotta.io;

import net.sf.cotta.TIoException;

public interface Output {
  public void write(OutputProcessor processor) throws TIoException;
}
