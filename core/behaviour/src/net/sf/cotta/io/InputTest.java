package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputTest extends TestCase {
  public void testWithStaticFactoryMethod() throws TIoException {
    final InputStream stream = new ByteArrayInputStream(new byte[0]);
    Input.with(stream).read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        ensure.that(inputManager.inputStream()).sameAs(stream);
      }
    });
  }

  public void testWStaticFactorySupportsPath() throws TIoException {
    InputStream stream = new ByteArrayInputStream(new byte[0]);
    Input.with(stream).read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        try {
          inputManager.reader("aoeuaoeuaoeu");
          fail("should have thrown exception for wrong encoding");
        } catch (TIoException e) {
          ensure.that(e).message().contains("input stream");
        }
      }
    });
  }
}
