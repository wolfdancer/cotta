package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;

public class OutputFactory {
  private OutputStreamFactory streamFactory;
  private String defaultEncoding;

  public OutputFactory(OutputStreamFactory streamFactory, String defaultEncoding) {
    this.streamFactory = streamFactory;
    this.defaultEncoding = defaultEncoding;
  }

  public OutputStream outputStream() throws TIoException {
    return streamFactory.outputStream();
  }

  public Writer writer() throws TIoException {
    return defaultEncoding == null ?
        new OutputStreamWriter(outputStream()) :
        writer(defaultEncoding);
  }

  public Writer writer(String encoding) throws TIoException {
    try {
      return new OutputStreamWriter(outputStream(), encoding);
    } catch (UnsupportedEncodingException e) {
      throw new TIoException(streamFactory.path(), "Encoding not supported:" + encoding, e);
    }
  }

  public BufferedWriter bufferedWriter() throws TIoException {
    return new BufferedWriter(writer());
  }

  public PrintWriter printWriter() throws TIoException {
    return new PrintWriter(outputStream());
  }

  public TPath path() {
    return streamFactory.path();
  }
}
