package net.sf.cotta.io;

import java.io.IOException;

/** @noinspection JavaDoc*/
public interface IoProcessor {
  public void process(IoManager io) throws IOException;
}
