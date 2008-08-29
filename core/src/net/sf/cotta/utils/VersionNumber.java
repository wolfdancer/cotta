package net.sf.cotta.utils;

import java.io.PrintStream;

/** @noinspection JavaDoc*/
public class VersionNumber {
  private String versionNumber;
  private String buildNumber;

  public VersionNumber(String versionNumber, String buildNumber) {
    this.versionNumber = versionNumber;
    this.buildNumber = buildNumber;
  }

  public String value() {
    return versionNumber;
  }

  public String build() {
    return buildNumber;
  }

  public void info(PrintStream out) {
    out.println("Version: " + versionNumber);
    out.println("Build: " + buildNumber);
  }
}
