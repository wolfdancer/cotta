package net.sf.cotta.memory;

import net.sf.cotta.CottaTestCase;
import net.sf.cotta.TFile;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.TIoException;
import net.sf.cotta.test.TestCase;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class InMemoryInputFileChannelTest extends TestCase {
  private InMemoryFileSystem fileSystem;

  public void beforeMethod() throws Exception {
    fileSystem = new InMemoryFileSystem();
  }

  public void afterMethod() throws TIoException {
    fileSystem = null;
  }

  public void testReadByte() throws Exception {
    String content = "1234567890";
    FileChannel inputChannel = channel(content);
    ByteBuffer dst = ByteBuffer.allocate(20);
    int read = inputChannel.read(dst);
    ensure.that(read).eq(10);
    ensure.that(new String(dst.array(), 0, dst.position())).eq(content);
  }

  private FileChannel channel(String content) throws TIoException {
    TFileFactory factory = new TFileFactory(fileSystem);
    TFile file = factory.file("test/content.txt");
    file.save(content);
    return fileSystem.createInputChannel(file.toPath());
  }

  public void testOnlyReadTheOnesToFillTheBuffer() throws Exception {
    FileChannel inputChannel = channel("1234567890");
    ByteBuffer dst = ByteBuffer.allocate(3);
    ensure.that(inputChannel.read(dst)).eq(3);
    ensure.that(new String(dst.array())).eq("123");
    ensure.that(inputChannel.position()).eq(3);
    ensure.that(inputChannel.read(dst)).eq(0);
  }

  public void testReadBuffers() throws Exception {
    FileChannel inChannel = channel("1234567890");
    ByteBuffer[] dsts = new ByteBuffer[]{
        ByteBuffer.allocate(1),
        ByteBuffer.allocate(1),
        ByteBuffer.allocate(1),
        ByteBuffer.allocate(1)
    };
    ensure.that(inChannel.read(dsts, 1, 2)).eq(2);
    ensure.that(new String(dsts[1].array())).eq("1");
    ensure.that(new String(dsts[2].array())).eq("2");
  }
}
