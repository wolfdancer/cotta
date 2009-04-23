package net.sf.cotta.test.assertion;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TIoException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TDirectoryAssert extends BaseAssert<TDirectory, TDirectoryAssert> {
  public TDirectoryAssert(TDirectory value) {
    super(value);
  }

  public TDirectoryAssert exists() {
    matches(new BaseMatcher<TDirectory>() {
      public boolean matches(Object o) {
        try {
          return ((TDirectory) o).exists();
        } catch (TIoException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }

      public void describeTo(Description description) {
        description.appendText("directory exists");
      }
    });
    return this;
  }
}
