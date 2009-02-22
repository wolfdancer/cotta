package net.sf.cotta.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FixtureRepositoryTest extends junit.framework.TestCase {
  private static AssertionFactory ensure = new AssertionFactory();
  private FixtureRepository repository;

  protected void setUp() throws Exception {
    super.setUp();
    repository = FixtureRepository.reset();
  }

  protected void tearDown() throws Exception {
    repository = null;
    super.tearDown();
  }

  public void testLoadingTestFixtureShouldCheckTypeForBetterMessage() {
    try {
      new TestWithAnnotationProblem();
      fail("should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      ensure.that(e).message().contains(TestFixture.class.getName());
    }
  }

  public void testFixtureOnlyCallsFirstSetupAndLastTearDown() throws Exception {
    TestDummy testOne = new TestDummy();
    TestDummy testTwo = new TestDummy();
    repository = FixtureRepository.reset();
    repository.register(testOne);
    repository.register(testTwo);
    repository.fixtureSetUp(testOne);
    assertEquals(1, SampleFixture.count);
    repository.fixtureTearDown(testOne);
    assertEquals(1, SampleFixture.count);
    repository.fixtureSetUp(testTwo);
    assertEquals(1, SampleFixture.count);
    repository.fixtureTearDown(testTwo);
    assertEquals(0, SampleFixture.count);
  }

  @Sample
  private static class TestDummy extends TestCase {}

  @Fixture(FixtureType.ENVIRONMENT)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Sample {
  }

  public static class SampleFixture implements TestFixture {
    private static int count = 0;

    public void setUp() {
      count++;
    }

    public void tearDown() {
      count--;
    }

    public void beforeMethod(TestCase testCase) {
    }

    public void afterMethod(TestCase testCase) {
    }

  }

  @FixtureClassNotTestFixture
  private static class TestWithAnnotationProblem extends TestCase {}

  @Retention(RetentionPolicy.RUNTIME)
  @Fixture(FixtureType.ENVIRONMENT)
  public static @interface FixtureClassNotTestFixture {
  }

  public static class FixtureClassNotTestFixtureFixture {
  }
}
