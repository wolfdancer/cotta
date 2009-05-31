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
    TDirectoryListing listing = directory.list();
    for (TFile file : listing.files()) {
      visit(file);
    }

    if (recursive) {
      for (TDirectory subDirectory : listing.dirs()) {
        subDirectory.visit(this);
      }
    }

  }

  abstract public void visit(TFile file) throws TIoException;
}
