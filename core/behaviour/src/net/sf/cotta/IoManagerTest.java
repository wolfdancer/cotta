package net.sf.cotta;

import net.sf.cotta.io.IoManager;
import net.sf.cotta.io.IoProcessor;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.io.StreamFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;

public class IoManagerTest extends CottaTestBase {
  private Mockery context;

  public void beforeMethod() throws Exception {
    context = new Mockery();
  }

  public void afterMethod() throws TIoException {
    context.assertIsSatisfied();
  }

  public void testLogInputStreamAndClose() throws Exception {
    //Given
    final InputStreamStub stub = new InputStreamStub();
    IoManager manager = mockInputStreamCall(stub);
    //When
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        ensure.that(io.inputStream()).sameAs(stub);
      }
    });
    //Ensure
    ensure.that(stub.isClosed()).eq(true);
  }

  private IoManager mockInputStreamCall(final InputStreamStub stub) throws TIoException {
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).inputStream();
        will(returnValue(stub));
      }
    });
    return new IoManager(factory);
  }

  public void testLogOutputAndClose() throws IOException {
    //Given
    final OutputStreamStub stub = new OutputStreamStub();
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream(OutputMode.APPEND);
        will(returnValue(stub));
      }
    });
    IoManager manager = new IoManager(factory);
    //When
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.outputStream(OutputMode.APPEND);
      }
    });
    //Ensure
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateLineNumberReader() throws Exception {
    //Given
    final InputStreamStub stub = new InputStreamStub();
    IoManager manager = mockInputStreamCall(stub);
    //When
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.lineNumberReader();
      }
    });
    //Ensure
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testProcessFileIoAndCloseResource() throws Exception {
    InputStreamStub inputStream = new InputStreamStub();
    IoManager manager = mockInputStreamCall(inputStream);
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.inputStream();
      }
    });
    ensure.that(inputStream.isClosed()).eq(true);
  }

  public void testProcessFileIoAndCloseResourceEvenIfExceptionOccurred() throws Exception {
    final InputStreamStub inputStream = new InputStreamStub();

    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).inputStream();
        will(returnValue(inputStream));
        one(factory).path();
        will(returnValue(TPath.parse("tmp/test")));
      }
    });
    IoManager manager = new IoManager(factory);
    try {
      manager.open(new IoProcessor() {
        public void process(IoManager io) throws IOException {
          io.inputStream();
          throw new IOException();
        }
      });
      fail("TIoException should have been thrown");
    } catch (TIoException e) {
      ensure.that(e.getCause()).isA(IOException.class);
    }
    ensure.that(inputStream.isClosed()).eq(true);
  }

  public void testCloseResourceInReverseOrderForSafety() throws Exception {
    //Given
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream(OutputMode.APPEND);
        will(returnValue(output));
      }
    });
    IoManager manager = new IoManager(factory);
    //When
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        Writer output = io.writer(OutputMode.APPEND);
        BufferedWriter bufferedWriter = new BufferedWriter(output);
        io.registerResource(bufferedWriter);
        bufferedWriter.write("test");
      }
    });
    //Ensure nothing fails
  }

}
