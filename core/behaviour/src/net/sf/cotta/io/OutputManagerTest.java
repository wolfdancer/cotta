package net.sf.cotta.io;

import net.sf.cotta.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayOutputStream;

public class OutputManagerTest extends TestCase {
  public Mockery context = new Mockery();

  public void testDelegateToStreamFactory() throws Exception {
    final OutputStreamFactory factory = context.mock(OutputStreamFactory.class);
    context.checking(new Expectations() {
      {
        oneOf(factory).outputStream();
        will(returnValue(new ByteArrayOutputStream()));
      }
    });
    OutputManager output = new OutputManager(factory, null);
    output.open(OutputManager::outputStream);
    context.assertIsSatisfied();
  }

  public void testHasAllOutputFactoryApis() throws Exception {
    final OutputStreamFactory factory = context.mock(OutputStreamFactory.class);
    context.checking(new Expectations() {
      {
        exactly(4).of(factory).outputStream();
        will(returnValue(new ByteArrayOutputStream()));
      }
    });
    OutputManager output = new OutputManager(factory, null);
    output.open(manager -> {
      manager.registerResource(manager.outputStream());
      manager.registerResource(manager.printWriter());
      manager.registerResource(manager.writer());
      manager.bufferedWriter();
    });
    context.assertIsSatisfied();
  }
}
