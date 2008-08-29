package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/** @noinspection JavaDoc*/
public class ProductInfo {
  private ClassPathEntry pathEntry;
  private Manifest manifest;
  private static final String IMPLEMENTATION_TITLE = "Implementation-Title";
  private static final String IMPLEMENTATION_VENDOR = "Implementation-Vendor";
  private static final String IMPLEMENTATION_VERSION = "Implementation-Version";
  private static final String IMPLEMENTATION_BUILD = "Implementation-Build";

  public ProductInfo(ClassPathEntry pathEntry) throws TIoException {
    this.pathEntry = pathEntry;
    loadManifest();
  }

  private void loadManifest() throws TIoException {
    InputStream is = null;
    boolean loadingPassed = false;
    try {
      TDirectory pathRoot = pathEntry.openAsDirectory();
      is = pathRoot.dir("META-INF").file("MANIFEST.MF").io().inputStream();
      manifest = new Manifest(is);
      loadingPassed = true;
    } catch (IOException e) {
      throw new TIoException(tpath(), "Error reading manifest", e);
    } finally {
      closeResource(!loadingPassed, is, pathEntry);
    }
  }

  private TPath tpath() {
    return TPath.parse(loadedPath().path());
  }

  private void closeResource(boolean reportError, InputStream is, ClassPathEntry pathEntry) throws TIoException {
    try {
      if (is != null) {
        is.close();
      }
      pathEntry.closeResource();
    } catch (IOException e) {
      if (reportError) {
        throw new TIoException(tpath(), "Error closing InputStream", e);
      }
    }
  }

  public void info(PrintStream out) {
    out.println("Loaded from " + pathEntry.path());
    out.println("Vendor: " + vendor());
    out.println("Title: " + title());
    out.println("URL: " + url());
    version().info(out);
  }

  public ClassPathEntry loadedPath() {
    return pathEntry;
  }

  public String vendor() {
    return mainAttributeValue(IMPLEMENTATION_VENDOR);
  }

  public String title() {
    return mainAttributeValue(IMPLEMENTATION_TITLE);
  }

  public VersionNumber version() {
    return new VersionNumber(
        mainAttributeValue(IMPLEMENTATION_VERSION),
        mainAttributeValue(IMPLEMENTATION_BUILD));
  }

  public String url() {
    return mainAttributeValue("Implementation-URL");
  }

  public static ProductInfo forClass(Class aClass) throws TIoException {
    ClassPathEntry pathEntry = new ClassPathEntryLocator(aClass).locateEntry();
    return new ProductInfo(pathEntry);
  }

  public String mainAttributeValue(String attributeName) {
    return manifest.getMainAttributes().getValue(attributeName);
  }

  public String otherAttributeValue(String section, String attributeName) {
    Attributes attributes = manifest.getAttributes(section);
    return attributes == null ? null : attributes.getValue(attributeName);
  }

}
