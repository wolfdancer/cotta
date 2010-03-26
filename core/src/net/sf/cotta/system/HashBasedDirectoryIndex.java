package net.sf.cotta.system;

import net.sf.cotta.PathContent;
import net.sf.cotta.PathSeparator;
import net.sf.cotta.TDirectoryNotFoundException;
import net.sf.cotta.TFileNotFoundException;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.memory.ListingOrder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashBasedDirectoryIndex<F extends FileContent> extends AbstractDirectoryIndex<F> {
  private final Map<TPath, DirectoryContent> createDirs = new HashMap<TPath, DirectoryContent>();
  private final Map<TPath, F> createFiles = new HashMap<TPath, F>();

  public HashBasedDirectoryIndex(ContentManager<F> contentManager) {
    super(contentManager);
    init();
  }

  public HashBasedDirectoryIndex(PathSeparator separator, ListingOrder order, ContentManager<F> contentManager) {
    super(separator, order, contentManager);
    init();
  }

  private void init() {
    createDirs.put(TPath.parse("/"), new DirectoryContent());
    createDirs.put(TPath.parse("."), new DirectoryContent());
  }

  public boolean fileExists(TPath path) {
    return createFiles.containsKey(path);
  }

  public boolean dirExists(TPath path) {
    if (createDirs.containsKey(path)) {
      return true;
    }
    return false;
  }

  public PathContent list(TPath path) {
    DirectoryContent content = createDirs.get(path);
    PathContent result = new PathContent(content.dirs(), content.files());
    sort(result.files());
    sort(result.dirs());
    return result;
  }

  public F createFile(TPath path) throws TIoException {
    validateBeforeCreateFile(path);
    if (!dirExists(path.parent())) {
      throw new TIoException(path, "parent needs to be created first");
    }

    createDirs.get(path.parent()).addFile(path);
    F fileContent = contentManager.createFileContent();
    createFiles.put(path, fileContent);
    return fileContent;
  }

  public void deleteFile(TPath path) throws TFileNotFoundException {
    if (!createFiles.containsKey(path)) {
      throw new TFileNotFoundException(path);
    }
    createFiles.remove(path);
    createDirs.get(path.parent()).removeFile(path);
  }

  public void moveFile(TPath source, TPath destination) throws TIoException {
    F file = createFiles.remove(source);
    createFiles.put(destination, file);
    createDirs.get(source.parent()).removeFile(source);
    createDirs.get(destination.parent()).addFile(destination);
  }

  public void createDir(TPath path) throws TIoException {
    validateBeforeCreateDir(path);
    
    ensureDirExists(path.parent()).addDir(path);
    createDirs.put(path, new DirectoryContent());
  }

  public void deleteDir(TPath path) throws TIoException {
    if (!dirExists(path)) {
      throw new TDirectoryNotFoundException(path);
    }
    DirectoryContent directoryContent = createDirs.get(path);
    if (!directoryContent.isEmpty()) {
      throw new TIoException(path, "Directory not empty");
    }
    createDirs.remove(path);
    createDirs.get(path.parent()).removeDir(path);
  }

  public void moveDir(TPath source, TPath destination) throws TIoException {
    createDir(destination);
    PathContent content = list(source);
    moveSubDirectories(content.dirs(), destination);
    moveFiles(content.files(), destination);
    deleteDir(source);
  }

  public F fileContent(TPath path) {
    return createFiles.get(path);
  }

  private DirectoryContent ensureDirExists(TPath dir) throws TIoException {
    if (!dirExists(dir)) {
      createDir(dir);
    }
    return createDirs.get(dir);
  }

  private void moveSubDirectories(List<TPath> directories, TPath destination) throws TIoException {
    for (TPath directory : directories) {
      moveDir(directory, destination.join(directory.lastElementName()));
    }
  }

  private void moveFiles(List<TPath> files, TPath destination) throws TIoException {
    for (TPath file : files) {
      moveFile(file, destination.join(file.lastElementName()));
    }
  }

  public static class DirectoryContent {
    private Map<String, TPath> dirs = new HashMap<String, TPath>();
    private Map<String, TPath> files = new HashMap<String, TPath>();

    public Collection<TPath> dirs() {
      return dirs.values();
    }

    public void addDir(TPath directory) {
      dirs.put(directory.lastElementName(), directory);
    }

    public void addFile(TPath file) {
      files.put(file.lastElementName(), file);
    }

    public Collection<TPath> files() {
      return files.values();
    }

    public boolean isEmpty() {
      return files.isEmpty() && dirs.isEmpty();
    }

    public void removeFile(TPath file) {
      files.remove(file.lastElementName());
    }

    public void removeDir(TPath directory) {
      dirs.remove(directory.lastElementName());
    }
  }
}
