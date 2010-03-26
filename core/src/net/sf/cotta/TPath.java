package net.sf.cotta;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An object presentation of path to mainly used by the implemenation of Cotta classes.
 * The methods on TPath has been exposed through TFile and TDirectory
 */
public final class TPath implements Comparable<TPath> {
  private final String headElement;
  private final String[] elements;
  private final int offset;
  private final int count;
  private int hash; // Default to 0
  private static final String WINDOWS_SEPARATOR_PATTERN = "\\\\";
  private static final char NATIVE_SEPERATOR_CHAR = '/';
  private static final String NATIVE_SEPARATOR = "/";
  private static final String ROOT_HEAD = "";
  private static final String WINDOWS_NETWORK_ROOT_HEAD = "\\\\";
  private static final String CURRENT_DIR_HEAD = ".";

  /**
   * Creates an instance of TPath with the given path elements
   *
   * @param headElement head element
   * @param elements path elements
   * @see #parse(String)
   */
  private TPath(String headElement, String[] elements) {
    int size = elements.length;
    this.elements = new String[size];
    System.arraycopy(elements, 0, this.elements, 0, size);
    this.offset = 0;
    this.count = size;
    this.headElement = headElement;
  }

  /**
   * Shares the element array for speed.
   *
   * @param headElement head element
   * @param offset the offset from which to begin in the element array
   * @param count the length
   * @param elements path elements
   */
  private TPath(String headElement, int offset, int count, String[] elements) {
    this.elements = elements;
    this.offset = offset;
    this.count = count;
    this.headElement = headElement;
  }

  /**
   * The name of the head element, e.g. "" (unix root), "c:", "."
   *
   * @return the name of the head element
   */
  public String headElement() {
    return headElement;
  }

  /**
   * The name of the last element, used by TFile and TDirectory to get the name
   *
   * @return the name of the last element
   * @see TFile#name()
   * @see TDirectory#name()
   */
  public String lastElementName() {
    if (count == 0) {
      return headElement;
    }
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
    if (count == 0) {
      return null;
    }
    return subpath(0, count - 1);
  }

  /**
   * Join with a path element to form a new path.  Used by TDirectory to get subdirectory or file
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
    return new TPath(headElement, newElements);
  }

  /**
   * Join with another relative path.  Used by TDirectory to get directory or file based on relative path.
   * This will also normalize the path so that there are no current-dir or parent-dir references.
   *
   * @param path The relative path to join
   * @return The result of the join.
   * @throws IllegalArgumentException if the path passed in is not a relative path
   * @see TDirectory#file(TPath)
   * @see TDirectory#dir(TPath)
   */
  public TPath join(TPath path) {
    return append(path).normalize();
  }

  private TPath normalize() {
    Stack<String> result = new Stack<String>();
    int off = offset;
    for (int i = 0; i < count; i++ ) {
      String element = elements[off++];
      if (isCurrentDirectoryReference(element)) {
        // do nothing
      } else if (isParentDirectoryReference(element)) {
        if (result.isEmpty()) {
          if (!isRelative()) {
            throw new IllegalArgumentException("Cannot normalize <" + toPathString() + ">");
          }
          result.push(element);
        }
        else {
          result.pop();
        }
      } else {
        result.push(element);
      }
    }
    return new TPath(headElement, result.toArray(new String[result.size()]));
  }

  /**
   * Append with another path, without normalizing.
   *
   * @param path The relative path to append
   * @return The result of the append
   */
  public TPath append(TPath path) {
    int length = count + path.count;
    String[] joined = new String[length];
    System.arraycopy(elements, offset, joined, 0, count);
    System.arraycopy(path.elements, path.offset, joined, count, path.count);
    return new TPath(headElement, joined);
  }

  public TPath intern() {
    if (offset == 0 && count == elements.length) {
      return this;
    }
    String[] newElements = new String[count];
    System.arraycopy(elements, offset, newElements, 0, count);
    return new TPath(headElement, newElements);
  }

