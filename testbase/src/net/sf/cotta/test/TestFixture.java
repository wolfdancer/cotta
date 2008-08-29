package net.sf.cotta.test;

public interface TestFixture {
  void setUp() throws Exception;

  void tearDown() throws Exception;

  void beforeMethod(TestBase testBase) throws Exception;

  void afterMethod(TestBase testBase) throws Exception;
}
