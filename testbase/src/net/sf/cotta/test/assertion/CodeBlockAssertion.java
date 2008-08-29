package net.sf.cotta.test.assertion;

import org.junit.Assert;

public class CodeBlockAssertion {
  private CodeBlock block;

  public CodeBlockAssertion(CodeBlock block) {
    this.block = block;
  }

  public <T extends Throwable> ExceptionAssert throwsException(Class<T> expected) {
    try {
      block.execute();
    } catch (Throwable e) {
      if (expected.equals(e.getClass())) {
        return new ExceptionAssert(e);
      }
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException("Error occurred during code block:" + e.getMessage(), e);
      }
    }
    StringBuilder buffer = new StringBuilder("Exception should have been thrown: " + expected);
    Assert.fail(buffer.toString());
    return null;
  }
}
