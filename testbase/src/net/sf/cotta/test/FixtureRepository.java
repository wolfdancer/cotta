package net.sf.cotta.test;

import java.lang.annotation.Annotation;
import java.util.*;

public class FixtureRepository {
  private static FixtureRepository instance = new FixtureRepository();
  private Map<String, FixtureWrapper> envFixtureMap = new HashMap<String, FixtureWrapper>();
  private Map<String, Set<FixtureWrapper>> classFixtureMap = new HashMap<String, Set<FixtureWrapper>>();

  static FixtureRepository instance() {
    return instance;
  }

  static FixtureRepository reset() {
    instance = new FixtureRepository();
    return instance;
  }

  void register(TestCase testCase) {
    for (Annotation annotation : testCase.getClass().getAnnotations()) {
      Fixture fixtureType = annotation.annotationType().getAnnotation(Fixture.class);
      if (fixtureType != null) {
        String fixtureAnnotationName = annotation.annotationType().getName();
        FixtureWrapper fixtureWrapper = envFixtureMap.get(fixtureAnnotationName);
        if (fixtureWrapper == null) {
          fixtureWrapper = load(annotation.annotationType().getName() + "Fixture");
          envFixtureMap.put(fixtureAnnotationName, fixtureWrapper);
        }
        add(testCase, fixtureWrapper);
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  private FixtureWrapper load(String fixtureClassName) {
    try {
      Class<TestFixture> fixtureClass = loadTestFixtureClass(fixtureClassName);
      return new FixtureWrapper(fixtureClass.newInstance());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("Couldn't load fixture <" + fixtureClassName + ">:" + e.getMessage(), e);
    }
  }

  @SuppressWarnings({"unchecked"})
  private Class<TestFixture> loadTestFixtureClass(String fixtureClassName) throws ClassNotFoundException {
    Class<?> clazz = Class.forName(fixtureClassName);
    if (TestFixture.class.isAssignableFrom(clazz)) {
      return (Class<TestFixture>) clazz;
    }
    throw new IllegalArgumentException("fixture class does not implement " + TestFixture.class.getName() + ": " + fixtureClassName);
  }

  private void add(TestCase testCase, FixtureWrapper fixtureWrapper) {
    String key = testCase.getClass().getName();
    Set<FixtureWrapper> fixtureWrapperList = classFixtureMap.get(key);
    if (fixtureWrapperList == null) {
      fixtureWrapperList = new HashSet<FixtureWrapper>();
      classFixtureMap.put(key, fixtureWrapperList);
    }
    fixtureWrapperList.add(fixtureWrapper);
    fixtureWrapper.increaseCount();
  }

  public void fixtureSetUp(TestCase testCase) throws Exception {
    for (FixtureWrapper info : loadFixture(testCase)) {
      info.setUp();
    }
  }

  private Set<FixtureWrapper> loadFixture(TestCase testCase) {
    Set<FixtureWrapper> infos = classFixtureMap.get(testCase.getClass().getName());
    if (infos == null) {
      infos = Collections.emptySet();
    }
    return infos;
  }

  public void fixtureTearDown(TestCase testCase) throws Exception {
    for (FixtureWrapper fixture : loadFixture(testCase)) {
      fixture.tearDown();
    }
  }

  public void beforeMethod(TestCase testCase) throws Exception {
    for (FixtureWrapper fixture : loadFixture(testCase)) {
      fixture.beforeMethod(testCase);
    }
  }

  public void afterMethod(TestCase testCase) throws Exception {
    for (FixtureWrapper fixture : loadFixture(testCase)) {
      fixture.afterMethod(testCase);
    }
  }
}
