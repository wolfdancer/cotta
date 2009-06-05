package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.OutputStream;

public interface OutputStreamFactory {
  OutputStream outputStream() throws TIoException;

  TPath path();
}
