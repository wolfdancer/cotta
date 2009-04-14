package net.sf.cotta;

import net.sf.cotta.test.TestCase;
import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.test.assertion.ExceptionAssert;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

abstract public class CottaTestCase extends TestCase {
  public List<Closeable> resourcesToClose;

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

  public void registerToClose(Closeable resource) {
    resourcesToClose.add(resource);
  }

  public void beforeMethod() throws Exception {
    resourcesToClose = new ArrayList<Closeable>();
  }

  public void afterMethod() throws TIoException {
    if (resourcesToClose == null) {
      return;
    }
    for (Closeable aResourcesToClose : resourcesToClose) {
      try {
        (aResourcesToClose).close();
      } catch (Exception e) {
        // ignore exception
      }
    }
  }
}
