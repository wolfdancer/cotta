package net.sf.cotta;

import net.sf.cotta.physical.PhysicalFileSystem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The factory class for creating TFile and TDirectory.
 * The static methods are used for a quick way to create TFile and TDirectory with PhysicalFileSystem.
 *
 * @see net.sf.cotta.TFile
 * @see net.sf.cotta.TDirectory
 */
public class TFileFactory {
  private FileSystem fileSystem;

  /**
   * Creat the factory using physical file system
   *
   * @see net.sf.cotta.physical.PhysicalFileSystem
   */
  public TFileFactory() {
    this(new PhysicalFileSystem());
  }

  public TFileFactory(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  /**
   * Create the TFile that is represented by the path and backed by the file system
   *
   * @param pathString The path string that represents the file
   * @return The TFile object
   * @see net.sf.cotta.TPath#parse(java.lang.String)
   */
  public TFile file(String pathString) {
    return fileFromPath(fileSystem, pathString);
  }

  /**
   * Create the TDirectory that is represented by the path and backed by the file system
   *
   * @param pathString The pat string that represento the directory
   * @return The TDirectory object
   * @see net.sf.cotta.TPath#parse(String)
   */
  public TDirectory dir(String pathString) {
    return new TDirectory(fileSystem, TPath.parse(pathString));
  }

  /**
   * Create a TFile instance is represented by URL.  The supported URL are:
   * <table>
   * <tr>
   * <th>Protocol</th>
   * <th>Sample code</th>
   * </tr>
   * <tr>
   * <td>jar</td>
   * <td><code>getClass().getResource(this.resourceString);</code></td>
   * </tr>
   * <tr>
   * <td>file</td>
   * <td><code>new java.io.File("/tmp/file.txt").toURL();</code></td>
   * </tr>
   * </table>
   *
   * @param url The url that points to a zipTo entry in a zipTo file
   * @return The TFile instance
   * @throws IllegalArgumentException if the url is not a zipTo file url
   */
  public static TFile fileFromUrl(URL url) {
    if (isJarUrl(url)) {
      return urlToFileEntryInJar(url);
    } else if (isFileUrl(url)) {
      return physicalFile(new File(url.getFile().replace("%20", " ")));
    } else {
      throw new IllegalArgumentException("Not supported url: " + url);
    }
  }

  private static TFile urlToFileEntryInJar(URL url) {
    String file = url.getFile();
    int index = file.indexOf("!");
    if (index == -1) {
      throw new IllegalArgumentException(url.toExternalForm() + " does not have '!' for a Jar URL");
    }
    String jarUrlString = file.substring(0, index);
    URL jarUrl = url(jarUrlString);
    return fileFromPath(new PhysicalFileSystem(), jarUrl.getFile().replace("%20", " "));
  }

  private static URL url(String jarUrlString) {
    try {
      return new URL(jarUrlString);
    } catch (MalformedURLException e) {
      throw new RuntimeException("file portion is not a URL: " + jarUrlString, e);
    }
  }

  /**
   * Convert the Java file to a TFile directly by using a physical file system
   *
   * @param file the Java file object
   * @return The TFile that represents the Java file
   * @see net.sf.cotta.physical.PhysicalFileSystem
   * @see #physicalFile(java.io.File)
   * @deprecated use physicalFile(java.io.File)
   */
  public static TFile fileFromJavaFile(File file) {
    return fileFromPath(new PhysicalFileSystem(), file.getAbsolutePath());
  }

  private static TFile fileFromPath(FileSystem fileSystem, String pathString) {
    return new TFile(fileSystem, TPath.parse(pathString));
  }

  /**
   * Convert the Java file to a TDirectory directly by using a physical file system
   *
   * @param file the Java file object
   * @return The TDirectory that represents the Java file
   * @see net.sf.cotta.physical.PhysicalFileSystem
   * @see #physicalDir(java.io.File)
   * @deprecated use #physicalDir
   */
  public static TDirectory directoryFromJavaFile(File file) {
    return directoryFromPath(new PhysicalFileSystem(), file.getAbsolutePath());
  }

  private static TDirectory directoryFromPath(FileSystem fileSystem, String pathString) {
    return new TDirectory(fileSystem, TPath.parse(pathString));
  }

  /**
   * The method to check is an URL can be converted to TFile or TDirectory
   *
   * @param url The url
   * @return true if the url protocol is supported
   */
  public static boolean canConvertUrl(URL url) {
    return isJarUrl(url) || isFileUrl(url);
  }

  private static boolean isFileUrl(URL url) {
    return "file".equalsIgnoreCase(url.getProtocol());
  }

  private static boolean isJarUrl(URL url) {
    return "jar".equalsIgnoreCase(url.getProtocol());
  }

  public static final TFileFactory PHYSICAL_FILE_FACTORY = new TFileFactory(new PhysicalFileSystem());

  public static TFile physicalFile(String path) {
    return PHYSICAL_FILE_FACTORY.file(path);
  }

  public static TFile physicalFile(File file) {
    return physicalFile(file.getAbsolutePath());
  }

  public static TFile file(File file) {
    return physicalFile(file);
  }

  public static TDirectory physicalDir(String path) {
    return PHYSICAL_FILE_FACTORY.dir(path);
  }

  public static TDirectory physicalDir(File file) {
    return physicalDir(file.getAbsolutePath());
  }

  public static TDirectory dir(File file) {
    return physicalDir(file);
  }
}
