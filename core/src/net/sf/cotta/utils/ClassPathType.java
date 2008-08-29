package net.sf.cotta.utils;

/** @noinspection JavaDoc*/
public class ClassPathType {
  private String name;

  private ClassPathType(String name) {
    this.name = name;
  }

  public String toString() {
    return "ClassPathType: " + name;
  }

  public static final ClassPathType DIRECTORY = new ClassPathType("directory");

  public static final ClassPathType FILE = new ClassPathType("file");

}
