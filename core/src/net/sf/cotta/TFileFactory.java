package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.physical.PhysicalFileSystem;
import net.sf.cotta.system.FileSystem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The factory class for creating TFile and TDirectory.
 * The static methods are used for a quick way to create TFile and TDirectory with PhysicalFileSystem.  There are two
 * sets of static factories, one for factory creation and one for file/directory ceration
 * <p/>
 * <p/>
 * File factory creation:
 * <ul>
 * <li>physical() will create the physical file factory</li>
 * <li>inMemory() will cerate the in-memory file factory</li>
 * </ul>
 * <p/>
 * File/Directory creation:
 * <ul>
 * <li>physicalFile() and physicalDir() will create the physical file and directory</li>
 * </ul>
 *
 * @see net.sf.cotta.TFile
 * @see net.sf.cotta.TDirectory
 */
public class TFileFactory {
  private FileSystem fileSystem;
  private String defaultEncoding;

  /**
   * Creat the factory using physical file system
   *
   * @see net.sf.cotta.physical.PhysicalFileSystem
   */
  public TFileFactory() {
    this(PhysicalFileSystem.instance);
  }

  public TFileFactory(FileSystem fileSystem) {
    this(fileSystem, null);
  }

  public TFileFactory(FileSystem fileSystem, String defaultEncoding) {
    this.fileSystem = fileSystem;
    this.defaultEncoding = defaultEncoding;
  }

  public FileSystem getFileSystem() {
    return fileSystem;
  }

  /**
   * Create the TFile that is represented by the path and backed by the file system
   *
   * @param pathString The path string that represents the file
   * @return The TFile object
   * @see net.sf.cotta.TPath#parse(java.lang.String)
   * @see #file(TPath)
   */
  public TFile file(String pathString) {
    return file(TPath.parse(pathString));
  }

  /**
   * Create the TFile that is repsented by the path and backed by the file system
   *
   * @param path the path that represents the file
   * @return the TFile object
   */
  public TFile file(TPath path) {
    return new TFile(this, path);
  }

  /**
   * Create the TDirectory that is represented by the path and backed by the file system
   *
   * @param pathString The pat string that represento the directory
   * @return The TDirectory object
   * @see net.sf.cotta.TPath#parse(String)
   * @see #dir(TPath)
   */
  public TDirectory dir(String pathString) {
    return dir(TPath.parse(pathString));
  }

  /**
   * Create the TDirectory that is repersented by the path and backed by the file system
   *
   * @param path the path that represents the directory
   * @return the TDirectory object
   */
  public TDirectory dir(TPath path) {
    return new TDirectory(this, path);
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
    return physical().file(jarUrl.getFile().replace("%20", " "));
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
   * @deprecated use file(java.io.File)
   */
  public static TFile fileFromJavaFile(File file) {
    return physical().file(file.getAbsolutePath());
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
  @Deprecated
  public static TDirectory directoryFromJavaFile(File file) {
    return physical().dir(file.getAbsolutePath());
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

  private static final TFileFactory PHYSICAL = new TFileFactory(PhysicalFileSystem.instance);
  /**
   * File factory backed by a physical file system
   *
   * @deprecated use TFileFactory#phylical instead.  This will become private
   */
  @Deprecated
  public static final TFileFactory PHYSICAL_FILE_FACTORY = PHYSICAL;


  public static TFile physicalFile(String path) {
    return physical().file(path);
  }

  public static TFile physicalFile(File file) {
    return physicalFile(file.getAbsolutePath());
  }

  public static TFile file(File file) {
    return physicalFile(file);
  }

  public static TDirectory physicalDir(String path) {
    return physical().dir(path);
  }

  public static TDirectory physicalDir(File file) {
    return physicalDir(file.getAbsolutePath());
  }

  public static TDirectory dir(File file) {
    return physicalDir(file);
  }

  public String defaultEncoding() {
    return defaultEncoding;
  }

  /**
   * Creates a file factory with in-memory file system.  Please note that this method returns a new in-memory
   * file factory each time
   *
   * @return in-memory file factory
   */
  public static TFileFactory inMemory() {
    return new TFileFactory(new InMemoryFileSystem());
  }

  /**
   * Retruns the shared file factory for the physical file system
   *
   * @return phylical file factory
   */
  public static TFileFactory physical() {
    return PHYSICAL;
  }
}
