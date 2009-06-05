package net.sf.cotta.io;

import net.sf.cotta.OutputStreamStub;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.test.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.*;

@SuppressWarnings({"deprecation"})
public class IoManagerTest extends TestCase {
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
      //noinspection ThrowableResultOfMethodCallIgnored
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

  public void testUseDefaultEncodingForWriter() throws Exception {
    final String string = "\u00c7\u00c9";
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream(OutputMode.OVERWRITE);
        will(returnValue(os));
      }
    });
    IoManager manager = new IoManager(factory, "utf-16");
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.writer(OutputMode.OVERWRITE).write(string);
      }
    });
    ensure.that(os.toString("utf-16")).eq(string);
  }

  public void testUseDefaultEncodingForReader() throws Exception {
    final String string = "\u00c7\u00c9";
    final ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes("utf-16"));
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).inputStream();
        will(returnValue(is));
      }
    });
    IoManager manager = new IoManager(factory, "utf-16");
    final char[] actual = new char[2];
    manager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        io.reader().read(actual, 0, 2);
      }
    });
    ensure.that(new String(actual)).eq(string);
  }

}
