package net.sf.cotta.utils;

import net.sf.cotta.*;
import net.sf.cotta.zip.ZipFileSystem;

import java.io.File;
import java.io.IOException;

public class ClassPathEntry {
  private ClassPathType type;
  private TDirectory directory;
  private TFile file;
  private TResource resource = TResource.NULL;
  private TDirectory cachedJarDirecotry;

  public ClassPathEntry(TDirectory directory) {
    this.directory = directory;
    type = ClassPathType.DIRECTORY;
  }

  public ClassPathEntry(TFile file) {
    this.file = file;
    type = ClassPathType.FILE;
  }

  public ClassPathType type() {
    return type;
  }

  /**
   * Use the class path entry as a TDirectory.
   * If the entry is a jar file, it will be opened and would require a close to be called.  If you don't want to handle the
   * resource management, you should use read method
   * @see #read(ClassPathEntryProcessor) 
   * @return If the class path entry is a directory, the directory ponting to it, or if it is a zip file, to the root entry of it.
   * @throws TIoException If the class path entry is pointing to a file that is not a zip format
   */
  public TDirectory openAsDirectory() throws TIoException {
    if (ClassPathType.DIRECTORY.equals(type)) {
      return directory;
    }
    return convertToJarRoot();
  }

  private TDirectory convertToJarRoot() throws TIoException {
    if (cachedJarDirecotry == null) {
      ZipFileSystem fileSystem = convertToFileSystem();
      cachedJarDirecotry = new TFileFactory(fileSystem).dir("/");
    }
    return cachedJarDirecotry;
  }

  private ZipFileSystem convertToFileSystem() throws TIoException {
    File jarFile = new File(file.path());
    ZipFileSystem fileSystem;
    try {
      fileSystem = new ZipFileSystem(jarFile);
    } catch (IOException e) {
      throw new TIoException(TPath.parse(file.path()), "Error opening zip file <" + jarFile.getAbsolutePath() + ">", e);
    }
    resource = fileSystem;
    return fileSystem;
  }

  /**
   * Close the resource.  This is used to close the file if this class path entry is a zip file.
   * @throws TIoException If close threw exception.
   */
  public void closeResource() throws TIoException {
    resource.close();
    cachedJarDirecotry = null;
    resource = TResource.NULL;
  }

  /**
   * @return The path string of this class path entry
   */
  public String path() {
    return ClassPathType.DIRECTORY.equals(type) ? directory.path() : file.path();
  }

  /**
   * Opens the class path entry, process its content, and close the resource
   * @param processor The processor to call after openning the class path entry
   * @throws TIoException for any read error
   */
  public void read(ClassPathEntryProcessor processor) throws TIoException {
    TResource resource = TResource.NULL;
    TDirectory directory;
    if (ClassPathType.DIRECTORY.equals(type)) {
      directory = this.directory;
    } else {
      ZipFileSystem fileSystem = convertToFileSystem();
      resource = fileSystem;
      directory = new TDirectory(new TFileFactory(fileSystem), TPath.parse("/"));
    }
    callBack(processor, resource, directory);
  }

  private void callBack(ClassPathEntryProcessor processor, TResource resource, TDirectory directory) throws TIoException {
    try {
      processor.process(directory);
    } finally {
      resource.close();
    }
  }
}
