package net.sf.cotta.system;

import net.sf.cotta.PathContent;
import net.sf.cotta.PathSeparator;
import net.sf.cotta.TDirectoryNotFoundException;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.memory.ListingOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeBasedDirectoryIndex<F extends FileContent> extends AbstractDirectoryIndex<F> {

  private Map<String, DirTreeNode> roots = new HashMap<String, DirTreeNode>();

  public TreeBasedDirectoryIndex(ContentManager<F> contentManager) {
    super(contentManager);
    init();
  }

  public TreeBasedDirectoryIndex(PathSeparator separator, ListingOrder order, ContentManager<F> contentManager) {
    super(separator, order, contentManager);
    init();
  }

  private void init() {
    roots.put("", new DirTreeNode());
    roots.put(".", new DirTreeNode());
  }

  public boolean fileExists(TPath path) {
    return findFile(path) != null;
  }

  public boolean dirExists(TPath path) {
    return findDir(path) != null;
  }

  public PathContent list(TPath path) {
    DirTreeNode dir = findDir(path);
    List<TPath> dirs = new ArrayList<TPath>(dir.dirs().size());
    for (String dirName : dir.dirs()) {
      dirs.add(path.join(dirName));
    }
    List<TPath> files = new ArrayList<TPath>(dir.files().size());
    for (String fileName : dir.files()) {
      files.add(path.join(fileName));
    }
    sort(dirs);
    sort(files);
    return new PathContent(dirs, files);
  }

  public F fileContent(TPath path) {
    return findFile(path);
  }

  public F createFile(TPath path) throws TIoException {
    validateBeforeCreateFile(path);
    DirTreeNode parentDir = findDir(path.parent());
    if (parentDir == null) {
      throw new TIoException(path, "parent needs to be created first");
    }
    F f = contentManager.createFileContent();
    parentDir.addFile(path.lastElementName(), f);
    return f;
  }

  public void deleteFile(TPath path) throws TFileNotFoundException {
    DirTreeNode parent = findDir(path.parent());
    if (parent == null || parent.getFile(path.lastElementName()) == null) {
      throw new TFileNotFoundException(path);
    }
    parent.removeFile(path.lastElementName());
  }

  public void moveFile(TPath source, TPath dest) throws TIoException {
    F f = findFile(source);
    DirTreeNode destParent = findDir(dest.parent());
    destParent.addFile(dest.lastElementName(), f);
    findDir(source.parent()).removeFile(source.lastElementName());
  }

  public void createDir(TPath path) throws TIoException {
    if (dirExists(path)) {
      throw new IllegalArgumentException(path.toPathString() + " already exists");
    }
    if (fileExists(path)) {
      throw new TIoException(path, "already exists as a file");
    }

    DirTreeNode current = roots.get(path.headElement());
    for (int i = 0; i < path.length(); i++) {
      DirTreeNode child = current.getDir(path.elementAt(i));
      if (child == null) {
        child = current.addDir(path.elementAt(i));
      }
      current = child;
    }
  }

  public void deleteDir(TPath path) throws TIoException {
    DirTreeNode parent = findDir(path.parent());
    if (parent == null || parent.getDir(path.lastElementName()) == null) {
      throw new TDirectoryNotFoundException(path);
    }
    if (!findDir(path).isEmpty()) {
      throw new TIoException(path, "Directory not empty");
    }
    parent.removeDir(path.lastElementName());
  }

  public void moveDir(TPath source, TPath dest) throws TIoException {
    DirTreeNode sourceDir = findDir(source.parent()).removeDir(source.lastElementName());
    DirTreeNode destParent = findDir(dest.parent());
    destParent.addDir(dest.lastElementName(), sourceDir);
  }

  private F findFile(TPath path) {
    TPath parent = path.parent();
    DirTreeNode dir = parent != null ? findDir(parent) : roots.get(path.headElement());
    F file = dir != null ? dir.getFile(path.lastElementName()) : null;
    return file;
  }

  private DirTreeNode findDir(TPath path) {
    DirTreeNode current = roots.get(path.headElement());
    for (int i = 0; i < path.length() && current != null; i++) {
      current = current.getDir(path.elementAt(i));
    }
    return current;
  }

  private class DirTreeNode {
    private Map<String, F> files = new HashMap<String, F>();
    private Map<String, DirTreeNode> dirs = new HashMap<String, DirTreeNode>();

    DirTreeNode addDir(String name) {
      DirTreeNode d = new DirTreeNode();
      addDir(name, d);
      return d;
    }
    void addDir(String name, DirTreeNode d) {
      dirs.put(name, d);
    }
    void addFile(String name, F file) {
      files.put(name, file);
    }
    DirTreeNode removeDir(String name) {
      return dirs.remove(name);
    }
    void removeFile(String name) {
      files.remove(name);
    }
    DirTreeNode getDir(String name) {
      return dirs.get(name);
    }
    F getFile(String name) {
      return files.get(name);
    }
    Collection<String> dirs() {
      return dirs.keySet();
    }
    Collection<String> files() {
      return files.keySet();
    }
    boolean isEmpty() {
      return dirs.isEmpty() && files.isEmpty();
    }
  }
}
