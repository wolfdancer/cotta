package net.sf.cotta;

/**
 * TEntry used for listing
 */
public interface TEntryFilter extends TFileFilter, TDirectoryFilter {
  /**
   * A filter tat returns all entrise
   */
  public static final TEntryFilter ALL = new TEntryFilter() {
    public boolean accept(TDirectory directory) {
      return true;
    }

    public boolean accept(TFile file) {
      return true;
    }
  };

  /**
   * Null value, which is the same as ALL
   */
  public static final TDirectoryFilter NULL = ALL;

  /**
   * A filter that returns no entries
   */
  public static final TEntryFilter NONE = new TEntryFilter() {
    public boolean accept(TDirectory directory) {
      return false;
    }

    public boolean accept(TFile file) {
      return false;
    }
  };

}
