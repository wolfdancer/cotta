package net.sf.cotta.memory;

import net.sf.cotta.system.FileContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

class InMemoryFileContent implements FileContent {
  private ByteArrayBuffer content;
  private int increment;
  private long lastModified;

  InMemoryFileContent(int initialCapacity, int increment) {
    content = new ByteArrayBuffer(initialCapacity, increment);
    this.increment = increment;
  }

  void setContent(String content) {
    setContent(content, System.currentTimeMillis());
  }

  void setContent(String content, long timestamp) {
    this.content = new ByteArrayBuffer(content.getBytes(), increment);
    this.lastModified = timestamp;
  }

  ByteArrayBuffer getContentBuffer() {
    return content;
  }

  public OutputStream outputStream() {
    lastModified = System.currentTimeMillis();
    return new OutputStream() {

      public void write(int b) {
        content.append((byte) b);
      }

      public void write(byte[] b, int off, int len) throws IOException {
        content.append(b, off, len);
      }

      public void write(byte[] b) throws IOException {
        content.append(b);
      }
    };
  }

  public InputStream inputStream() {
    return new InputStream() {
      private int position = 0;

      public int read() {
        return (position == content.size()) ? -1 : content.byteAt(position++) & 0xFF;
      }
    };
  }

  public FileChannel inputChannel() {
    return new InMemoryInputFileChannel(content);
  }

  public long lastModified() {
    return lastModified;
  }
}
