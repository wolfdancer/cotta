package net.sf.cotta.acceptance;

import net.sf.cotta.*;
import net.sf.cotta.physical.PhysicalFileSystem;

public class PhysicalTfsTest extends TfsTestCase {

  public void beforeMethod() throws Exception {
    super.beforeMethod();
    TDirectory directory = new TFileFactory(fileSystem()).dir("tmp");
    if (directory.exists()) {
      directory.deleteAll();
    }
  }

/*
  // manual test because it depends on the windows machine this test is running on
  public void testNetworkPath() {
    ensure.that(new TFileFactory(new PhysicalFileSystem()).dir("\\\\wolfdancer\\TempDownload")).exists();
  }
*/

  protected FileSystem fileSystem() {
    return ControlledFileSystem.pathControlledFileSystem(new PhysicalFileSystem(), TPath.parse("./tmp"));
  }
}
