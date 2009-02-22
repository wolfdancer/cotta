package net.sf.cotta.test.assertion;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;

import java.util.Enumeration;

public class TestSuiteAssert extends BaseAssert<TestSuite> {
  public TestSuiteAssert(TestSuite suite) {
    super(suite);
  }

  public void hasTest(Class testClass, String testName) {
    if (match(testClass, testName, value())) {
      return;
    }
    StringBuilder buffer = new StringBuilder("Test suite should have test of class ");
    buffer.append(" <").append(testClass).append("> with name <").append(testName).append("> but got ");
    buffer.append(value().testCount()).append(" <");
    describeActual(buffer, value());
    Assert.fail(buffer.toString());
  }

  private boolean match(Class testClass, String testName, TestSuite testSuite) {
    for (Enumeration<Test> enumeration = testSuite.tests(); enumeration.hasMoreElements();) {
      Test test = enumeration.nextElement();
      if (test instanceof TestCase) {
        if (test.getClass().equals(testClass) && ((TestCase) test).getName().equals(testName)) {
          return true;
        }
      } else if (match(testClass, testName, (TestSuite) test)) {
        return true;
      }
    }
    return false;
  }

  private void describeActual(StringBuilder buffer, TestSuite value) {
    for (Enumeration<Test> enumeration = value.tests(); enumeration.hasMoreElements();) {
      Test test = enumeration.nextElement();
      if (test instanceof TestCase) {
        TestCase testCase = (TestCase) test;
        buffer.append(testCase.getClass()).append("(").append(testCase.getName()).append(")\n");
      } else if (test instanceof TestSuite) {
        TestSuite testSuite = (TestSuite) test;
        describeActual(buffer, testSuite);
      }
    }
  }
}
