package net.sf.cotta.memory;

import net.sf.cotta.PathSeparator;

public class InMemoryFileSystemBuilder {

  private PathSeparator separator = InMemoryFileSystem.DEFAULT_PATH_SEPARATOR;
  private ListingOrder order = InMemoryFileSystem.DEFAULT_LISTING_ORDER;
  private InMemoryFileSystem.IndexType index = InMemoryFileSystem.DEFAULT_INDEX_TYPE;

  public InMemoryFileSystemBuilder() {
  }

  public InMemoryFileSystemBuilder withPathSeparator(PathSeparator separator) {
    this.separator = separator;
    return this;
  }

  public InMemoryFileSystemBuilder withListingOrder(ListingOrder order) {
    this.order = order;
    return this;
  }

  public InMemoryFileSystemBuilder withIndexType(InMemoryFileSystem.IndexType index) {
    this.index = index;
    return this;
  }

  public InMemoryFileSystem build() {
    return new InMemoryFileSystem(separator, order, index);
  }
}
