package net.sf.cotta.io;

import java.io.IOException;

public interface InputProcessor {
  void process(InputManager inputManager) throws IOException;
}
