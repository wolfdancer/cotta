package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.nio.channels.FileChannel;

public class InputFactory {
  private InputStreamFactory streamFactory;
  private String defaultEncoding;

  public InputFactory(InputStreamFactory inputStreamFactory, String defaultEncoding) {
    this.streamFactory = inputStreamFactory;
    this.defaultEncoding = defaultEncoding;
  }

  public InputStream inputStream() throws TIoException {
    return streamFactory.inputStream();
  }

  /**
   * Create a Reader using the default encoding
   *
   * @return a Reader with the default encoding
   * @throws net.sf.cotta.TIoException for any IO error
   */
  public Reader reader() throws TIoException {
    return defaultEncoding == null ?
        new InputStreamReader(inputStream()) :
        reader(defaultEncoding);
  }

  /**
   * Create a Reader using the provided encoding
   *
   * @param encoding encoding for the reader
   * @return Reader with specified encoding
   * @throws net.sf.cotta.TIoException if any IOException thrown
   */
  public Reader reader(String encoding) throws TIoException {
    try {
      return new InputStreamReader(inputStream(), encoding);
    } catch (UnsupportedEncodingException e) {
      throw new TIoException(streamFactory.path(), "Encoding not supported:" + encoding, e);
    }
  }

  public BufferedReader bufferedReader() throws TIoException {
    return new BufferedReader(reader());
  }

  public LineNumberReader lineNumberReader() throws TIoException {
    return new LineNumberReader(reader());
  }

  public FileChannel inputChannel() throws TIoException {
    return streamFactory.inputChannel();
  }

  public TPath path() {
    return streamFactory.path();
  }
}
