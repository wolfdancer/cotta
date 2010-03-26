package net.sf.cotta.system;

import net.sf.cotta.PathSeparator;
import net.sf.cotta.memory.ListingOrder;

public class HashBasedDirectoryIndexTest extends AbstractDirectoryIndexTest {

  @Override
  protected DirectoryIndex<DummyFileContent> newDirectoryIndexWithSort() {
    return new HashBasedDirectoryIndex<DummyFileContent>(PathSeparator.Unix, ListingOrder.AToZ, new DummyContentManager());
  }
}
