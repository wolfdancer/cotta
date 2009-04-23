package net.sf.cotta.test.assertion;

import net.sf.cotta.TFile;
import net.sf.cotta.TIoException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TFileAssert extends BaseAssert<TFile, TFileAssert> {
  public TFileAssert(TFile value) {
    super(value);
  }

  public TFileAssert fileExtists() {
    matches(new BaseMatcher<TFile>() {
      public boolean matches(Object item) {
        try {
          return ((TFile) item).exists();
        } catch (TIoException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }

      public void describeTo(Description description) {
        description.appendText("file should exist");
      }
    });
    return this;
  }

}
