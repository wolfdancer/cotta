package net.sf.cotta;

import java.io.File;
import java.util.*;


/**
 * An object presentation of path to mainly used by the implemenation of Cotta classes.
 * The methods on TPath has been exposed through TFile and TDirectory
 */
public class TPath {
  private TPath parent;
  private String[] elements;
  private static final String WINDOWS_SEPERATOR_PATTERN = "\\\\";
  private static final String WINDOWOS_NETWORK_PATh = "\\\\";
  private static final char NATIVE_SEPERATOR_CHAR = '/';
  private static final String NATIVE_SEPERATOR = "/";

  /**
   * Creates an instance of TPath with the array of path element
   *
   * @param elements path elements
   * @see #parse(String)
   */
  private TPath(String[] elements) {
    this.elements = elements;
  }

  /**
   * The name of the last element, used by TFile and TDirectory to get the name
   *
   * @return the name of the last element
   * @see TFile#name()
   * @see TDirectory#name()
   */
  public String lastElementName() {
    return elements[elements.length - 1];
  }

  /**
   * The path of the parent, used by TFile and TDirectory to get the parent
   *
   * @return parent path
   * @see TFile#parent()
   * @see TDirectory#parent()
   */
  public TPath parent() {
    if (elements.length == 1) {
      return null;
    }
    if (parent == null) {
      String[] newElements = new String[elements.length - 1];
      System.arraycopy(elements, 0, newElements, 0, elements.length - 1);
      parent = new TPath(newElements);
    }
    return parent;
  }

  /**
   * Join with an pat element to form a new path.  Used by TDirectory to get subdirectory or file
   *
   * @param name The name of the path element to join
   * @return The resulting path under current path
   * @see TDirectory#file(String)
   * @see TDirectory#dir(String)
   */
  public TPath join(String name) {
    String[] newElements = new String[elements.length + 1];
    System.arraycopy(elements, 0, newElements, 0, elements.length);
    newElements[elements.length] = name;
    return new TPath(newElements);
  }

  /**
   * Join with another relative path.  Used by TDirectory to get directory or file based on relative path
   *
   * @param path The relative path to join
   * @return The result of the join.
   * @throws IllegalArgumentException if the path passed in is not a relative path
   * @see TDirectory#file(TPath)
   * @see TDirectory#dir(TPath)
   */
  public TPath join(TPath path) {
    Stack<String> result = new Stack<String>();
    result.addAll(Arrays.asList(elements));
    for (int i = 0; i < path.elements.length; i++) {
      String element = path.elements[i];
      if (isCurrentDirectory(element)) {
        // do nothing
      } else if (isParentDirectoryReference(element)) {
        result.pop();
        if (result.isEmpty()) {
          if (isRelative()) {
            result.push(".");
            result.push("..");
          } else {
            throw new IllegalArgumentException("Cannot join <" + toPathString() + "> to <" + path.toPathString() + ">");
          }
        }
      } else {
        result.push(element);
      }
    }
    return new TPath(result.toArray(new String[result.size()]));
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final TPath tPath = (TPath) o;

    return Arrays.equals(elements, tPath.elements);
  }

  public int hashCode() {
    int result = 0;
    for (String element : elements) {
      result = 29 * result + element.hashCode();
    }
    return result;
  }

  /**
   * String representation of the path.  To get the path string understood by most other application
   * please use toPathString()
   *
   * @return path string
   * @see #toPathString()
   */
  public String toString() {
    return toPathString();
  }

  /**
   * Parses a path string into a TPath object.  Both '/' and '\' are treated as the file separator
   *
   * @param pathString The path string that represents the path
   * @return The path object that match to the path string
   * @throws IllegalArgumentException if the path string is null
   */
  public static TPath parse(String pathString) {
    if (pathString == null || pathString.length() == 0) {
      throw new IllegalArgumentException("null or empty path string is not allowed");
    }
    String head = "";
    if (pathString.startsWith(WINDOWOS_NETWORK_PATh)) {
      head = WINDOWOS_NETWORK_PATh;
      pathString = pathString.substring(WINDOWOS_NETWORK_PATh.length());
    }
    return new TPath(convertToElementArray(head + pathString.replaceAll(WINDOWS_SEPERATOR_PATTERN, NATIVE_SEPERATOR)));
  }

