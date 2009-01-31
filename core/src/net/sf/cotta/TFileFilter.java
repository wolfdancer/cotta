package net.sf.cotta;

public interface TFileFilter {
  /**
   * Null value for TFileFilter that accepts all files
   */
  public static final TFileFilter NULL = new TFileFilter() {
    public boolean accept(TFile file) {
      return true;
    }
  };

  /**
   * Accept all files
   */
  public static final TFileFilter ALL = NULL;
  public static final TFileFilter NONE = new TFileFilter() {
    public boolean accept(TFile file) {
      return false;
    }
  };

  /**
   * returns true if the file passes the filter
   * @param file file
   * @return true if the file passes the filter
   */
  boolean accept(TFile file);
}
