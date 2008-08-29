package net.sf.cotta.io;

import net.sf.cotta.CottaTestBase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OutputManagerTest extends CottaTestBase {
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
      public void process(OutputManager outputManager) throws IOException {
        outputManager.outputStream();
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
      public void process(OutputManager outputManager) throws IOException {
        outputManager.registerResource(outputManager.outputStream());
        outputManager.registerResource(outputManager.printWriter());
        outputManager.registerResource(outputManager.writer());
        outputManager.bufferedWriter();
      }
    });
    context.assertIsSatisfied();
  }
}
