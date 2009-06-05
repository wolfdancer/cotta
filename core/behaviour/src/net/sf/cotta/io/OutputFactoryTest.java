package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class OutputFactoryTest extends TestCase {
  private Mockery context;

  public void beforeMethod() throws Exception {
    context = new Mockery();
  }

  public void afterMethod() throws Exception {
    context.assertIsSatisfied();
  }

  public void testCreateOutputStream() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final OutputStreamFactory factory = mockFactoryForOutput(output);
    ensure.that(new OutputFactory(factory, null).outputStream()).sameAs(output);
  }

  private OutputStreamFactory mockFactoryForOutput(final ByteArrayOutputStream output) throws TIoException {
    final OutputStreamFactory factory = context.mock(OutputStreamFactory.class);
    context.checking(new Expectations() {
      {
        one(factory).outputStream();
        will(returnValue(output));
      }
    });
    return factory;
  }

  public void testCreateWriter() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final OutputStreamFactory OutputStreamFactory = mockFactoryForOutput(output);
    OutputFactory factory = new OutputFactory(OutputStreamFactory, null);
    Writer writer = factory.writer();
    writer.write("content".toCharArray());
    writer.close();
    ensure.that(output.toString()).eq("content");
  }

  public void testCreateWriterWithEncoding() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final OutputStreamFactory OutputStreamFactory = mockFactoryForOutput(output);
    OutputFactory factory = new OutputFactory(OutputStreamFactory, null);
    Writer writer = factory.writer("utf-8");
    writer.write("\u00c7\u00c9".toCharArray());
    writer.close();
    ensure.that(output.toString("utf-8")).eq("\u00c7\u00c9");
  }

  public void testCreateWriterUsingDefaultEncoding() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final OutputStreamFactory OutputStreamFactory = mockFactoryForOutput(output);
    OutputFactory factory = new OutputFactory(OutputStreamFactory, "utf-8");
    Writer writer = factory.writer();
    writer.write("\u00c7\u00c9".toCharArray());
    writer.close();
    ensure.that(output.toString("utf-8")).eq("\u00c7\u00c9");
  }

  public void testCreatePrintWriter() throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final OutputStreamFactory OutputStreamFactory = mockFactoryForOutput(output);
    OutputFactory factory = new OutputFactory(OutputStreamFactory, null);
    PrintWriter printer = factory.printWriter();
    printer.print("number");
    printer.close();
    ensure.that(output.toString()).eq("number");
    context.assertIsSatisfied();
  }

}
