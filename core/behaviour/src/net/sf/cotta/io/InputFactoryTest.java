package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;

public class InputFactoryTest extends TestCase {
  public void testCreateInputStream() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final InputStreamFactory factory = mockFactoryForInput(stub);
    ensure.that(new InputFactory(factory, null).inputStream()).sameAs(stub);
  }

  private InputStreamFactory mockFactoryForInput(final InputStream stub) throws TIoException {
    Mockery context = new Mockery();
    final InputStreamFactory factory = context.mock(InputStreamFactory.class);
    context.checking(new Expectations() {
      {
        oneOf(factory).inputStream();
        will(returnValue(stub));
      }
    });
    return factory;
  }

  public void testCreateReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final InputStreamFactory InputStreamFactory = mockFactoryForInput(stub);
    Reader reader = new InputFactory(InputStreamFactory, null).reader();
    reader.close();
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateReaderWithEncoding() throws Exception {
    String value = "\u00c7\u00c9";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes("utf-16"));
    final InputStreamFactory InputStreamFactory = mockFactoryForInput(inputStream);
    Reader reader = new InputFactory(InputStreamFactory, null).reader("utf-16");
    char[] ch = new char[2];
    ensure.that(reader.read(ch, 0, 2)).eq(2);
    reader.close();
    ensure.that(new String(ch)).eq(value);
  }

  public void testCreateBufferredReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final InputStreamFactory InputStreamFactory = mockFactoryForInput(stub);
    InputFactory factory = new InputFactory(InputStreamFactory, null);
    factory.bufferedReader().close();
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateLineBufferedReader() throws Exception {
    final InputStreamStub stub = new InputStreamStub();
    final InputStreamFactory InputStreamFactory = mockFactoryForInput(stub);
    InputFactory factory = new InputFactory(InputStreamFactory, null);
    factory.lineNumberReader().close();
    ensure.that(stub.isClosed()).eq(true);
  }

  public void testCreateLineNumberReader() throws Exception {
    InputStreamStub stub = new InputStreamStub();
    InputStreamFactory factory = mockFactoryForInput(stub);
    LineNumberReader reader = new InputFactory(factory, null).lineNumberReader();
    reader.close();
    ensure.that(stub.isClosed()).eq(true);
  }

}
