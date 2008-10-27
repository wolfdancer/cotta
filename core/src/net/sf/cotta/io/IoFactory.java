package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.nio.channels.FileChannel;

public class IoFactory {
  private StreamFactory streamFactory;

  public IoFactory(StreamFactory streamFactory) {
    this.streamFactory = streamFactory;
  }

  public InputStream inputStream() throws TIoException {
    return streamFactory.inputStream();
  }

  public OutputStream outputStream(OutputMode mode) throws TIoException {
    return streamFactory.outputStream(mode);
  }

  /**
   * Create a Reader using the default encoding
   * @return a Reader with the default encoding
   * @throws TIoException for any IO error
   */
  public Reader reader() throws TIoException {
    return new InputStreamReader(inputStream());
  }
  
  /**
   * Create a Reader using the provided encoding
   * @param encoding
   * @return
   * @throws TIoException
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

  public Writer writer(OutputMode mode) throws TIoException {
    return new OutputStreamWriter(outputStream(mode));
  }

  public Writer writer(OutputMode mode, String encoding) throws TIoException {
	try {
	  return new OutputStreamWriter(outputStream(mode), encoding);
	} catch (UnsupportedEncodingException e) {
	  throw new TIoException(streamFactory.path(), "Encoding not supported:" + encoding, e);
	}
  }
  
  public BufferedWriter bufferedWriter(OutputMode mode) throws TIoException {
    return new BufferedWriter(writer(mode));
  }

  public PrintWriter printWriter(OutputMode mode) throws TIoException {
    return new PrintWriter(outputStream(mode));
  }

  public TPath path() {
    return streamFactory.path();
  }

  public FileChannel inputChannel() throws TIoException {
    return streamFactory.inputChannel();
  }
}
