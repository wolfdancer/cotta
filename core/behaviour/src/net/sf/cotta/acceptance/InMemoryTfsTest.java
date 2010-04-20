package net.sf.cotta.acceptance;

import net.sf.cotta.TFileFactory;
import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.system.FileSystem;

public class InMemoryTfsTest extends TfsTestCase {
  protected FileSystem fileSystem() {
    return new InMemoryFileSystem();
  }

  public void testCDriveDoesNotExistByDefault() {
    TFileFactory factory = new TFileFactory(fileSystem());
    ensure.that(factory.dir("C:/").exists())
            .eq(false);
  }
}
