package net.sf.cotta;

import net.sf.cotta.io.InputManager;

import java.io.IOException;

public interface Parser<T> {
  T parse(InputManager input) throws IOException;
}
