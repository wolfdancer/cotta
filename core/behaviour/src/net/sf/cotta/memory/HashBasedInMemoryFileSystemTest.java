package net.sf.cotta.memory;

public class HashBasedInMemoryFileSystemTest extends InMemoryFileSystemTest {

  @Override
  protected InMemoryFileSystem createFileSystem() {
    return new InMemoryFileSystemBuilder().withIndexType(InMemoryFileSystem.IndexType.HASH_BASED).build();
  }
}
