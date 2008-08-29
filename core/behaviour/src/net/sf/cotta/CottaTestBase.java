package net.sf.cotta;

import net.sf.cotta.io.IoResource;
import net.sf.cotta.test.TestBase;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.test.assertion.ExceptionAssert;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CottaTestBase extends TestBase {
  public List<IoResource> resourcesToClose;

  protected CottaAssertionFactory ensure() {
    return new CottaAssertionFactory();
  }

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
    for (Iterator<IoResource> iterator = resourcesToClose.iterator(); iterator.hasNext();) {
      try {
        (iterator.next()).close();
      } catch (Exception e) {
        // ignore exception
      }
    }
  }
}
