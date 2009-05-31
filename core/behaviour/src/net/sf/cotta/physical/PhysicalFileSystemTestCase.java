package net.sf.cotta.physical;

import net.sf.cotta.*;
import net.sf.cotta.acceptance.TfsTestCase;

public class PhysicalFileSystemTestCase extends TfsTestCase {
  protected FileSystem fileSystem;

  protected void makeSureTmpDirectoryDoesNotExist() throws TIoException {
    TDirectory directory = new TFileFactory(fileSystem).dir("tmp");
    if (directory.exists()) {
      directory.deleteAll();
    }
  }

  protected FileSystem fileSystem() {
    return ControlledFileSystem.pathControlledFileSystem(PhysicalFileSystem.instance, TPath.parse("tmp"));
  }

  public void beforeMethod() throws Exception {
    super.beforeMethod();
    fileSystem = fileSystem();
    makeSureTmpDirectoryDoesNotExist();
  }
}
