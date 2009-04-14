package net.sf.cotta;

import net.sf.cotta.test.TestCase;

public class TEntryTest extends TestCase {
  public void testAccessToFactory() {
    TFileFactory factory = new TFileFactory();
    TEntry entry = new TEntry(factory, TPath.parse("/path"));
    ensure.that(entry.factory()).sameAs(factory);
  }
}
