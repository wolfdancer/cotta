package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TIoException;

public interface ClassPathEntryProcessor {
  void process(TDirectory directory) throws TIoException;
}
