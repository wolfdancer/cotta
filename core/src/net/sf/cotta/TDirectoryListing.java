package net.sf.cotta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Directory listing that can return list of entries, files or directories.
 * Each returned list is a newly created mutable list.
 */
public class TDirectoryListing implements Iterable<TEntry> {
  private PathContent pathContent;
  private TFileFactory factory;
  private boolean ordered;
  private TFileFilter fileFilter = TFileFilter.ALL;
  private TDirectoryFilter directoryFilter = TDirectoryFilter.ALL;

  public TDirectoryListing(TFileFactory factory, PathContent pathContent) {
    this.pathContent = pathContent;
    this.factory = factory;
  }

  public Iterator<TEntry> iterator() {
    return entries().iterator();
  }

  public List<TEntry> entries() {
    List<TPath> dirs = pathContent.dirs();
    List<TPath> files = pathContent.files();
    ArrayList<TEntry> entries = new ArrayList<TEntry>(dirs.size() + files.size());
    addDirs(entries, dirs);
    addFiles(entries, files);
    return sortIfNeeded(entries);
  }

  private <T extends TEntry> ArrayList<T> sortIfNeeded(ArrayList<T> list) {
    if (ordered) {
      Collections.sort(list);
    }
    return list;
  }

  @SuppressWarnings({"unchecked"})
  private void addFiles(ArrayList entries, List<TPath> files) {
    for (TPath path : files) {
      TFile file = factory.file(path);
      if (fileFilter.accept(file)) {
        entries.add(file);
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  private void addDirs(ArrayList entries, List<TPath> dirs) {
    for (TPath path : dirs) {
      TDirectory dir = factory.dir(path);
      if (directoryFilter.accept(dir)) {
        entries.add(dir);
      }
    }
  }

  /**
   * Sets the ordered flag to true
   *
   * @return current instance
   */
  public TDirectoryListing ordered() {
    ordered = true;
    return this;
  }

  public List<TDirectory> dirs() {
    List<TPath> paths = pathContent.dirs();
    ArrayList<TDirectory> dirs = new ArrayList<TDirectory>(paths.size());
    addDirs(dirs, paths);
    return sortIfNeeded(dirs);
  }

  public List<TFile> files() {
    List<TPath> paths = pathContent.files();
    ArrayList<TFile> files = new ArrayList<TFile>(paths.size());
    addFiles(files, paths);
    return sortIfNeeded(files);
  }

  /**
   * Sets filter for file
   *
   * @param filter file filter
   * @return current instance
   */
  public TDirectoryListing filteredBy(TFileFilter filter) {
    fileFilter = filter;
    return this;
  }

  /**
   * Sets filter for directory
   *
   * @param filter directory filter
   * @return current instance
   */
  public TDirectoryListing filteredBy(TDirectoryFilter filter) {
    directoryFilter = filter;
    return this;
  }

  /**
   * Sets filter for file and directory
   *
   * @param directoryFilter directory filter
   * @param fileFilter      filter filter
   * @return current instance
   */
  public TDirectoryListing filteredBy(TDirectoryFilter directoryFilter, TFileFilter fileFilter) {
    this.directoryFilter = directoryFilter;
    this.fileFilter = fileFilter;
    return this;
  }

}
