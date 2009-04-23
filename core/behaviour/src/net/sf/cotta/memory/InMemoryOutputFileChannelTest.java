package net.sf.cotta.memory;

import net.sf.cotta.TestCase;

import java.nio.ByteBuffer;

public class InMemoryOutputFileChannelTest extends TestCase {
  public void testWrite() throws Exception {
    InMemoryOutputFileChannel channel = new InMemoryOutputFileChannel(new ByteArrayBuffer());
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.put("test".getBytes()).rewind();
    channel.write(buffer);
    ensure.inMemoryOutput(channel).hasContent("test");
  }
}
