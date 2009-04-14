package net.sf.cotta.io;

import net.sf.cotta.CottaTestCase;
import net.sf.cotta.TIoException;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputManagerTest extends CottaTestCase {
  public Mockery context = new Mockery();

  public void testDelegateToStreamFactory() throws Exception {
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream(OutputMode.APPEND);
        will(returnValue(new ByteArrayOutputStream()));
      }
    });
    OutputManager output = new OutputManager(factory, OutputMode.APPEND);
    output.open(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        manager.outputStream();
      }
    });
    context.assertIsSatisfied();
  }

  public void testHasAllOutputFactoryApis() throws Exception {
    final StreamFactory factory = context.mock(StreamFactory.class);
    context.checking(new Expectations() {
      {
        exactly(4).of(factory).outputStream(OutputMode.OVERWRITE);
        will(returnValue(new ByteArrayOutputStream()));
      }
    });
    OutputManager output = new OutputManager(factory, OutputMode.OVERWRITE);
    output.open(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        manager.registerResource(manager.outputStream());
        manager.registerResource(manager.printWriter());
        manager.registerResource(manager.writer());
        manager.bufferedWriter();
      }
    });
    context.assertIsSatisfied();
  }
  
  public void testWithStaticFactoryMethod() throws TIoException {
    final OutputStream stream = new ByteArrayOutputStream();
    OutputManager.with(stream).write(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        ensure.that(manager.outputStream()).sameAs(stream);
      }
    });
  }
  
  public void testWithStaticFactorySupportsPath() throws TIoException {
    OutputStream stream = new ByteArrayOutputStream();
    OutputManager.with(stream).write(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        try {
          manager.writer("aoeuaoeuaoeu");
          fail("should have thrown exception for wrong encoding");
        } catch (TIoException e) {
          ensure.that(e).message().contains("output stream");
        }
      }
    });
  }
}
