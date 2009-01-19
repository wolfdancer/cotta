package net.sf.cotta;

abstract public class AbstractFileVisitor implements FileVisitor {
  private boolean recursive = true;

  public AbstractFileVisitor() {
    this(true);
  }

  public AbstractFileVisitor(boolean recursive) {
    this.recursive = recursive;
  }

  public void visit(TDirectory directory) throws TIoException {
    for (TFile file : directory.listFiles()) {
      visit(file);
    }

    if (recursive) {
      for (TDirectory subDirectory : directory.listDirs()) {
        subDirectory.visit(this);
      }
    }

  }

  abstract public void visit(TFile file) throws TIoException;
}