  public boolean equals(Object o) {
    if (this == o) return true;

    if (o instanceof TPath) {
      TPath tPath = (TPath) o;
      if (headElement == tPath.headElement || headElement.equals(tPath.headElement)) {
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
    }
    return false;
  }

  public int hashCode() {
    int h = hash;
    if (h == 0) {
      h = 29 * h + headElement.hashCode();

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
    Pattern currentDir = Pattern.compile("(\\.)|(\\.(\\\\|/)(.*))");
    Pattern windowsRoot = Pattern.compile("([A-Z|a-z]:)[\\\\|/]?(.*)");
    String headElement;
    Matcher matcher;
    if (pathString.startsWith(WINDOWS_NETWORK_ROOT_HEAD)) {
      headElement = WINDOWS_NETWORK_ROOT_HEAD;
      pathString = pathString.substring(WINDOWS_NETWORK_ROOT_HEAD.length());
    }
    else if (pathString.startsWith("\\") || pathString.startsWith("/")) {
      headElement = ROOT_HEAD;
      pathString = pathString.substring(1);
    }
    else if ((matcher = currentDir.matcher(pathString)).matches()) {
      headElement = CURRENT_DIR_HEAD;
      pathString = matcher.group(4);
      if (pathString == null) {
        pathString = "";
      }
    }
    else if ((matcher = windowsRoot.matcher(pathString)).matches()) {
      headElement = matcher.group(1);
      pathString = matcher.group(2);
    }
    else {
      headElement = CURRENT_DIR_HEAD;
    }
    pathString = pathString.replaceAll(WINDOWS_SEPARATOR_PATTERN, NATIVE_SEPARATOR);
    List<String> list = new ArrayList<String>();
    for (StringTokenizer tokenizer = new StringTokenizer(pathString, NATIVE_SEPARATOR); tokenizer.hasMoreTokens();) {
      list.add(tokenizer.nextToken());
    }
    return new TPath(headElement, list.toArray(new String[list.size()]));
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
    StringBuilder buffer = new StringBuilder();
    if (headElement == ROOT_HEAD) {
      if (count == 0) {
        buffer.append(seperator);
      }
    }
    else {
      buffer.append(headElement);
    }
    int off = offset;
    for (int i = 0; i < count; i++) {
      buffer.append(seperator).append(elements[off++]);
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
    if (count <= path.count) {
      return false;
    }
    return checkCommonElements(path) == path.count;
  }

  private boolean isParentDirectoryReference(String element) {
    return "..".equals(element);
  }

  private boolean isCurrentDirectoryReference(String element) {
    return ".".equals(element);
  }

  /**
   * Check is the current path is a relative path or absolute path
   *
   * @return true if the current path is a relative path.
   */
  public boolean isRelative() {
    return headElement == CURRENT_DIR_HEAD;
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
    int index = checkCommonElements(path);
    int numberOfThisExcessElements = count - index;
    int numberOfThatExcessElements = path.count - index;
    String[] relativePath = new String[numberOfThatExcessElements + numberOfThisExcessElements];
    for (int i = 0; i < numberOfThatExcessElements; i++) {
      relativePath[i] = "..";
    }
    System.arraycopy(elements, index, relativePath, numberOfThatExcessElements, numberOfThisExcessElements);
    return new TPath(CURRENT_DIR_HEAD, relativePath);
  }

  private int checkCommonElements(TPath path) {
    if (!headElement.equals(path.headElement)) {
      return 0;
    }
    int i = 0;
    int max = Math.min(count, path.count);
    while (i < max && elements[offset + i].equals(path.elements[path.offset + i])) {
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
    int len1 = count;
    int len2 = that.count;
    int n = Math.min(len1, len2);
    String v1[] = elements;
    String v2[] = that.elements;
    int i = offset;
    int j = that.offset;

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
      String head;
      if (beginIndex == 0) {
        head = this.headElement;
      }
      else {
        head = CURRENT_DIR_HEAD;
      }
      return new TPath(head, offset + beginIndex, endIndex - beginIndex, elements);
    }
  }

  public TPath subpath(int beginIndex) {
    return subpath(beginIndex, count);
  }

  public TPath trim() {
    int len = count;
    int st = 0;
    
    while (st < len && isCurrentDirectoryReference(elements[offset + st])) {
      st++;
    }
    while (st < len && isCurrentDirectoryReference(elements[offset + len - 1])) {
      len--;
    }
    return (st > 0 || len < count) ? subpath(st, len) : this;
  }

  public String elementAt(int i) {
    return elements[i];
  }

  public int length() {
    return count;
  }

  String[] toElementArray() {
    String[] result = new String[count];
    System.arraycopy(elements, offset, result, 0, count);
    return result;
  }
}
