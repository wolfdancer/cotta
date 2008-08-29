package net.sf.cotta.acceptance;

import net.sf.cotta.*;
import net.sf.cotta.physical.PhysicalFileSystem;

public class PhysicalTfsTest extends TfsTestBase {

  public void beforeMethod() throws Exception {
    super.beforeMethod();
    TDirectory directory = new TFileFactory(fileSystem()).dir("tmp");
    if (directory.exists()) {
      directory.deleteAll();
    }
  }

  protected FileSystem fileSystem() {
    return ControlledFileSystem.pathControlledFileSystem(new PhysicalFileSystem(), TPath.parse("./tmp"));
  }
}
