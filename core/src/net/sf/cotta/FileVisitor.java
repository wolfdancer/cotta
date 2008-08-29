package net.sf.cotta;

public interface FileVisitor {
  void visit(TDirectory directory) throws TIoException;

  void visit(TFile file) throws TIoException;
}
