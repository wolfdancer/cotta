package net.sf.cotta;

abstract public class AbstractFileVisitor implements FileVisitor {
  public void visit(TDirectory directory) throws TIoException {
    TFile[] files = directory.listFiles();
    for (int i = 0; i < files.length; i++) {
      visit(files[i]);
    }

  }

  abstract public void visit(TFile file) throws TIoException;
}
