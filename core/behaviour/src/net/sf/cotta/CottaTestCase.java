package net.sf.cotta;

import net.sf.cotta.io.IoResource;
import net.sf.cotta.test.TestCase;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.test.assertion.ExceptionAssert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

abstract public class CottaTestCase extends TestCase {
  public List<IoResource> resourcesToClose;

  public static CottaAssertionFactory ensure = new CottaAssertionFactory();

  protected void ensureEquals(boolean actual, boolean expected) {
    ensure.that(actual).eq(expected);
  }

  protected void ensureEquals(String actual, String expected) {
    ensure.that(actual).eq(expected);
  }

  protected void ensureEquals(int actual, int expected) {
    ensure.integer(actual).eq(expected);
  }

  protected <T extends Exception> ExceptionAssert runAndCatch(Class<T> exceptionClass, CodeBlock block) {
    return ensure.code(block).throwsException(exceptionClass);
  }

  public void registerToClose(IoResource resource) {
    resourcesToClose.add(resource);
  }

  public IoResource resource(final OutputStream stream) {
    return new IoResource() {
      public void close() throws IOException {
        stream.close();
      }
    };
  }

  public IoResource resource(final InputStream stream) {
    return new IoResource() {

      public void close() throws IOException {
        stream.close();
      }
    };
  }

  public IoResource resource(final Writer writer) {
    return new IoResource() {
      public void close() throws IOException {
        writer.close();
      }
    };
  }

  public IoResource resource(final Reader reader) {
    return new IoResource() {
      public void close() throws IOException {
        reader.close();
      }
    };
  }

  public void beforeMethod() throws Exception {
    resourcesToClose = new ArrayList<IoResource>();
  }

  public void afterMethod() throws TIoException {
    if (resourcesToClose == null) {
      return;
    }
    for (IoResource aResourcesToClose : resourcesToClose) {
      try {
        (aResourcesToClose).close();
      } catch (Exception e) {
        // ignore exception
      }
    }
  }
}
