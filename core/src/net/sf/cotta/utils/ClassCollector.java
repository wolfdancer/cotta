package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;
import net.sf.cotta.TFileFilter;
import net.sf.cotta.TIoException;

import java.util.ArrayList;
import java.util.List;

public class ClassCollector {
  private TDirectory directory;
  private String packageNamePrefix;
  private TFileFilter filter;

  public ClassCollector(TDirectory directory, String packageName) {
    this(directory, packageName, new TFileFilter() {
      public boolean accept(TFile file) {
        return file.name().endsWith("Behaviour.class");
      }
    });
  }

  public ClassCollector(TDirectory directory, String packageName, TFileFilter filter) {
    this.directory = directory;
    this.packageNamePrefix = packageName.length() == 0 ? "" : packageName + ".";
    this.filter = filter;
  }

  public List<String> collectNames() throws TIoException {
    ArrayList<String> result = new ArrayList<String>();
    collectClasses(result);
    collectSubDirectories(result);
    return result;
  }

  private void collectClasses(ArrayList<String> result) throws TIoException {
    for (TFile file : directory.list().files()) {
      if (looksLikeBehaviourClassFile(file)) {
        result.add(fullClassName(shortClassName(file.name())));
      }
    }
  }

  private void collectSubDirectories(List<String> result) throws TIoException {
    for (TDirectory directory : this.directory.list().dirs()) {
      ClassCollector classCollector = new ClassCollector(directory, packageNamePrefix + directory.name(), filter);
      result.addAll(classCollector.collectNames());
    }
  }

  private boolean looksLikeBehaviourClassFile(TFile file) {
    return filter.accept(file);
  }

  private String fullClassName(String shortClassName) {
    return new StringBuffer(packageNamePrefix).append(shortClassName).toString();
  }

  private String shortClassName(String fileName) {
    return fileName.substring(0, fileName.length() - ".class".length());
  }
}
