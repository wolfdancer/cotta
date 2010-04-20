package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @noinspection JavaDoc
 */
public class CatastrophicFileSystem extends ControlledFileSystem {

  public CatastrophicFileSystem() {
    super(new InMemoryFileSystem(), new CatastrophicController());
  }

  public void diskFull() {
    ((CatastrophicController) controller).diskFull();
  }

  public void lockFile(TPath path) {
    lockFile(path, 0);
  }

  public void lockFile(TPath path, int counter) {
    ((CatastrophicController) controller).lockFile(path, counter);
  }

  public void unLockFile(TPath path) {
    ((CatastrophicController) controller).unLockFile(path);
  }

  public void diskErrorFor(TPath path) {
    ((CatastrophicController) controller).diskError(path);
  }

  private static class CatastrophicController implements Controller {
    private boolean diskFull;
    private Map<TPath, Integer> fileLock = new HashMap<TPath, Integer>();
    private Set<TPath> readError = new HashSet<TPath>();

    public void writeOperationControl(TPath path) throws TIoException {
      checkError(path);
      checkLock(path);
    }

    private void checkLock(TPath path) throws TIoException {
      Integer counter = fileLock.get(path);
      if (counter != null) {
        counter = counter - 1;
        if (counter == 0) {
          fileLock.remove(path);
        } else {
          fileLock.put(path, counter);
        }
        throw new TIoException(path, "File locked");
      }
    }

    private void checkError(TPath path) throws TIoException {
      if (diskFull) {
        throw new TIoException(path, "Disk is full");
      }
    }

    public void readOperationControl(TPath path) throws TIoException {
      if (readError.contains(path)) {
        throw new TIoException(path, "Disk Error");
      }
    }

    public void diskFull() {
      diskFull = true;
    }

    public void lockFile(TPath path, int i) {
      fileLock.put(path, i);
    }

    public void unLockFile(TPath path) {
      fileLock.remove(path);
    }

    public void diskError(TPath path) {
      readError.add(path);
    }
  }

}
