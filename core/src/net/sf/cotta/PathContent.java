package net.sf.cotta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A value object holds a list of path for files and a list of path for directories.  This is used by
 * the file system implementations to return two list in one method call
 */
public class PathContent {
  private List<TPath> files;
  private List<TPath> directories;

  /**
   * Create an empty path content.  It will create two empty lists, each with the size of half of the argument passed in
   *
   * @param totalSize total size of the content.  This parameter is used to determine the inital size of the list to create
   */
  public PathContent(int totalSize) {
    files = new ArrayList<TPath>(totalSize / 2);
    directories = new ArrayList<TPath>(totalSize / 2);
  }

  /**
   * Create path content directly with the two lists
   *
   * @param directories list of paths to the directories
   * @param files       list of paths to the files
   */
  public PathContent(Collection<TPath> directories, Collection<TPath> files) {
    this.files = new ArrayList<TPath>(files);
    this.directories = new ArrayList<TPath>(directories);
  }

  /**
   * Get the list of the paths of the files
   *
   * @return the list of the paths of the files
   */
  public List<TPath> files() {
    return files;
  }

  /**
   * Get the list of the paths of the directories
   *
   * @return the list of the paths of the directories
   */
  public List<TPath> dirs() {
    return directories;
  }

  /**
   * Add a path of the directory
   *
   * @param path path of the directory
   */
  public void addDirectoryPath(TPath path) {
    directories.add(path);
  }

  /**
   * Add a path of the file
   *
   * @param path path of the file
   */
  public void addFilePath(TPath path) {
    files.add(path);
  }
}
