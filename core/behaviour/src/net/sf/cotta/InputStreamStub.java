package net.sf.cotta;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamStub extends InputStream {
  private boolean closed;

  public int read() throws IOException {
    return 0;
  }

  public void close() throws IOException {
    super.close();
    closed = true;
  }

  public boolean isClosed() {
    return closed;
  }
}
