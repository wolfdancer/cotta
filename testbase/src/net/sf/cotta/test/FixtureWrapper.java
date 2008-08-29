package net.sf.cotta.test;

class FixtureWrapper {
  private TestFixture fixture;
  private boolean called;
  private int expectedCalls;

  public FixtureWrapper(TestFixture fixture) {
    this.fixture = fixture;
  }

  public void setUp() throws Exception {
    if (called) {
      return;
    }
    called = true;
    fixture.setUp();
  }

  public void tearDown() throws Exception {
    expectedCalls--;
    if (expectedCalls == 0) {
      fixture.tearDown();
    }
  }

  public void increaseCount() {
    expectedCalls++;
  }

  public void beforeMethod(TestBase testBase) throws Exception {
    fixture.beforeMethod(testBase);
  }

  public void afterMethod(TestBase testBase) throws Exception {
    fixture.afterMethod(testBase);
  }
}
