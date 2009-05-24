package net.sf.cotta;

import net.sf.cotta.test.TestCase;

import java.io.File;

public class TEntryTest extends TestCase {
  public void testAccessToFactory() {
    TFileFactory factory = new TFileFactory();
    TEntry entry = new TEntry(factory, TPath.parse("/path")) {
      public boolean exists() throws TIoException {
        return false;
      }
    };
    ensure.that(entry.factory()).sameAs(factory);
  }

  public void testToStringContainsEntryTypeAndPath() {
    TFileFactory factory = TFileFactory.inMemory();
    TEntry entry = new TEntry(factory, TPath.parse("/path")) {
      public boolean exists() throws TIoException {
        return false;
      }
    };
    ensure.that(entry.toString()).eq("</path>");
  }

  public void testHashCode() {
    TFileFactory factory = TFileFactory.physical();
    String path = "/test/one.txt";
    ensure.that(factory.file(path).hashCode()).eq(new File(path).hashCode());
  }
}
