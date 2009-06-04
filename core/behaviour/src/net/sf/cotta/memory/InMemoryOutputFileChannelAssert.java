package net.sf.cotta.memory;

import net.sf.cotta.test.AssertionFactory;
import net.sf.cotta.test.assertion.BaseAssert;

import java.nio.channels.FileChannel;

public class InMemoryOutputFileChannelAssert extends BaseAssert<InMemoryOutputFileChannel, InMemoryOutputFileChannelAssert> {
  private AssertionFactory ensure;

  public InMemoryOutputFileChannelAssert(FileChannel value) {
    this(value, new AssertionFactory());
  }

  public InMemoryOutputFileChannelAssert(FileChannel value, AssertionFactory assertionFactory) {
    super((InMemoryOutputFileChannel) value);
    this.ensure = assertionFactory;
  }

  public InMemoryOutputFileChannelAssert hasContent(String expected) {
    ensure.that(value().getContent().toString()).eq(expected);
    return this;
  }
}
