package net.sf.cotta.utils;

public class PlatformInfoUtil {

  public static boolean isWindows() {
    String osName = System.getProperty("os.name");
    return osName != null && osName.contains("Windows");
  }
}
