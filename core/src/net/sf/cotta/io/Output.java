package net.sf.cotta.io;

import net.sf.cotta.TIoException;

public class Output {
  private OutputManager manager;

  public Output(OutputManager manager) {
    this.manager = manager;
  }

  public void write(OutputProcessor processor) throws TIoException {
    manager.open(processor);
  }
}
