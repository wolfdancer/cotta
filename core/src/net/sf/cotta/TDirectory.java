package net.sf.cotta;

import net.sf.cotta.io.OutputManager;
import net.sf.cotta.io.OutputProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The class that represent the directory.  To create TDirectory, use TFile, TDirectory, and TFileFactory
 *
 * @see TFileFactory#directoryFromJavaFile(java.io.File)
 * @see TFileFactory#dir(String)
 * @see TFile#parent()
 * @see TDirectory#parent()
 * @see TDirectory#dir(String)
 * @see TDirectory#dir(TPath)
 */
public class TDirectory extends TEntry {
  /**
   * Constructor that creates the directory to be mainly used internally.
   *
   * @param fileSystem The file system that backs the file
   * @param path       The path to the file
   * @see #TDirectory(TFileFactory, TPath)
   * @deprecated Use the other constructor for default encoding support in TFileFactory
   */
  public TDirectory(FileSystem fileSystem, TPath path) {
    super(new TFileFactory(fileSystem), path);
  }

  public TDirectory(TFileFactory factory, TPath path) {
    super(factory, path);
  }

  public boolean exists() throws TIoException {
    return filesystem().dirExists(path);
  }

  public TDirectory ensureExists() throws TIoException {
    if (!filesystem().dirExists(path)) {
      filesystem().createDir(path);
    }
    return this;
  }

  /**
   * Constucts a file given the path.  If the path is relative path,
   * it will be constructed based on the current directory
   *
   * @param path the path of the file
   * @return The file that is under the directory with the name
   * @see #file(TPath)
   */
  public TFile file(String path) {
    return file(TPath.parse(path));
  }

  /**
   * Constructs a file given the relative path.  If the path is relative,
   * it will be constructed based on the current direcotry
   *
   * @param path path to the file
   * @return The file that is of the relative to the current directory
   */
  public TFile file(TPath path) {
    return new TFile(factory(), join(path));
  }

  private TPath join(TPath path) {
    return path.isRelative() ? this.path.join(path) : path;
  }

  /**
   * Constructs a subdirectory given the directory name
   *
   * @param relativePath the relative path of the subdirectory
   * @return The directory that is under the current directory with the given name
   */
  public TDirectory dir(String relativePath) {
    return dir(TPath.parse(relativePath));
  }

  /**
   * Constructs a directory given the relative path to the current directory
   *
   * @param path the relative path of the target directory to current directory
   * @return The target directory that is of the given the relative path
   */
  public TDirectory dir(TPath path) {
    return new TDirectory(factory(), join(path));
  }

  /**
   * List the sub-directories of the current directory
   *
   * @return mutable list of TDirectory that can be sorted
   * @throws TIoException for error in reading the directory
   */
  public List<TDirectory> listDirs() throws TIoException {
    return listDirs(TDirectoryFilter.ALL);
  }

  /**
   * List the sub-directories in the alphabetic order
   *
   * @return sorted mutable list of sub-directories
   * @throws TIoException for erros in reading the directory
   */
  public List<TDirectory> listDirsOrdered() throws TIoException {
    return sort(listDirs());
  }

  /**
   * List the sub-directories of the current directory that can be accepted by the filter
   *
   * @param directoryFilter filter for the directory
   * @return list of sub-directories
   * @throws TIoException for error in reading the directory
   */
  public List<TDirectory> listDirs(TDirectoryFilter directoryFilter) throws TIoException {
    PathContent content = listContent();
    List<TDirectory> directories = new ArrayList<TDirectory>(content.dirs().size());
    addDirectories(directories, directoryFilter, content);
    return directories;
  }

