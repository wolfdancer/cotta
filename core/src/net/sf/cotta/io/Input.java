package net.sf.cotta.io;

import net.sf.cotta.TIoException;

public interface Input {
  public void read(InputProcessor processor) throws TIoException;
}
