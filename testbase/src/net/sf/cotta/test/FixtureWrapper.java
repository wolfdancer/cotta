package net.sf.cotta.test;

class FixtureWrapper {
  private TestFixture fixture;
  private boolean alarmed;
  private int expectedCalls;

  public FixtureWrapper(TestFixture fixture) {
    this.fixture = fixture;
  }

  public void setUp() throws Exception {
    if (alarmed) {
      return;
    }
    alarmed = true;
    fixture.setUp();
  }

  public void tearDown() throws Exception {
    expectedCalls--;
    if (expectedCalls == 0) {
      fixture.tearDown();
      alarmed = false;
    }
  }

  public void increaseCount() {
    expectedCalls++;
  }

  public void beforeMethod(TestCase testCase) throws Exception {
    fixture.beforeMethod(testCase);
  }

  public void afterMethod(TestCase testCase) throws Exception {
    fixture.afterMethod(testCase);
  }
}
