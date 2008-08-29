package net.sf.cotta.test;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestBase extends TestCase {
  public TestBase() {
    loadFixtures();
  }

  public TestBase(String name) {
    super(name);
    loadFixtures();
  }

  private void loadFixtures() {
    FixtureRepository.instance().register(this);
  }

  public void runBare() throws Throwable {
    runBare(FixtureRepository.instance());
  }

  void runBare(FixtureRepository repository) throws Throwable {
    repository.fixtureSetUp(this);
    try {
      reallyRunBare(repository);
    } finally {
      repository.fixtureTearDown(this);
      resetsFieldsToSaveMemoryForLargeTestSuite();
    }
  }

  private void resetsFieldsToSaveMemoryForLargeTestSuite() {
    Class<?> aClass = getClass();
    for (Class<?> testClass = aClass; !testClass.equals(TestBase.class); testClass = testClass.getSuperclass()) {
      reset(testClass);
    }
  }

  private void reset(Class<?> aClass) {
    for (Field field : aClass.getDeclaredFields()) {
      reset(field);
    }
  }

  private void reset(Field field) {
    if (Modifier.isPublic(field.getModifiers())
        && !Modifier.isStatic(field.getModifiers())) {
      try {
        field.set(this, null);
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("couldn't reset field " + field.getName(), e);
      }
    }
  }

  private void reallyRunBare(FixtureRepository repository) throws Throwable {
    Throwable exception = null;
    repository.beforeMethod(this);
    beforeMethod();
    try {
      runTest();
    } catch (Throwable running) {
      exception = running;
    }
    finally {
      try {
        afterMethod();
      } catch (Throwable tearingDown) {
        if (exception == null) exception = tearingDown;
      }
      repository.afterMethod(this);
    }
    if (exception != null) throw exception;
  }

  /**
   * @throws Exception
   * @deprecated call beforeMethod instead
   */
  @Deprecated
  final protected void setUp() throws Exception {
    throw new UnsupportedOperationException("you should call super.beforeMethod() instead.  Otherwise super.beforeMethod() might be skipped");
  }

  /**
   * @throws Exception
   * @deprecated call afterMethod instead
   */
  @Deprecated
  final protected void tearDown() throws Exception {
    throw new UnsupportedOperationException("you should call super.afterMethod() instead.  Otherwise super.afterMethod() might be skipped");
  }

  public void beforeMethod() throws Exception {
  }

  public void afterMethod() throws Exception {
  }

  protected AssertionFactory ensure() {
    return new AssertionFactory();
  }

  public static AssertionFactory ensure = new AssertionFactory();

  public void inject(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("Cannot set null value to the field");
    }
    for (Field field : getClass().getFields()) {
      ForFixture forFixture = field.getAnnotation(ForFixture.class);
      if (forFixture != null && field.getType().equals(value.getClass())) {
        try {
          field.set(this, value);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException("Couldn't inject value to field " + field);
        }
      }
    }
  }
}
