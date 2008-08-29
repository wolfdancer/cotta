package net.sf.cotta;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamStub extends OutputStream {
  private boolean isClosed;

  public void write(int b) throws IOException {
    if (isClosed) {
      throw new IllegalStateException("stream closed");
    }
  }

  public void close() throws IOException {
    super.close();
    isClosed = true;
  }

  public boolean isClosed() {
    return isClosed;
  }
}
