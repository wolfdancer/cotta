package net.sf.cotta.utils;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TIoException;
import net.sf.cotta.TestCase;
import net.sf.cotta.test.assertion.CodeBlock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ProductInfoTest extends TestCase {
  public void testLoadWithThePathPointingToClass() throws Exception {
    ProductInfo productInfo = forCottaResource();
    TDirectory directory = productInfo.loadedPath().openAsDirectory();
    ensure.that(directory.file("META-INF/cotta.txt").exists()).eq(true);
  }

  public void testThrowTIoExceptionIfManifestNotFound() throws Exception {
    ensure.code(new CodeBlock() {
      public void execute() throws Exception {
        ProductInfo.forClass(getClass());
      }
    }).throwsException(TIoException.class);
  }

  public void testAquireInformationFromManifest() throws Exception {
    ProductInfo productInfo = forCottaResource();
    ensure.that(productInfo.mainAttributeValue("Not-Available")).isNull();
    ensure.that(productInfo.otherAttributeValue("No-Such-Section", "Name")).isNull();
    ensure.that(productInfo.mainAttributeValue("Implementation-Title")).eq("Cotta");
    ensure.that(productInfo.otherAttributeValue("Dependencies", "Dependency-Count")).eq("0");
  }

  public void testUnderstandPredefinedInformation() throws TIoException {
    ProductInfo productInfo = forCottaResource();
    ensure.that(productInfo.title()).eq("Cotta");
    ensure.that(productInfo.vendor()).eq("SourceForge Cotta");
    ensure.that(productInfo.url()).eq("http://cotta.sourceforge.net");
    ensure.that(productInfo.version().value()).eq("2.1.0");
    ensure.that(productInfo.version().build()).notNull();
  }

  public void testPrintOutInfo() throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    forCottaResource().info(printer);
    String result = output.toString();
    ensure.that(result).contains("SourceForge Cotta");
  }

  private ProductInfo forCottaResource() throws TIoException {
    ClassPathEntry pathEntry = new ClassPathEntryLocator("/META-INF/cotta.txt").locateEntry();
    return new ProductInfo(pathEntry);
  }

}
