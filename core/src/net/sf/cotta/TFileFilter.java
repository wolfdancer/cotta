package net.sf.cotta;

public interface TFileFilter {
  TFileFilter ALL = new TFileFilter() {
    public boolean accept(TFile file) {
      return true;
    }
  };

  boolean accept(TFile file);
}
