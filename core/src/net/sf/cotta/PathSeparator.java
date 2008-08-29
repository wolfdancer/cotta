package net.sf.cotta;

public class PathSeparator {
  private char value;

  private PathSeparator(char value) {
    this.value = value;
  }

  public char getValue() {
    return value;
  }

  public static final PathSeparator Windows = new PathSeparator('\\'); 
  public static final PathSeparator Unix = new PathSeparator('/'); 
}
