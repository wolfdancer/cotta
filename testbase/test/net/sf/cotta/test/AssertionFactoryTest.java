package net.sf.cotta.test;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

public class AssertionFactoryTest extends TestBase {
  public void testAllMethodsHaveMatchingThatMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    AssertionFactory factory = new AssertionFactory();
    for (Method method : factory.getClass().getDeclaredMethods()) {
      if (Modifier.isPublic(method.getModifiers()) && !"that".equals(method.getName())) {
        Class<?>[] parameters = method.getParameterTypes();
        Class<?> item = ensure.that(parameters).hasOneItem();
        Method that = factory.getClass().getDeclaredMethod("that", parameters);
        if (!item.isPrimitive()) {
          method.invoke(factory, new Object[]{null});
          that.invoke(factory, new Object[]{null});
        }
      }
    }
  }
}
