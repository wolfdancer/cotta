package net.sf.cotta.system;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class DummyFileContent implements FileContent {
  public OutputStream outputStream() {
    return null;
  }

  public InputStream inputStream() {
    return null;
  }

  public FileChannel inputChannel() {
    return null;
  }

  public long lastModified() {
    return -1;
  }
}
