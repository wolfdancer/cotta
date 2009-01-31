package net.sf.cotta;

import net.sf.cotta.test.assertion.CodeBlock;
import net.sf.cotta.test.assertion.ExceptionAssert;

public class TPathTest extends CottaTestCase {
  public void testNotAllowNullPathString() throws Exception {
    ExceptionAssert actualException = runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        TPath.parse(null);
      }
    });
    actualException.message().contains("null", "allowed");
  }

  public void testNotAllowEmptyString() throws Exception {
    ExceptionAssert actualException = runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        TPath.parse("");
      }
    });
    actualException.message().contains("empty", "allowed");
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
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir")).javaEquals((TPath.parse("c:\\tmp\\cotta\\testDir")));
    ensure.that(TPath.parse("c:\\tmp\\cotta\\testDir")).javaEquals((TPath.parse("c:/tmp/cotta/testDir")));
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
    ensureEquals(TPath.parse("test").parent().lastElementName(), ".");
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
    ExceptionAssert actualException = runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        path.join(relative);
      }
    });
    actualException.notNull();
    actualException.message().contains(path.toPathString(), relative.toPathString());
  }

  public void testResultToParentReferenceIfCurrentPathIsRelative() throws Exception {
    TPath path = TPath.parse("one");
    TPath relative = TPath.parse("../../");
    ensure.that(path.join(relative)).eq(TPath.parse("../"));
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
    ExceptionAssert exception = runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        absolutePath.pathFrom(relativePath);
      }
    });
    ensure.that(exception).notNull();
    exception = runAndCatch(IllegalArgumentException.class, new CodeBlock() {
      public void execute() throws Exception {
        relativePath.pathFrom(absolutePath);
      }
    });
    ensure.that(exception).notNull();
  }

}
