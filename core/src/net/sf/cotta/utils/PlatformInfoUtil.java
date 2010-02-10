package net.sf.cotta.utils;

public class PlatformInfoUtil {

  public static boolean isWindows() {
    String osName = System.getProperty("os.name");
    return "Windows".equals(osName);
  }
}
