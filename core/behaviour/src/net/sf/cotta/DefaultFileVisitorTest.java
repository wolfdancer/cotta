package net.sf.cotta;

import net.sf.cotta.memory.InMemoryFileSystem;
import net.sf.cotta.test.TestBase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DefaultFileVisitorTest extends TestBase {
  public void testVisitAllFiles() throws Exception {
    TFileFactory factory = new TFileFactory(new InMemoryFileSystem());
    TDirectory directory = factory.dir("/one/two");
    TFile test = directory.file("one.txt").save("test");
    TFile testTwo = directory.file("two.txt").save("testTwo");
    Visitor visitor = new Visitor();
    directory.visit(visitor);
    ensure.set(visitor.list).eq(test, testTwo);
  }

  public void testTest() throws IOException {
//    InetAddress address = InetAddress.getByName("www.google.com");
//    System.out.println("address = " + address);
    URL url = new URL("http://www.google.com");
    URLConnection connection = url.openConnection();
    HttpURLConnection urlConnection = (HttpURLConnection) connection;
    System.out.println("urlConnection.getResponseCode() = " + urlConnection.getResponseCode());
  }

  private static class Visitor extends AbstractFileVisitor {
    private List<TFile> list = new ArrayList<TFile>();

    public void visit(TFile file) {
      list.add(file);
    }
  }
}
