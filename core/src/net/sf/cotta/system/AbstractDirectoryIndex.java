package net.sf.cotta.system;

import net.sf.cotta.PathSeparator;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.memory.ListingOrder;

import java.util.List;

public abstract class AbstractDirectoryIndex<F extends FileContent> implements DirectoryIndex<F> {

  private final PathSeparator separator;
  private final ListingOrder order;
  protected ContentManager<F> contentManager;

  protected AbstractDirectoryIndex(ContentManager<F> contentManager) {
    this(PathSeparator.Unix, ListingOrder.NULL, contentManager);
  }

  protected AbstractDirectoryIndex(PathSeparator separator, ListingOrder order, ContentManager<F> contentManager) {
    this.separator = separator;
    this.order = order;
    this.contentManager = contentManager;
  }

  public final String pathString(TPath path) {
    return path.toPathString(separator);
  }

  public final int compare(TPath path1, TPath path2) {
    return path1.compareTo(path2);
  }

  public final boolean equals(TPath path1, TPath path2) {
    return path1.equals(path2);
  }

  public final int hashCode(TPath path) {
    return path.hashCode();
  }

  protected final void sort(List<TPath> paths) {
    order.sort(paths);
  }

  protected final void validateBeforeCreateFile(TPath path) throws TIoException {
    if (dirExists(path)) {
      throw new TIoException(path, "already exists as a directory");
    }
  }

  protected final void validateBeforeCreateDir(TPath path) throws TIoException {
    if (dirExists(path)) {
      throw new IllegalArgumentException(path.toPathString() + " already exists");
    }
    if (fileExists(path)) {
      throw new TIoException(path, "already exists as a file");
    }
  }
}
