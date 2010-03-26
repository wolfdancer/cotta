package net.sf.cotta.system;

public interface ContentManager<F extends FileContent> {
  F createFileContent();
}
