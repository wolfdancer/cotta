package net.sf.cotta;

public class TDirectoryListingTest extends TestCase {
  public void testImplementsIterableOfEntries() {
    TFileFactory factory = TFileFactory.inMemory();
    TPath a = TPath.parse("a");
    TPath one = TPath.parse("1");
    PathContent content = new PathContent(2);
    content.addDirectoryPath(a);
    content.addFilePath(one);
    TDirectoryListing listing = new TDirectoryListing(factory, content);
    ensure.that(listing).eq(factory.dir(a), factory.file(one));
  }

  public void testDirsReturnDirsInSameOrder() {
    TFileFactory factory = TFileFactory.inMemory();
    TPath a = TPath.parse("a");
    TPath b = TPath.parse("b");
    TPath one = TPath.parse("1");
    PathContent content = new PathContent(2);
    content.addDirectoryPath(b);
    content.addDirectoryPath(a);
    content.addFilePath(one);
    TDirectoryListing listing = new TDirectoryListing(factory, content);
    ensure.that(listing.dirs()).eq(factory.dir(b), factory.dir(a));
  }

  public void testFilesReturnFiles() {
    TFileFactory factory = TFileFactory.inMemory();
    TPath a = TPath.parse("a");
    TPath one = TPath.parse("1");
    TPath two = TPath.parse("2");
    PathContent content = new PathContent(2);
    content.addDirectoryPath(a);
    content.addFilePath(two);
    content.addFilePath(one);
    TDirectoryListing listing = new TDirectoryListing(factory, content);
    ensure.that(listing.files()).eq(factory.file(two), factory.file(one));
  }

  public void testOrderedListing() {
    TFileFactory factory = TFileFactory.inMemory();
    TPath a = TPath.parse("a");
    TPath one = TPath.parse("1");
    TPath two = TPath.parse("2");
    PathContent content = new PathContent(2);
    content.addDirectoryPath(a);
    content.addFilePath(two);
    content.addFilePath(one);
    TDirectoryListing listing = new TDirectoryListing(factory, content).ordered();
    ensure.that(listing.files()).eq(factory.file(one), factory.file(two));
  }

  public void testFileFiltering() {
    TFileFactory factory = TFileFactory.inMemory();
    final TPath a = TPath.parse("a");
    TPath b = TPath.parse("b");
    PathContent content = new PathContent(2);
    content.addFilePath(a);
    content.addFilePath(b);
    TDirectoryListing listing = new TDirectoryListing(factory, content).filteredBy(new TFileFilter() {
      public boolean accept(TFile file) {
        return file.toPath().equals(a);
      }
    });
    ensure.that(listing.files()).eq(factory.file(a));
  }

  public void testDirectoryFiltering() {
    TFileFactory factory = TFileFactory.inMemory();
    final TPath a = TPath.parse("a");
    TPath b = TPath.parse("b");
    PathContent content = new PathContent(2);
    content.addDirectoryPath(a);
    content.addDirectoryPath(b);
    TDirectoryListing listing = new TDirectoryListing(factory, content).filteredBy(new TDirectoryFilter() {
      public boolean accept(TDirectory directory) {
        return directory.toPath().equals(a);
      }
    });
    ensure.that(listing.dirs()).eq(factory.dir(a));
  }

  public void testEntryFiltering() {
    TFileFactory factory = TFileFactory.inMemory();
    final TPath a = TPath.parse("a");
    TPath b = TPath.parse("b");
    final TPath one = TPath.parse("one");
    TPath two = TPath.parse("two");
    PathContent content = new PathContent(4);
    content.addDirectoryPath(a);
    content.addDirectoryPath(b);
    content.addFilePath(one);
    content.addFilePath(two);
    TEntryFilter filter = new TEntryFilter() {
      public boolean accept(TDirectory directory) {
        return directory.toPath().equals(a);
      }

      public boolean accept(TFile file) {
        return file.toPath().equals(one);
      }
    };
    TDirectoryListing listing = new TDirectoryListing(factory, content).filteredBy(filter, filter);
    ensure.that(listing.dirs()).eq(factory.dir(a));
    ensure.that(listing.files()).eq(factory.file(one));
  }
}
