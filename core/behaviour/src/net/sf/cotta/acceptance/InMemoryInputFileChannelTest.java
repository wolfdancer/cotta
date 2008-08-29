package net.sf.cotta.acceptance;

import net.sf.cotta.FileSystem;
import net.sf.cotta.memory.InMemoryFileSystem;

public class InMemoryInputFileChannelTest extends InputFileChannelTestBase {
  protected FileSystem fileSystem() {
    return new InMemoryFileSystem();
  }
}
