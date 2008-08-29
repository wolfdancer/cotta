package net.sf.cotta.test.assertion;

import net.sf.cotta.TFile;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TFileAssert extends ObjectAssert<TFile> {
  public TFileAssert(TFile value) {
    super(value);
  }

  public void fileExtists() {
    matches(new BaseMatcher<TFile>() {
      public boolean matches(Object item) {
        return ((TFile) item).exists();
      }

      public void describeTo(Description description) {
        description.appendText("file should exist");
      }
    });
  }

}
