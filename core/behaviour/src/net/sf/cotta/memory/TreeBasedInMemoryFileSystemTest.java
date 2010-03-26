package net.sf.cotta.memory;

public class TreeBasedInMemoryFileSystemTest extends InMemoryFileSystemTestBase {

  @Override
  protected InMemoryFileSystem createFileSystem() {
    return new InMemoryFileSystemBuilder().withIndexType(InMemoryFileSystem.IndexType.TREE_BASED).build();
  }
}
