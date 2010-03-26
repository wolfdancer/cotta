package net.sf.cotta.system;

public class DummyContentManager implements ContentManager<DummyFileContent> {

  private static final DummyFileContent DUMMY_FILE_CONTENT = new DummyFileContent();

  public DummyFileContent createFileContent() {
    return DUMMY_FILE_CONTENT;
  }
}
