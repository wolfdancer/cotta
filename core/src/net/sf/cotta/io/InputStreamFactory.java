package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.InputStream;
import java.nio.channels.FileChannel;

public interface InputStreamFactory {
  InputStream inputStream() throws TIoException;

  FileChannel inputChannel() throws TIoException;

  TPath path();
}
