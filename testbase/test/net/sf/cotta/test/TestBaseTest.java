package net.sf.cotta.test;

import junit.framework.TestCase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TestBaseTest extends TestCase {
  private static AssertionFactory ensure = new AssertionFactory();

  public void testCallsBeforeAndAfterSettingPublicFieldsToNullAndFixtures() throws Throwable {
    TestDummy test = new TestDummy();
    TestDummy.staticField = new Object();
    FixtureRepository repository = new FixtureRepository();
    repository.register(test);
    test.runBare(repository);
    assertEquals(true, test.beforeCalled);
    assertEquals(true, test.afterCalled);
    assertNotNull(test.protectedField);
    assertNotNull(TestDummy.staticField);
    assertNull(test.publicField);
    ensure.that(SampleFixture.log).eq("setUp", "beforeMethod", "afterMethod", "tearDown");
  }

  public void testInject() {
    TestDummy test = new TestDummy();
    test.inject("String");
    ensure.that(test.injectedField).eq("String");
  }

  public void testResetAllTheFieldsInSuperClass() throws Throwable {
    TestChildDummy test = new TestChildDummy();
    test.runBare();
    assertNull(test.publicField);
  }

  @Fixture(FixtureType.ENVIRONMENT)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Sample {
  }

  public static class SampleFixture implements TestFixture {
    private static List<String> log = new ArrayList<String>();

    public void setUp() {
      log();
    }

    public void tearDown() {
      log();
    }

    public void beforeMethod(TestBase testBase) {
      log();
    }

    public void afterMethod(TestBase testBase) {
      log();
    }

    private void log() {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      log.add(stack[3].getMethodName());
    }
  }

  @Sample
  public static class TestDummy extends TestBase {
    boolean beforeCalled, afterCalled;
    public Object publicField;
    private Object privateField;
    protected Object protectedField;
    public static Object staticField;

    @ForFixture
    public String injectedField;

    public TestDummy() {
      super("testMethod");
    }

    public void testMethod() {
      publicField = new Object();
      privateField = new Object();
      protectedField = new Object();
    }

    public void beforeMethod() {
      beforeCalled = true;
    }

    public void afterMethod() {
      afterCalled = true;
    }

    public Object getPrivateField() {
      return privateField;
    }
  }

  public static class TestChildDummy extends TestDummy {
  }
}