  @SuppressWarnings({"unchecked"})
  private void addDirectories(List list, TDirectoryFilter directoryFilter, PathContent content) {
    List<TPath> paths = content.dirs();
    for (TPath path : paths) {
      TDirectory candidate = new TDirectory(factory(), path);
      if (directoryFilter.accept(candidate)) {
        list.add(candidate);
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  private void addFiles(List list, TFileFilter fileFilter, PathContent content) {
    List<TPath> paths = content.files();
    for (TPath path : paths) {
      TFile candidate = new TFile(factory(), path);
      if (fileFilter.accept(candidate)) {
        list.add(candidate);
      }
    }
  }

  /**
   * List the sub-directories of the current directory that can be accepted by the filter, and sort the result
   *
   * @param directoryFilter filter for the directory
   * @return list of ordered sub-directories
   * @throws TIoException for error in reading the directory
   */
  public List<TDirectory> listDirsOrdered(TDirectoryFilter directoryFilter) throws TIoException {
    List<TDirectory> list = listDirs(directoryFilter);
    return sort(list);
  }

  /**
   * List the current directory
   *
   * @return list of TEntry that are either TFile or TDirectory
   * @throws TIoException for error in reading current directory
   */
  public List<TEntry> list() throws TIoException {
    return list(TEntryFilter.ALL);
  }

  /**
   * List the current directory with entry filter
   *
   * @param filter entry filter
   * @return filted lits of entries
   * @throws TIoException for error in reading current directory
   */
  public List<TEntry> list(TEntryFilter filter) throws TIoException {
    PathContent content = listContent();
    List<TEntry> result = new ArrayList<TEntry>(content.dirs().size() + content.files().size());
    addDirectories(result, filter, content);
    addFiles(result, filter, content);
    return result;
  }

  /**
   * List the current directory and sort the result
   *
   * @return ordered list of TEntry that are either TFile or TDirectory
   * @throws TIoException for error in reading current  directory
   */
  public List<TEntry> listOrdered() throws TIoException {
    return listOrdered(TEntryFilter.ALL);
  }

  private List<TEntry> listOrdered(TEntryFilter filter) throws TIoException {
    return sort(list(filter));
  }

  private <T extends TEntry> List<T> sort(List<T> list) {
    Collections.sort(list);
    return list;
  }

  private PathContent listContent() throws TIoException {
    checkDirectoryExists();
    return filesystem().list(this.path);
  }


  private void checkDirectoryExists() throws TIoException {
    if (!filesystem().dirExists(path)) {
      throw new TDirectoryNotFoundException(path);
    }
  }

  /**
   * List files under current directory
   *
   * @return a mutable list of files under current directory
   * @throws TIoException error in reading from current directory
   */
  public List<TFile> listFiles() throws TIoException {
    return listFiles(TFileFilter.ALL);
  }

  /**
   * List files under current directory and return them as sorted list
   *
   * @return sorted list of files under current directory
   * @throws TIoException error in reading form current directory
   */
  public List<TFile> listFilesOrdered() throws TIoException {
    return sort(listFiles());
  }

  /**
   * List files under current directory that accepted by the file filter
   *
   * @param fileFilter file filter for the list
   * @return a mutable list of files
   * @throws TIoException error in reading from current directory
   */
  public List<TFile> listFiles(TFileFilter fileFilter) throws TIoException {
    PathContent content = listContent();
    List<TFile> files = new ArrayList<TFile>(content.files().size());
    addFiles(files, fileFilter, content);
    return files;
  }

  /**
   * List files under current directory that accepted by the file filter, and sort the result
   *
   * @param fileFilter file filter for the list
   * @return a mutable ordede list of files
   * @throws TIoException error in reading from current directory
   */
  public List<TFile> listFilesOrdered(TFileFilter fileFilter) throws TIoException {
    return sort(listFiles(fileFilter));
  }

  public String toString() {
    return "TDirectory " + path();
  }

  /**
   * Delete the current directory.  Most file system will fail
   * if the current directory is not empty
   *
   * @throws TIoException error in deleting the directory (most file system will fail when directory is not empty)
   */
  public void delete() throws TIoException {
    filesystem().deleteDirectory(path);
  }

  /**
   * Delete the whole directory tree
   *
   * @throws TIoException error in the operation
   */
  public void deleteAll() throws TIoException {
    for (TDirectory aSubDirectory : listDirs()) {
      aSubDirectory.deleteAll();
    }

    List<TFile> files = listFiles();
    for (TFile file : files) {
      file.delete();
    }
    delete();
  }

  public void mergeTo(TDirectory target) throws TIoException {
    target.ensureExists();
    copySubDirectories(target);
    copyFiles(target);
  }

  private void copySubDirectories(TDirectory target) throws TIoException {
    for (TDirectory subdir : listDirs()) {
      subdir.mergeTo(target.dir(subdir.name()));
    }
  }

  private void copyFiles(TDirectory target) throws TIoException {
    List<TFile> files = listFiles();
    for (TFile file : files) {
      file.copyTo(target.file(file.name()));
    }
  }

  public void moveTo(TDirectory target) throws TIoException {
    if (!exists()) {
      throw new TFileNotFoundException(path);
    }
    if (target.exists()) {
      throw new TIoException(target.path, "Destination exists");
    }
    if (filesystem() == target.filesystem() || filesystem().equals(target.filesystem())) {
      filesystem().moveDirectory(path, target.path);
    } else {
      this.mergeTo(target);
      delete();
    }
  }

  /**
   * @return java.io.File presentation of the directory
   * @deprecated use #toJavaFile()
   */
  public File getJavaFile() {
    return toJavaFile();
  }

  /**
   * Zip the current directory to a file, with the files and directories of current directory at the root level
   *
   * @param file the target file
   * @throws TIoException error in reading from the directory or writing to the file
   */
  public void zipTo(TFile file) throws TIoException {
    file.write(new OutputProcessor() {
      public void process(OutputManager manager) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(manager.outputStream());
        manager.registerResource(zipStream);
        addDirEntry(zipStream, "", TDirectory.this);
      }

      private void addDirEntry(ZipOutputStream zipStream, String path, TDirectory directory) throws IOException {
        List<TFile> files = directory.listFiles();
        for (TFile file : files) {
          addFileEntry(zipStream, path, file);
        }
        for (TDirectory subDirectory : directory.listDirs()) {
          addDirEntry(zipStream, path + subDirectory.name() + "/", subDirectory);
        }
        zipStream.putNextEntry(new ZipEntry(path + "/"));
        zipStream.closeEntry();
      }

      private void addFileEntry(ZipOutputStream zipStream, String path, TFile file) throws IOException {
        ZipEntry entry = new ZipEntry(path + file.name());
        zipStream.putNextEntry(entry);
        file.copyTo(zipStream);
        zipStream.closeEntry();
      }
    });
  }

  public void visit(FileVisitor fileVisitor) throws TIoException {
    fileVisitor.visit(this);
  }
}
