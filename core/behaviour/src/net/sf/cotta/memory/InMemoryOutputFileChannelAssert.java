package net.sf.cotta.memory;

import net.sf.cotta.test.AssertionFactory;
import net.sf.cotta.test.assertion.BaseAssert;

import java.nio.channels.FileChannel;

public class InMemoryOutputFileChannelAssert extends BaseAssert<InMemoryOutputFileChannel> {
  private AssertionFactory ensure;

  public InMemoryOutputFileChannelAssert(FileChannel value) {
    this(value, new AssertionFactory());
  }

  public InMemoryOutputFileChannelAssert(FileChannel value, AssertionFactory assertionFactory) {
    super((InMemoryOutputFileChannel) value);
    this.ensure = assertionFactory;
  }

  public void hasContent(String expected) {
    ensure.string(value().getContent().toString()).eq(expected);
  }
}
