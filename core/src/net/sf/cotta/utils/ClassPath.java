package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;

/**
 * @deprecated use ClassPathEntry
 */
public class ClassPath extends ClassPathEntry {
  public ClassPath(TDirectory directory) {
    super(directory);
  }

  public ClassPath(TFile file) {
    super(file);
  }
}
