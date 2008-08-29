package net.sf.cotta;

import junit.framework.TestSuite;
import net.sf.cotta.memory.InMemoryOutputFileChannelAssert;
import net.sf.cotta.test.assertion.TDirectoryAssert;
import net.sf.cotta.test.assertion.TFileAssert;
import net.sf.cotta.test.assertion.TestSuiteAssert;

import java.nio.channels.FileChannel;

public class CottaAssertionFactory extends net.sf.cotta.test.AssertionFactory {
  public TestSuiteAssert that(TestSuite value) {
    return suite(value);
  }

  public TestSuiteAssert suite(TestSuite value) {
    return new TestSuiteAssert(value);
  }

  public InMemoryOutputFileChannelAssert that(FileChannel fileChannel) {
    return inMemoryOutput(fileChannel);
  }

  public InMemoryOutputFileChannelAssert inMemoryOutput(FileChannel fileChannel) {
    return new InMemoryOutputFileChannelAssert(fileChannel, this);
  }

  public TDirectoryAssert that(TDirectory value) {
    return new TDirectoryAssert(value);
  }

  public TFileAssert that(TFile value) {
    return new TFileAssert(value);
  }
}
