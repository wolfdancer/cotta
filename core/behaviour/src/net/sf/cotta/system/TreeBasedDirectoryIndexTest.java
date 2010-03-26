package net.sf.cotta.system;

import net.sf.cotta.PathSeparator;
import net.sf.cotta.memory.ListingOrder;

public class TreeBasedDirectoryIndexTest extends AbstractDirectoryIndexTest {
  @Override
  protected DirectoryIndex<DummyFileContent> newDirectoryIndexWithSort() {
    return new TreeBasedDirectoryIndex<DummyFileContent>(PathSeparator.Unix, ListingOrder.AToZ, new DummyContentManager());
  }
}
