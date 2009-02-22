package net.sf.cotta.test.assertion;

import net.sf.cotta.TDirectory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TDirectoryAssert extends BaseAssert<TDirectory> {
  public TDirectoryAssert(TDirectory value) {
    super(value);
  }

  public void exists() {
    matches(new BaseMatcher<TDirectory>() {
      public boolean matches(Object o) {
        return ((TDirectory) o).exists();
      }

      public void describeTo(Description description) {
        description.appendText("directory exists");
      }
    });
  }
}
