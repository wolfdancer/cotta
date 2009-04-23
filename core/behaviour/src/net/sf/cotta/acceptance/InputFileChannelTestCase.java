package net.sf.cotta.acceptance;

import net.sf.cotta.*;
import net.sf.cotta.io.InputManager;
import net.sf.cotta.io.InputProcessor;
import net.sf.cotta.memory.AccesssUtil;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.TestCase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;

abstract public class InputFileChannelTestCase extends TestCase {
  public void beforeMethod() throws Exception {
    file().delete();
  }

  public void afterMethod() throws TIoException {
    file().delete();
  }

  public void testSupportFileChannel() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/source/file.txt");
    file.save("test");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        InputFileChannelTestCase.this.process(inputManager.channel());
      }
    });
    FileChannel channel = file.io().inputChannel();
    process(channel);
    channel.close();
  }

  public void testHandleReadWhenPositionedAtEndOfFile() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/test.txt");
    file.save("content");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(3);
        FileChannel channel = inputManager.channel();
        ensure.integer(channel.read(buffer)).eq(3);
        ensure.object(channel.position(channel.size())).sameAs(channel);
        ensure.integer(channel.read(buffer)).eq(0);
        buffer.clear();
        ensure.integer(channel.read(buffer)).eq(-1);
      }
    });
  }

  public void testNotAllowWriteOperations() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/test.txt");
    file.save("content");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        final FileChannel channel = inputManager.channel();
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.write(ByteBuffer.allocate(3));
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.write(new ByteBuffer[]{ByteBuffer.allocate(3)}, 0, 1);
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.write(new ByteBuffer[]{ByteBuffer.allocate(3)});
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.write(ByteBuffer.allocate(3), 0);
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.truncate(3);
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.transferFrom(AccesssUtil.createInMemoryOutputChannel(), 0, 1);
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
          }
        });
        assertNotSupported(new CodeBlock() {
          public void execute() throws Exception {
            channel.map(FileChannel.MapMode.PRIVATE, 0, 10);
          }
        });
      }
    });
  }

  private void assertNotSupported(CodeBlock block) {
    ensure.code(block).throwsException(NonWritableChannelException.class);
  }

  private TFileFactory factory() {
    return new TFileFactory(fileSystem());
  }

  abstract protected FileSystem fileSystem();

  public void testHandleCaseWhenBufferIsNotBigEnough() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/dir/test.txt");
    file.save("this is a very long content, well, sort of");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        FileChannel channel = inputManager.channel();
        ensure.integer(channel.read(buffer)).eq(5);
        ensure.string(buffer.array()).eq("this ");
        ensure.longValue(channel.position()).eq(5);
        ensure.integer(channel.read(buffer)).eq(0);
        ensure.string(buffer.array()).eq("this ");
        ensure.longValue(channel.position()).eq(5);
      }
    });
  }

  public void testPositionToAnyPoint() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/dir/test.txt");
    file.save("testing re-positioning of the channel");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        FileChannel channel = inputManager.channel();
        ensure.integer(channel.read(buffer)).eq(10);
        ensure.string(buffer.array()).eq("testing re");
        buffer.clear();
        channel.position(1);
        ensure.longValue(channel.position()).eq(1);
        ensure.integer(channel.read(buffer)).eq(10);
        ensure.string(buffer.array()).eq("esting re-");
      }
    });
  }

  public void testAllowsPositionToPassEndOfFile() throws Exception {
    TFileFactory factory = factory();
    TFile file = factory.file("tmp/dir/test.txt");
    file.save("testing positioning to end of file");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(3);
        FileChannel channel = inputManager.channel();
        ensure.object(channel.position(channel.size() + 3)).sameAs(channel);
        ensure.longValue(channel.position()).eq(channel.size() + 3);
        ensure.integer(channel.read(buffer)).eq(-1);
      }
    });
  }

  private void process(FileChannel channel) throws IOException {
    ensure.longValue(channel.position()).eq(0);
    ensure.longValue(channel.size()).eq(4);
    ByteBuffer buffer = ByteBuffer.allocate(5);
    int count = channel.read(buffer);
    ensure.integer(count).eq(4);
    byte[] bytes = buffer.array();
    String actual = new String(bytes, 0, buffer.position());
    ensure.string(actual).eq("test");
  }

  public void testNotFailForce() throws Exception {
    TFile file = factory().file("tmp/content2.txt").save("test");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        inputManager.channel().force(true);
      }
    });
  }

  public void testTransferToTarget() throws Exception {
    final TFileFactory factory = factory();
    TFile file = factory.file("tmp/content1.txt").save("content");
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        FileChannel outputChannel = AccesssUtil.createInMemoryOutputChannel();
        FileChannel inputChannel = inputManager.channel();
        long transferred = inputChannel.transferTo(1, 3, outputChannel);
        ensure.longValue(transferred).eq(3);
        ensure.inMemoryOutput(outputChannel).hasContent("ont");
        ensure.longValue(inputChannel.position()).eq(0);
      }
    });
  }

  public void testTransferOnlyAvailableBytes() throws Exception {
    TFile file = file();
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        FileChannel outputChannel = AccesssUtil.createInMemoryOutputChannel();
        FileChannel inChannel = inputManager.channel();
        long transferred = inChannel.transferTo(5, 10, outputChannel);
        ensure.longValue(transferred).eq(5);
        ensure.inMemoryOutput(outputChannel).hasContent("67890");
        ensure.longValue(inChannel.position()).eq(0);
        ensure.longValue(inChannel.transferTo(100, 1, outputChannel)).eq(0);
        outputChannel.close();
      }
    });
  }

  protected TFile file() throws TIoException {
    final TFileFactory factory = factory();
    return factory.file("tmp/content.txt").save("1234567890");
  }

  public void testReadToByteBufferFromAnyPosition() throws Exception {
    TFile file = file();
    final ByteBuffer buffer = ByteBuffer.allocate(6);
    file.read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        FileChannel channel = inputManager.channel();
        int read = channel.read(buffer, 3);
        ensure.integer(read).eq(6);
        ensure.string(buffer.array()).eq("456789");
        ensure.longValue(channel.position()).eq(0);
      }
    });
  }

}
