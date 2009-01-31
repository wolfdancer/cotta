package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestCase;

import java.util.ArrayList;
import java.util.List;

public class AbstractFileVisitorTest extends TestCase {
  public void testVisitAllFiles() throws Exception {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    TDirectory directory = factory.dir("/one/two");
    TFile test = directory.file("one.txt").save("test");
    TFile testTwo = directory.file("two.txt").save("testTwo");
    TDirectory subdirectory = directory.dir("sub");
    TFile testThree = subdirectory.file("three.txt").save("three");
    TFile testFour = subdirectory.file("four.txt").save("four");
    Visitor visitor = new Visitor(false);
    directory.visit(visitor);
    ensure.set(visitor.list).eq(test, testTwo);
    Visitor recursiveVisitor = new Visitor(true);
    directory.visit(recursiveVisitor);
    ensure.set(recursiveVisitor.list).eq(test, testTwo, testThree, testFour);
  }

  private static class Visitor extends AbstractFileVisitor {
    private List<TFile> list = new ArrayList<TFile>();

    public Visitor(boolean recursive) {
      super(recursive);
    }

    public void visit(TFile file) {
      list.add(file);
    }
  }
}
