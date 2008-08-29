package net.sf.cotta.memory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;

class InMemoryInputFileChannel extends FileChannel {
  private long position = 0;
  private ByteArrayBuffer content;

  public InMemoryInputFileChannel(ByteArrayBuffer content) {
    this.content = content;
  }

  public int read(ByteBuffer dst) throws IOException {
    int copied = read(dst, position);
    position = position + copied;
    return copied;
  }

  public int read(ByteBuffer dst, long position) throws IOException {
    int available = dst.limit() - dst.position();
    if (available == 0) {
      return 0;
    }
    if (position >= size()) {
      return -1;
    }
    long end = Math.min(content.size(), position + available);
    int copied = (int) (end - position);
    content.copyTo(dst, (int) position, copied);
    return copied;
  }

  public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
    int readCount = 0;
    for (int i = offset; i < offset + length; i++) {
      if (position == content.size()) {
        break;
      }
      readCount += read(dsts[i]);
    }
    return readCount;
  }

  private NonWritableChannelException nonWritableChannelException() {
    //noinspection ThrowableInstanceNeverThrown
    return new NonWritableChannelException();
  }

  public int write(ByteBuffer src) throws IOException {
    throw nonWritableChannelException();
  }

  public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
    throw nonWritableChannelException();
  }

  public long position() throws IOException {
    return position;
  }

  public FileChannel position(long newPosition) throws IOException {
    this.position = newPosition;
    return this;
  }

  public long size() throws IOException {
    return content.size();
  }

  public FileChannel truncate(long size) throws IOException {
    throw nonWritableChannelException();
  }

  public void force(boolean metaData) throws IOException {
    // no op since I have no idea what to do
  }

  public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
    return content.copyTo(target, position, count);
  }

  public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
    throw nonWritableChannelException();
  }

  public int write(ByteBuffer src, long position) throws IOException {
    throw nonWritableChannelException();
  }

  public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
    if (mode == MapMode.READ_WRITE || mode == MapMode.PRIVATE) {
      throw new NonWritableChannelException();
    }
    throw new UnsupportedOperationException("map is not supported for in-memory input file channel because MappedByteBuffer can not be instantiated directly from outside the package");
  }

  public FileLock lock(long position, long size, boolean shared) throws IOException {
    throw new UnsupportedOperationException("lock is not supported for in-memory input file channel because tryLock is not supported");
  }

  public FileLock tryLock(long position, long size, boolean shared) throws IOException {
    throw new UnsupportedOperationException("tryLock is not supported for in-memory input file channel because FileLock can not be instantiated from outside the package");
  }

  protected void implCloseChannel() throws IOException {
  }
}
