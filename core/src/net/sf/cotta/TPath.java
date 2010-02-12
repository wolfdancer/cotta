package net.sf.cotta;

import java.io.File;
import java.util.*;


/**
 * An object presentation of path to mainly used by the implemenation of Cotta classes.
 * The methods on TPath has been exposed through TFile and TDirectory
 */
public final class TPath implements Comparable<TPath> {
  private final String[] elements;
  private final int offset;
  private final int count;
  private int hash; // Default to 0
  private static final String WINDOWS_SEPERATOR_PATTERN = "\\\\";
  private static final String WINDOWS_NETWORK_PATH = "\\\\";
  private static final char NATIVE_SEPERATOR_CHAR = '/';
  private static final String NATIVE_SEPERATOR = "/";

  /**
   * Creates an instance of TPath with the given path elements
   *
   * @param elements path elements
   * @see #parse(String)
   */
  TPath(String[] elements) {
    int size = elements.length;
    this.elements = new String[size];
    System.arraycopy(elements, 0, this.elements, 0, size);
    this.offset = 0;
    this.count = size;
  }

  /**
   * Creates an instance of TPath with possibly a subarray of the given path elements
   *
   * @param elements path elements
   * @param offset the initial offset
   * @param count the length
   */
  private TPath(String[] elements, int offset, int count) {
    if (offset < 0) {
      throw new ArrayIndexOutOfBoundsException(offset);
    }
    if (count < 0) {
      throw new ArrayIndexOutOfBoundsException(count);
    }
    if (offset > elements.length - count) {
      throw new ArrayIndexOutOfBoundsException(offset + count);
    }
    this.elements = new String[count];
    System.arraycopy(elements, offset, this.elements, 0, count);
    this.offset = 0;
    this.count = count;
  }

  /**
   * Shares the element array for speed.
   * @param offset the offset from which to begin in the element array
   * @param count the length
   * @param elements path elements
   */
  private TPath(int offset, int count, String[] elements) {
    this.elements = elements;
    this.offset = offset;
    this.count = count;
  }

  /**
   * The name of the last element, used by TFile and TDirectory to get the name
   *
   * @return the name of the last element
   * @see TFile#name()
   * @see TDirectory#name()
   */
  public String lastElementName() {
    return elements[offset + count - 1];
  }

  /**
   * The path of the parent, used by TFile and TDirectory to get the parent
   *
   * @return parent path
   * @see TFile#parent()
   * @see TDirectory#parent()
   */
  public TPath parent() {
    if (count - offset == 1) {
      return null;
    }
    return subpath(0, count - 1);
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
    String[] newElements = new String[count + 1];
    System.arraycopy(elements, offset, newElements, 0, count);
    newElements[count] = name;
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
    int off = offset;
    for (int i = 0; i < count; i++ ) {
      result.add(elements[off++]);
    }
    off = path.offset;
    for (int i = 0; i < path.count; i++) {
      String element = path.elements[off++];
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

  public TPath append(TPath path) {
    TPath left = trim();
    TPath right = path.trim();
    int length = left.count + right.count;
    String[] joined = new String[length];
    System.arraycopy(left.elements, left.offset, joined, 0, left.count);
    System.arraycopy(right.elements, right.offset, joined, left.count, right.count);
    return new TPath(joined);
  }

  public TPath intern() {
    return null;// TODO
  }

  public boolean equals(Object o) {
    if (this == o) return true;

    if (o instanceof TPath) {
      TPath tPath = (TPath) o;
      int n = count;
      if (n == tPath.count) {
        String[] e1 = elements;
        String[] e2 = tPath.elements;
        int i = offset;
        int j = tPath.offset;
        while (n-- != 0) {
          if (!e1[i++].equals(e2[j++])) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    int h = hash;
    if (h == 0) {
      int off = offset;
      for (int i = 0; i < count; i++) {
        h = 29 * h + elements[off++].hashCode();
      }
      hash = h;
    }
    return hash;
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
    if (pathString.startsWith(WINDOWS_NETWORK_PATH)) {
      head = WINDOWS_NETWORK_PATH;
      pathString = pathString.substring(WINDOWS_NETWORK_PATH.length());
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
    return top.startsWith(WINDOWS_NETWORK_PATH);
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

  /**
   * Compare two path by comparing each elements.
   *
   * @param that the other path
   * @return comparing result
   */
  public int compareTo(TPath that) {
    int len1 = this.elements.length;
    int len2 = that.elements.length;
    int n = Math.min(len1, len2);
    String v1[] = elements;
    String v2[] = that.elements;
    int i = 0;
    int j = 0;

    if (i == j) {
      int k = i;
      int lim = n + i;
      while (k < lim) {
        String s1 = v1[k];
        String s2 = v2[k];
        if (!s1.equals(s2)) {
          return s1.compareTo(s2);
        }
        k++;
      }
    } else {
      while (n-- != 0) {
        String s1 = v1[i++];
        String s2 = v2[j++];
        if (!s1.equals(s2)) {
          return s1.compareTo(s2);
        }
      }
    }
    return len1 - len2;
  }

  public TPath subpath(int beginIndex, int endIndex) {
    if (beginIndex < 0) {
      throw new ArrayIndexOutOfBoundsException(beginIndex);
    }
    if (endIndex > count) {
      throw new ArrayIndexOutOfBoundsException(endIndex);
    }
    if (beginIndex > endIndex) {
      throw new ArrayIndexOutOfBoundsException(endIndex - beginIndex);
    }
    if (beginIndex == 0 && endIndex == count) {
      return this;
    }
    else {
      return new TPath(offset + beginIndex, endIndex - beginIndex, elements);
    }
  }

  public TPath subpath(int beginIndex) {
    return subpath(beginIndex, count);
  }

  public TPath trim() {
    int len = count;
    int st = 0;
    
    while (st < len && isCurrentDirectory(elements[offset + st])) {
      st++;
    }
    while (st < len && isCurrentDirectory(elements[offset + len - 1])) {
      len--;
    }
    return (st > 0 || len < count) ? subpath(st, len) : this;
  }

  public int length() {
    return count;
  }

  TPath withNoLeadingDot() {
    if (count > 0 && isCurrentDirectory(elements[offset])) {
      return subpath(1);
    }
    return this;
  }

  String[] toElementArray() {
    String[] result = new String[count];
    System.arraycopy(elements, offset, result, 0, count);
    return result;
  }
}
