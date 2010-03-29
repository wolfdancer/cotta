package net.sf.cotta.acceptance;

import net.sf.cotta.system.FileSystem;
import net.sf.cotta.memory.InMemoryFileSystem;

public class InMemoryTfsTest extends TfsTestCase {
  protected FileSystem fileSystem() {
    return new InMemoryFileSystem();
  }
}
