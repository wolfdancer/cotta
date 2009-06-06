package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputTest extends TestCase {
  public void testWithStaticFactoryMethod() throws TIoException {
    final OutputStream stream = new ByteArrayOutputStream();
    Output.with(stream).write(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        ensure.that(manager.outputStream()).sameAs(stream);
      }
    });
  }

  public void testWithStaticFactorySupportsPath() throws TIoException {
    OutputStream stream = new ByteArrayOutputStream();
    Output.with(stream).write(new OutputProcessor() {
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
