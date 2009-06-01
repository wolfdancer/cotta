package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class ResourceManager<P> {
  private List<Closeable> resourceList;

  public ResourceManager() {
    this(new ArrayList<Closeable>());
  }

  public ResourceManager(List<Closeable> resourceList) {
    this.resourceList = resourceList;
  }

  public void registerResource(Closeable resource) {
    resourceList.add(resource);
  }

  public void open(P processor) throws TIoException {
    boolean errorOccurred = true;
    try {
      process(processor);
      errorOccurred = false;
    } catch (TIoException e) {
      throw e;
    } catch (IOException e) {
      throw new TIoException(path(), "IO Error", e);
    } finally {
      safeClose(errorOccurred);
    }
  }

  abstract protected void process(P processor) throws IOException;

  abstract protected TPath path();

  private void safeClose(boolean errorOccurred) throws TIoException {
    try {
      close();
    } catch (IOException e) {
      if (!errorOccurred) {
        throw new TIoException(path(), "closing resource", e);
      }
    }
  }

  private void close() throws IOException {
    Collections.reverse(resourceList);
    for (Closeable Closeable : resourceList) {
      Closeable.close();
    }
  }

}
