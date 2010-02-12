package net.sf.cotta;

import net.sf.cotta.test.assertion.CodeBlock;

public class TPathTest extends TestCase {
  public void testNotAllowNullPathString() throws Exception {
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        TPath.parse(null);
      }
    }).throwsException(IllegalArgumentException.class)
        .message().contains("null", "allowed");
  }

  public void testNotAllowEmptyString() throws Exception {
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        TPath.parse("");
      }
    }).throwsException(IllegalArgumentException.class)
        .message().contains("empty", "allowed");
  }

  public void testHaveCurrentWorkingDirectoryIfNotAbsolute() throws Exception {
    TPath path = TPath.parse("test");
    ensure.that(path.parent().lastElementName()).eq(".");
  }

  public void testSupportWindowsNetworkPath() {
    TPath path = TPath.parse("\\\\host\\dir\\file");
    ensure.that(path.lastElementName()).eq("file");
    TPath parent = path.parent();
    ensure.that(parent.lastElementName()).eq("dir");
    TPath host = parent.parent();
    ensure.that(host.lastElementName()).eq("\\\\host");
  }

  public void testBeAbleToGetLastElementName() throws Exception {
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir").lastElementName()).eq("testDir");
    ensure.that(TPath.parse("c:/tmp/cotta/testDir").lastElementName()).eq("testDir");
    ensure.that(TPath.parse("testDir").lastElementName()).eq("testDir");
    ensure.that(TPath.parse("c:").lastElementName()).eq("c:");
  }

  public void testBeAbleToJoinName() throws Exception {
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir").join("subDir").lastElementName()).eq("subDir");
    ensure.that(TPath.parse("c:/tmp/cotta/testDir").join("subDir").lastElementName()).eq("subDir");
  }

  public void testBeEqualWithSamePathInformation() throws Exception {
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir")).eqWithHash((TPath.parse("c:\\tmp\\cotta\\testDir")));
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir")).eqWithHash((TPath.parse("c:/tmp/cotta/testDir")));
  }

  public void testBeAbleToConstructParent() throws Exception {
    ensure.that(TPath.parse("/tmp/one/two").parent()).eq(TPath.parse("/tmp/one"));
  }

  public void testReturnNullParentFromRoot() throws Exception {
    ensure.that(TPath.parse("/").parent()).isNull();
    ensure.that(TPath.parse("C:").parent()).isNull();
  }

  public void testProvideFullPathInToString() throws Exception {
    ensure.that(TPath.parse("/").toString()).eq("/");
    ensure.that(TPath.parse("C:/").toString()).eq("C:");
  }

  public void testConstructPathStringUsingSeperator() throws Exception {
    ensure.that(TPath.parse("/tmp/one/two/").toPathString()).eq("/tmp/one/two");
  }

  public void testKnowWhenCurrentWorkingDirectoryIsUsed() throws Exception {
    ensure.that(TPath.parse("test").parent().lastElementName()).eq(".");
  }

  public void testKnowItsHierarchy() throws Exception {
    TPath pathOne = TPath.parse("/tmp/test/sub");
    TPath pathTwo = TPath.parse("/tmp");
    TPath pathThree = TPath.parse("/test");
    ensure.that(pathOne.isChildOf(pathTwo)).eq(true);
    ensure.that(pathOne.isChildOf(pathThree)).eq(false);
  }

  public void testUnderstandCurrentDirectoryNotation() throws Exception {
    TPath path = TPath.parse("./tmp");
    ensure.that(path.parent().lastElementName()).eq(".");
    ensure.that(path.parent().parent()).isNull();
  }

  public void testJoinAnotherPath() throws Exception {
    TPath pathOne = TPath.parse("/one/two");
    TPath pathTwo = TPath.parse("three/four");
    ensure.that(pathOne.join(pathTwo)).eq(TPath.parse("/one/two/three/four"));
  }

  public void testEndUpWithParentWhenJoinToParentReference() throws Exception {
    TPath path = TPath.parse("/one/two/three/four");
    TPath relativePath = TPath.parse("../../five");
    ensure.that(path.join(relativePath)).eq(TPath.parse("/one/two/five"));
  }

  public void testNotAllowGoingToParentIfCurrentPathIsAbsolute() throws Exception {
    final TPath path = TPath.parse("/one/two");
    final TPath relative = TPath.parse("../../../");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        path.join(relative);
      }
    }).throwsException(IllegalArgumentException.class)
        .message().contains(path.toPathString(), relative.toPathString());
  }

  public void testResultToParentReferenceIfCurrentPathIsRelative() throws Exception {
    TPath path = TPath.parse("one");
    TPath relative = TPath.parse("../../");
    ensure.that(path.join(relative)).eq(TPath.parse("../"));
  }

  public void testAppendAnotherPath() throws Exception {
    TPath pathOne = TPath.parse("/one/two");
    TPath pathTwo = TPath.parse("three/four");
    ensure.that(pathOne.append(pathTwo)).eq(TPath.parse("/one/two/three/four"));
  }

  public void testAppendWillStripCurrentDirElements() throws Exception {
    TPath pathOne = TPath.parse("/one/two/./.");
    TPath pathTwo = TPath.parse("././three/four/./.");
    ensure.that(pathOne.append(pathTwo)).eq(TPath.parse("/one/two/three/four"));

  }

  public void testAbsolutePath() throws Exception {
    ensure.that(TPath.parse("/one/two").isRelative()).eq(false);
  }

  public void testKnowRelativePath() throws Exception {
    ensure.that(TPath.parse("./one/two").isRelative()).eq(true);
  }

  public void testBeAbleToDeriveRelativePath() throws Exception {
    TPath path = TPath.parse("/one/two/three");
    TPath result = path.pathFrom(TPath.parse("/one"));
    ensure.that(result).eq(TPath.parse("two/three"));
  }

  public void testUseParentDirectoryNotation() throws Exception {
    TPath path = TPath.parse("/one/two/dir1/three");
    TPath result = path.pathFrom(TPath.parse("/one/two/three/four"));
    ensure.that(result).eq(TPath.parse("../../dir1/three"));
  }

  public void testNotMixRelativePathAndAbsolutePathForCalculatingRelativePath() throws Exception {
    final TPath absolutePath = TPath.parse("/absolute/path");
    final TPath relativePath = TPath.parse("./relative/path");
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        absolutePath.pathFrom(relativePath);
      }
    }).throwsException(IllegalArgumentException.class);
    ensure.that(new CodeBlock() {
      public void execute() throws Exception {
        relativePath.pathFrom(absolutePath);
      }
    }).throwsException(IllegalArgumentException.class);
  }

  public void testCompare() {
    TPath a = TPath.parse("/a");
    TPath b = TPath.parse("/b");
    ensure.that(a.compareTo(b)).eq("a".compareTo("b"));
  }

  public void testCompareChecksAllElements() {
    TPath a = TPath.parse("/one/two/a");
    TPath b = TPath.parse("/one/two/b");
    ensure.that(a.compareTo(b)).eq("a".compareTo("b"));
  }

  public void testSubpathWith1Arg() {
    TPath path = TPath.parse("./one/two");
    TPath subpath = path.subpath(1);
    ensure.that(subpath.toElementArray()).eq("one", "two");
    ensure.that(subpath.toElementArray()[0]).sameAs(path.toElementArray()[1]);
  }

  public void testSubpathWith2Args() {
    TPath path = TPath.parse("./one/two");
    TPath subpath = path.subpath(1, 3);
    ensure.that(subpath.toElementArray()).eq("one", "two");
    ensure.that(subpath.toElementArray()[0]).sameAs(path.toElementArray()[1]);

    subpath = path.subpath(0, 2);
    ensure.that(subpath.toElementArray()).eq(".", "one");
    ensure.that(subpath.toElementArray()[0]).sameAs(path.toElementArray()[0]);
  }

  public void testSubpathHash() {
    TPath path = TPath.parse("./one/two");
    TPath subpath = path.subpath(1);
    TPath expected = path.withNoLeadingDot();
    ensure.that(subpath).eqWithHash(expected);

    subpath = path.subpath(1, 3);
    // (same expected)
    ensure.that(subpath).eqWithHash(expected);

    subpath = path.subpath(0, 2);
    expected = TPath.parse("./one");
    ensure.that(subpath).eqWithHash(expected);
  }

  public void testSubpathParent() {
    // TODO
  }

  public void testSubpathJoin1Element() {
    TPath path = TPath.parse("./one/two");
    TPath subpath = path.subpath(1);
    TPath joined = subpath.join("three");
    ensure.that(joined.toElementArray()).eq("one", "two", "three");

    subpath = path.subpath(0, 2);
    joined = subpath.join("three");
    ensure.that(joined.toElementArray()).eq(".", "one", "three");
  }

  public void testSubpathJoinPath() {
    TPath leftPath = TPath.parse("./one/two");
    TPath rightPath = TPath.parse("./three/four");
    TPath leftSubpath = leftPath.subpath(1);
    TPath joined = leftSubpath.join(rightPath);
    ensure.that(joined.toElementArray()).eq("one", "two", "three", "four");

    leftSubpath = leftPath.subpath(0, 2);
    joined = leftSubpath.join(rightPath);
    ensure.that(joined.toElementArray()).eq(".", "one", "three", "four");
  }

  public void testPathJoinSubpath() {
    TPath leftPath = TPath.parse("./one/two");
    TPath rightPath = TPath.parse("./three/four");
    TPath rightSubpath = rightPath.subpath(2);
    TPath joined = leftPath.join(rightSubpath);
    ensure.that(joined.toElementArray()).eq(".", "one", "two", "four");

    rightSubpath = rightPath.subpath(0, 2);
    joined = leftPath.join(rightSubpath);
    ensure.that(joined.toElementArray()).eq(".", "one", "two", "three");
  }

  public void testTrim() {
    TPath path = new TPath(new String[] {"one", "two"});
    ensure.that(path.trim()).sameAs(path);

    path = new TPath(new String[] {"one", ".", "two"});
    ensure.that(path.trim()).sameAs(path);

    path = new TPath(new String[] {".", "one", "two"});
    ensure.that(path.trim().toElementArray()).eq("one", "two");

    path = new TPath(new String[] {".", ".", "one", "two"});
    ensure.that(path.trim().toElementArray()).eq("one", "two");

    path = new TPath(new String[] {"one", "two", "."});
    ensure.that(path.trim().toElementArray()).eq("one", "two");

    path = new TPath(new String[] {"one", "two", ".", "."});
    ensure.that(path.trim().toElementArray()).eq("one", "two");
  }

  public void testWithNoLeadingDot() {
    TPath path = TPath.parse("one/two");
    ensure.that(path.toElementArray()).eq(".", "one", "two");
    ensure.that(path.withNoLeadingDot().toElementArray()).eq("one", "two");

    path = new TPath(new String[]{"one", "two"});
    ensure.that(path.withNoLeadingDot()).sameAs(path);

    path = new TPath(new String[0]);
    ensure.that(path.withNoLeadingDot()).sameAs(path);
  }
}
