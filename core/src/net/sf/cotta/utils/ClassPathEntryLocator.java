package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;
import net.sf.cotta.TFileFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

public class ClassPathEntryLocator {
  protected String resourceString;
  private int level;

  public ClassPathEntryLocator(Class clazz) {
    this.resourceString = "/" + clazz.getName().replace('.', '/') + ".class";
    this.level = new StringTokenizer(clazz.getName(), ".").countTokens();
  }

  public ClassPathEntryLocator(String absoluteResourcePath) {
    if (!absoluteResourcePath.startsWith("/")) {
      throw new IllegalArgumentException("resource path needs to be absolute:" + absoluteResourcePath);
    }
    this.resourceString = absoluteResourcePath;
    this.level = new StringTokenizer(absoluteResourcePath, "/").countTokens();
  }

  public ClassPathEntry locateEntry() {
    URL url = getClass().getResource(this.resourceString);
    if ("jar".equalsIgnoreCase(url.getProtocol())) {
      return new ClassPathEntry(getJarFileOnClassPath(url));
    } else {
      return new ClassPathEntry(goToClassPathRootDirectory(url));
    }
  }

  protected TDirectory goToClassPathRootDirectory(URL url) {
    TFile classFile = getClassFile(url);
    TDirectory directory = classFile.parent();
    for (int i = 0; i < this.level - 1; i++) {
      directory = directory.parent();
    }
    return directory;
  }

  TFile getJarFileOnClassPath(URL url) {
    return TFileFactory.fileFromUrl(url);
  }

  private TFile getClassFile(URL url) {
    try {
      File file = new File(new URI(url.toExternalForm()));
      return TFileFactory.physicalFile(file);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Couldn't convert URL to File:" + url, e);
    }
  }
}
