package net.sf.cotta.io;

import java.io.IOException;

public interface OutputProcessor {
  void process(OutputManager manager) throws IOException;
}
