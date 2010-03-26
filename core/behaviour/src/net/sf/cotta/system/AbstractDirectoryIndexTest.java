package net.sf.cotta.system;

import net.sf.cotta.PathContent;
import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;
import net.sf.cotta.TestCase;
import net.sf.cotta.test.assertion.CodeBlock;

import java.util.List;

public abstract class AbstractDirectoryIndexTest extends TestCase {

  protected abstract DirectoryIndex<DummyFileContent> newDirectoryIndexWithSort();

  public void testCreateFile() throws Exception {
    final DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    TPath path = TPath.parse("/one");
    ensure.that(dirIndex.fileExists(path)).isFalse();
    dirIndex.createFile(path);
    ensure.that(dirIndex.fileExists(path)).isTrue();

    path = TPath.parse("/two/three");
    ensure.that(dirIndex.fileExists(path)).isFalse();
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        dirIndex.createFile(TPath.parse("/two/three"));
      }
    }).throwsException(TIoException.class).message().eq("parent needs to be created first</two/three>");
    dirIndex.createDir(path.parent());
    dirIndex.createFile(path);
    ensure.that(dirIndex.fileExists(path)).isTrue();
  }

  public void testCreateDir() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    TPath path = TPath.parse("/one");
    ensure.that(dirIndex.dirExists(path)).isFalse();
    dirIndex.createDir(path);
    ensure.that(dirIndex.dirExists(path)).isTrue();

    path = TPath.parse("/two/three");
    ensure.that(dirIndex.dirExists(path)).isFalse();
    dirIndex.createDir(path);
    ensure.that(dirIndex.dirExists(path)).isTrue();
  }

  public void testCreateDirForPathOfExistingFile() throws Exception {
    final DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    dirIndex.createFile(TPath.parse("/one"));
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        dirIndex.createDir(TPath.parse("/one"));
      }
    }).throwsException(TIoException.class).message().eq("already exists as a file</one>");
  }

  public void testCreateFileForPathOfExistingDir() throws Exception {
    final DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    dirIndex.createDir(TPath.parse("/one"));
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        dirIndex.createFile(TPath.parse("/one"));
      }
    }).throwsException(TIoException.class).message().eq("already exists as a directory</one>");
  }

  public void testDeleteFile() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    TPath path = TPath.parse("/one");
    dirIndex.createFile(path);
    ensure.that(dirIndex.fileExists(path)).isTrue();
    dirIndex.deleteFile(path);
    ensure.that(dirIndex.fileExists(path)).isFalse();
  }

  public void testDeleteDir() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    TPath path = TPath.parse("/one");
    dirIndex.createDir(path);
    ensure.that(dirIndex.dirExists(path)).isTrue();
    dirIndex.deleteDir(path);
    ensure.that(dirIndex.dirExists(path)).isFalse();
  }

  public void testMoveFile() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    dirIndex.createFile(TPath.parse("/one"));
    ensure.that(dirIndex.fileExists(TPath.parse("/one"))).isTrue();
    ensure.that(dirIndex.fileExists(TPath.parse("/two"))).isFalse();
    dirIndex.moveFile(TPath.parse("/one"), TPath.parse("/two"));
    ensure.that(dirIndex.fileExists(TPath.parse("/one"))).isFalse();
    ensure.that(dirIndex.fileExists(TPath.parse("/two"))).isTrue();
  }

  public void testMoveDir() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();

    dirIndex.createDir(TPath.parse("/one"));
    ensure.that(dirIndex.dirExists(TPath.parse("/one"))).isTrue();
    ensure.that(dirIndex.dirExists(TPath.parse("/two"))).isFalse();
    dirIndex.moveDir(TPath.parse("/one"), TPath.parse("/two"));
    ensure.that(dirIndex.dirExists(TPath.parse("/one"))).isFalse();
    ensure.that(dirIndex.dirExists(TPath.parse("/two"))).isTrue();
  }

  public void testList() throws Exception {
    DirectoryIndex<DummyFileContent> dirIndex = newDirectoryIndexWithSort();
    
    dirIndex.createDir(TPath.parse("/one"));
    dirIndex.createDir(TPath.parse("/two"));
    dirIndex.createFile(TPath.parse("/three"));
    dirIndex.createFile(TPath.parse("/four"));

    PathContent content = dirIndex.list(TPath.parse("/"));
    List<TPath> dirs = content.dirs();
    ensure.that(dirs).eq(TPath.parse("/one"), TPath.parse("/two"));
    List<TPath> files = content.files();
    ensure.that(files).eq(TPath.parse("/four"), TPath.parse("/three"));
  }

}
