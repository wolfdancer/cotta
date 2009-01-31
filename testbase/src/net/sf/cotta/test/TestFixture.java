package net.sf.cotta.test;

public interface TestFixture {
  void setUp() throws Exception;

  void tearDown() throws Exception;

  void beforeMethod(TestCase testCase) throws Exception;

  void afterMethod(TestCase testCase) throws Exception;
}
