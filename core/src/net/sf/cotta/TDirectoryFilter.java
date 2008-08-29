package net.sf.cotta;

public interface TDirectoryFilter {
  TDirectoryFilter ALL = new TDirectoryFilter() {
    public boolean accept(TDirectory directory) {
      return true;
    }
  };

  boolean accept(TDirectory directory);
}
