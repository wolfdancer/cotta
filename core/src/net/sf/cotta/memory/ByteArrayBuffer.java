package net.sf.cotta.memory;

import net.sf.cotta.ByteArrayIndexOutOfBoundsException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Faster performing ByteArrayBuffer using the ByteArrayOutputStream as a
 * backing up stream. Writes 400MB in 44 seconds. Requires approx 2x memory
 * of the file size, eg for 400MB set -Xmx1024m.
 * <p/>
 * Contributed by:
 * Sergey Abramov
 * http://coldcore.com
 */
public class ByteArrayBuffer {

  private int size = 0;
  public static final int INCREMENT = 8192;

  /**
   * Flush the backing up stream into the byte array at
   * every 10MB (optimal performance)
   */
  private static final int FLUSH_AT = 1024 * 1024 * 10;

  /**
   * The byte buffer where the backing up stream flushes its data.
   */
  private byte[] buffer;

  /**
   * All the data is written into this backing up stream, which is
   * then flushed into the byte buffer.
   */
  private ByteArrayOutputStream bout;


  public ByteArrayBuffer(byte[] content) {
    this(content, INCREMENT);
  }


  public ByteArrayBuffer(byte[] content, int increament) {
    this.buffer = new byte[content.length];
    System.arraycopy(content, 0, buffer, 0, content.length);
    size = content.length;
    bout = new ByteArrayOutputStream(increament);
  }


  public ByteArrayBuffer() {
    this(INCREMENT);
  }


  public ByteArrayBuffer(int initialCapacity) {
    this(initialCapacity, INCREMENT);
  }

  public ByteArrayBuffer(int initialCapacity, int increment) {
    buffer = new byte[initialCapacity];
    bout = new ByteArrayOutputStream(increment);
  }


  /**
   * Flush data from the backing up stream into the byte buffer.
   */
  private void flush() {
    int bsize = bout.size();
    if (bsize > 0) {
      if (size > buffer.length) increaseCapacity(size);
      System.arraycopy(bout.toByteArray(), 0, buffer, size - bsize, bsize);
      bout.reset();
    }
  }


  public byte[] toByteArray() {
    flush();
    byte[] result = new byte[size];
    System.arraycopy(buffer, 0, result, 0, size);
    return result;
  }


  public ByteArrayBuffer append(byte b) {
    bout.write(b);
    size++;
    if (size % FLUSH_AT == 0) flush();
    return this;
  }

  /**
   * Faster performing method, output stream should delegate to it (comment above).
   */
  public ByteArrayBuffer append(byte[] b, int off, int len) {
    bout.write(b, off, len);
    size += len;
    if (size % FLUSH_AT == 0) flush();
    return this;
  }


  /**
   * Faster performing method, output stream should delegate to it (comment above).
   */
  public ByteArrayBuffer append(byte[] b) {
    return append(b, 0, b.length);
  }


  /**
   * This works for the default charset only:
   * <p/>
   * String str = ""+(char)1077+(char)1078;     //UTF-8
   * System.out.println((int)str.charAt(0));    //1077
   * str = new String(str.getBytes(), "UTF-8");
   * System.out.println((int)str.charAt(0));    //63, no UTF-8 any longer
   */
  public void append(String value) throws IOException {
    byte[] bytes = value.getBytes();
    append(bytes);
  }


  /**
   * This one converts to the proper charset.
   */
  public void append(String value, String charsetName) throws IOException {
    byte[] bytes = value.getBytes(charsetName);
    append(bytes);
  }


  private void increaseCapacity(int targetCapacity) {
    byte[] newBuffer = new byte[targetCapacity];
    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
    buffer = newBuffer;
  }


  public byte byteAt(long position) {
    return byteAt((int) position);
  }


  public byte byteAt(int position) {
    if (position < 0 || position >= size) {
      throw new ByteArrayIndexOutOfBoundsException(position, size);
    }
    flush();
    return buffer[position];
  }


  public int size() {
    return size;
  }


  public int copyFrom(ByteBuffer src) {
    flush();
    int sizeToCopy = src.remaining();
    int resultingSize = sizeToCopy + size;
    if (buffer.length < resultingSize) {
      increaseCapacity(resultingSize);
    }
    src.get(buffer, size, sizeToCopy);
    size += sizeToCopy;
    return sizeToCopy;
  }


  public long copyTo(ByteBuffer dst, int start, int count) {
    int end = Math.min(size, start + count);
    int sizeToCopy = end - start;
    if (sizeToCopy <= 0) {
      return 0;
    }
    flush();
    dst.put(buffer, start, sizeToCopy);
    return sizeToCopy;
  }


  public long copyTo(WritableByteChannel target, long position, long count) throws IOException {
    long end = Math.min(size, position + count);
    long sizeToCopy = end - position;
    if (sizeToCopy <= 0) {
      return 0;
    }
    ByteBuffer buffer = ByteBuffer.allocate((int) sizeToCopy);
    copyTo(buffer, (int) position, (int) (count));
    buffer.rewind();
    return target.write(buffer);
  }


  /**
   * This method returns a string using the default charset.
   */
  public String toString() {
    return new String(toByteArray());
  }


  /**
   * This method returns a string converted to the proper charset.
   */
  public String toString(String charsetName) throws UnsupportedEncodingException {
    return new String(toByteArray(), charsetName);
  }
}