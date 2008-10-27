package net.sf.cotta;

import net.sf.cotta.io.IoFactory;
import net.sf.cotta.io.OutputMode;
import net.sf.cotta.io.StreamFactory;
import net.sf.cotta.test.TestBase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.*;

public class IoFactoryTest extends TestBase {
  private Mockery context;

  public void beforeMethod() throws Exception {
    context = new Mockery();
  }

  public void afterMethod() throws Exception {
    context.assertIsSatisfied();
  }

  public void testCreateInputStream() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final StreamFactory factory = mockFactoryForInput(stub);
    ensure.that(new IoFactory(factory).inputStream()).sameAs(stub);
  }

  private StreamFactory mockFactoryForInput(final InputStreamStub stub) throws TIoException {
    Mockery context = new Mockery();
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).inputStream();
        will(returnValue(stub));
      }
    });
    return factory;
  }

  public void testCreateOutputStream() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final StreamFactory factory = mockFactoryForOutput(output);
    ensure.that(new IoFactory(factory).outputStream(OutputMode.OVERWRITE)).sameAs(output);
  }

  private StreamFactory mockFactoryForOutput(final ByteArrayOutputStream output) throws TIoException {
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream(OutputMode.OVERWRITE);
        will(returnValue(output));
      }
    });
    return factory;
  }

  public void testCreateReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final StreamFactory streamFactory = mockFactoryForInput(stub);
    Reader reader = new IoFactory(streamFactory).reader();
    reader.close();
    ensure.that(stub.isClosed()).eq(true);
  }
  
  public void testCreateReaderWithEncoding() throws Exception {
	final InputStreamStub stub = new InputStreamStub();
	final StreamFactory streamFactory = mockFactoryForInput(stub);
	Reader reader = new IoFactory(streamFactory).reader("utf-8");
	reader.close();
	ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateBufferredReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final StreamFactory streamFactory = mockFactoryForInput(stub);
    IoFactory factory = new IoFactory(streamFactory);
    factory.bufferedReader().close();
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateLineBufferedReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final StreamFactory streamFactory = mockFactoryForInput(stub);
    IoFactory factory = new IoFactory(streamFactory);
    factory.lineNumberReader().close();
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateWriter() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final StreamFactory streamFactory = mockFactoryForOutput(output);
    IoFactory factory = new IoFactory(streamFactory);
    Writer writer = factory.writer(OutputMode.OVERWRITE);
    writer.write("content".toCharArray());
    writer.close();
    ensure.that(output.toString()).eq("content");
  }

  public void testCreateWriterWithEncoding() throws Exception {
	final ByteArrayOutputStream output = new ByteArrayOutputStream();
	final StreamFactory streamFactory = mockFactoryForOutput(output);
	IoFactory factory = new IoFactory(streamFactory);
	Writer writer = factory.writer(OutputMode.OVERWRITE, "utf-8");
	writer.write("content".toCharArray());
	writer.close();
	ensure.that(output.toString()).eq("content");
  }
  
  public void testCreatePrintWriter() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final StreamFactory streamFactory = mockFactoryForOutput(output);
    IoFactory factory = new IoFactory(streamFactory);
    PrintWriter printer = factory.printWriter(OutputMode.OVERWRITE);
    printer.print("number");
    printer.close();
    ensure.that(output.toString()).eq("number");
    context.assertIsSatisfied();
  }

  public void testCreateLineNumberReader() throws Exception {
    InputStreamStub stub = new InputStreamStub();
    StreamFactory factory = mockFactoryForInput(stub);
    LineNumberReader reader = new IoFactory(factory).lineNumberReader();
    reader.close();
    ensure.that(stub.isClosed()).eq(true);
  }

}
