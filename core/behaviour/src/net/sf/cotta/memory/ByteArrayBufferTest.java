package net.sf.cotta.memory;

import net.sf.cotta.ByteArrayIndexOutOfBoundsException;
import net.sf.cotta.CottaTestCase;

import java.nio.ByteBuffer;

public class ByteArrayBufferTest extends CottaTestCase {

  public void testProvideToByteArray() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer("test".getBytes());
    ensureEquals(new String(buffer.toByteArray()), "test");
    ensure.that(buffer.size()).eq(4);
  }

  public void testAppendByte() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    buffer.append((byte) 1);
    buffer.append((byte) 2);
    ensure.that(buffer.size()).eq(2);
    byte[] actual = buffer.toByteArray();
    ensure.bytes(actual).eq(1, 2);
  }

  public void testIncreaseCapacityAutomatically() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(1);
    buffer.append((byte) 0);
    buffer.append((byte) 1);
    buffer.append((byte) 2);
    ensure.bytes(buffer.toByteArray()).eq(0, 1, 2);
  }

  public void testProvideByteAtAnyPosition() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    buffer.append((byte) 0);
    buffer.append((byte) 1);
    buffer.append((byte) 2);
    buffer.append((byte) 3);
    ensure.that(buffer.byteAt(2)).eq(2);
  }

  public void testThrowExceptionWithDetailedInformationIfPositionProvidedIsTooBig() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    buffer.append((byte) 3);
    try {
      buffer.byteAt(5);
      fail("ByteArrayIndexOutOfBoundsException should have been thrown");
    } catch (ByteArrayIndexOutOfBoundsException e) {
      ensure.that(e.getPosition()).eq(5);
      ensure.that(e.getBound()).eq(1);
    }
  }

  public void testThrowExceptionWithDetailedInformationIfPositionProvidedIsNegative() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    try {
      buffer.byteAt(-1);
      fail("ByteArrayIndexOutOfBoundsException should have been thrown");
    } catch (ByteArrayIndexOutOfBoundsException e) {
      ensure.that(e.getPosition()).eq(-1);
      ensure.that(e.getBound()).eq(0);
    }
  }

  public void testAppendString() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(20);
    buffer.append("test");
    buffer.append("test");
    ensure.that(buffer.toString()).eq("testtest");
  }

  public void testEncreaseCapacityAutomatically() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(2, 1);
    buffer.append("test encreament");
    ensure.that(buffer.toString()).eq("test encreament");
  }

  public void testCopyFromBytBuffer() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    buffer.append("test");
    ByteBuffer source = ByteBuffer.allocate(6);
    source.put("source".getBytes());
    source.rewind();
    int copied = buffer.copyFrom(source);
    ensure.integer(copied).eq(source.limit());
    ensure.string(buffer.toString()).eq("testsource");
  }

  public void testCopyToByteBuffer() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer();
    buffer.append("test");
    ByteBuffer target = ByteBuffer.allocate(8);
    target.put("source".getBytes());
    long copied = buffer.copyTo(target, 1, 2);
    ensure.longValue(copied).eq(2);
    ensure.string(target.array()).eq("sourcees");
  }

  public void testCopyOnlyAvailableBytesToBuffer() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(30);
    buffer.append("test");
    ByteBuffer target = ByteBuffer.allocate(30);
    ensure.longValue(buffer.copyTo(target, 1, 20)).eq(3);
    ensure.string(new String(target.array(), 0, target.position())).eq("est");
  }

  public void testCopyZeroBytesWhenPositionPassesLimitsForByteBuffer() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(20);
    buffer.append("123");
    ensure.longValue(buffer.copyTo(ByteBuffer.allocate(5), 10, 5)).eq(0);
  }

  public void testCopyZeroBytesWhenPositionPassesLimitsForChannel() throws Exception {
    ByteArrayBuffer buffer = new ByteArrayBuffer(20);
    buffer.append("123");
    ensure.longValue(buffer.copyTo(new InMemoryOutputFileChannel(new ByteArrayBuffer(20)), 10, 5)).eq(0);
  }
}
