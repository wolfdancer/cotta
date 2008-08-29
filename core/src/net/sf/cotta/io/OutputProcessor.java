package net.sf.cotta.io;

import java.io.IOException;

public interface OutputProcessor {
  void process(OutputManager outputManager) throws IOException;
}