  private static String[] convertToElementArray(String pathString) {
    boolean identifiedAsReferencedByRoot = false;
    List<String> list = new ArrayList<String>();
    if (pathString.startsWith("/")) {
      list.add("");
      identifiedAsReferencedByRoot = true;
    }
    for (StringTokenizer tokenizer = new StringTokenizer(pathString, NATIVE_SEPERATOR); tokenizer.hasMoreTokens();) {
      list.add(tokenizer.nextToken());
    }
    if (!identifiedAsReferencedByRoot
        && !isTopElementADrivePath(list)
        && !isTopElementWorkingDirectory(list)
        && !isTopElementWindowsNetworkHost(list)) {
      list.add(0, ".");
    }
    return list.toArray(new String[list.size()]);
  }

  private static boolean isTopElementWorkingDirectory(List<String> list) {
    String top = list.get(0);
    return top.equals(".");
  }

  private static boolean isTopElementADrivePath(List<String> list) {
    String top = list.get(0);
    return top.matches("[A-Z|a-z]:");
  }

  private static boolean isTopElementWindowsNetworkHost(List<String> list) {
    String top = list.get(0);
    return top.startsWith(WINDOWOS_NETWORK_PATh);
  }

  public String toPathString() {
    return toPathStringImpl(NATIVE_SEPERATOR_CHAR);
  }

  public String toPathString(PathSeparator pathSeparator) {
    return toPathStringImpl(pathSeparator.getValue());
  }

  public String toSystemPathString() {
    return toPathStringImpl(File.separatorChar);
  }

  private String toPathStringImpl(char seperator) {
    StringBuffer buffer = new StringBuffer();
    for (String element : elements) {
      buffer.append(element).append(seperator);
    }
    if (buffer.length() > 1) {
      buffer.delete(buffer.length() - 1, buffer.length());
    }
    return buffer.toString();
  }

  /**
   * Check if current path is the child of the given path.  This used by TFile and TDirectory
   * to see if it is under another directory
   *
   * @param path The path to check to see if current path is its child
   * @return true if current path is the child of the given path
   * @see TFile#isChildOf(TDirectory)
   * @see TDirectory#isChildOf(TDirectory)
   */
  public boolean isChildOf(TPath path) {
    if (elements.length <= path.elements.length) {
      return false;
    }
    return checkCommonElements(elements, path.elements) == path.elements.length;
  }

  private boolean isParentDirectoryReference(String element) {
    return "..".equals(element);
  }

  private boolean isCurrentDirectory(String element) {
    return ".".equals(element);
  }

  /**
   * Check is the current path is a relative path or absolute path
   *
   * @return true if the current path is a relative path.
   */
  public boolean isRelative() {
    return isCurrentDirectory(elements[0]);
  }

  /**
   * Dericve the relative path from the other path, to be used by TFile and TDirectory
   *
   * @param path The other path to derive relative path from
   * @return The relative path from the other path
   */
  public TPath pathFrom(TPath path) {
    if (isRelative() ^ path.isRelative()) {
      throw new IllegalArgumentException("path passed in should be the same kind of relative or absolute path:" + path);
    }
    int index = checkCommonElements(elements, path.elements);
    int numberOfParentsToGo = path.elements.length - index;
    String[] relativePath = new String[elements.length - index + 1 + numberOfParentsToGo];
    relativePath[0] = ".";
    for (int i = 1; i <= numberOfParentsToGo; i++) {
      relativePath[i] = "..";
    }
    System.arraycopy(elements, index, relativePath, 1 + numberOfParentsToGo, elements.length - index);
    return new TPath(relativePath);
  }

  private int checkCommonElements(String[] elements1, String[] elements2) {
    int max = Math.min(elements1.length, elements2.length);
    int i = 0;
    while (i < max && elements1[i].equals(elements2[i])) {
      i++;
    }
    return i;
  }
}
