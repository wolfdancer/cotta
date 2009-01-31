package net.sf.cotta;

public interface TDirectoryFilter {
  public static final TDirectoryFilter ALL = new TDirectoryFilter() {
    public boolean accept(TDirectory directory) {
      return true;
    }
  };

  public static final TDirectoryFilter NULL = ALL;

  /**
   * A filter that filters all files
   */
  public static final TDirectoryFilter NONE = new TDirectoryFilter() {
    public boolean accept(TDirectory directory) {
      return false;
    }
  };

  boolean accept(TDirectory directory);
}
