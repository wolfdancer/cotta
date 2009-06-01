package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * I/O manager that handles creation and management of the I/O resources
 *
 * @deprecated use InputManager or OutputManager
 */
@Deprecated
public class IoManager {
  private static final int INITIAL_CAPACITY = 3;

  private IoFactory ioFactory;
  private List<Closeable> resourceList = new ArrayList<Closeable>(INITIAL_CAPACITY);

  public IoManager(StreamFactory streamFactory) {
    this(streamFactory, null);
  }

  public IoManager(StreamFactory streamFactory, String defaultEncoding) {
    this.ioFactory = new IoFactory(streamFactory, defaultEncoding);
  }

  public InputStream inputStream() throws TIoException {
    InputStream inputStream = ioFactory.inputStream();
    registerResource(inputStream);
    return inputStream;
  }

  public OutputStream outputStream(OutputMode mode) throws TIoException {
    OutputStream outputStream = ioFactory.outputStream(mode);
    registerResource(outputStream);
    return outputStream;
  }

  public Writer writer(OutputMode outputMode) throws TIoException {
    Writer writer = ioFactory.writer(outputMode);
    registerResource(writer);
    return writer;
  }

  public Writer writer(OutputMode outputMode, String encoding) throws TIoException {
    Writer writer = ioFactory.writer(outputMode, encoding);
    registerResource(writer);
    return writer;
  }

  public Reader reader() throws TIoException {
    Reader reader = ioFactory.reader();
    registerResource(reader);
    return reader;
  }

  public Reader reader(String encoding) throws TIoException {
    Reader reader = ioFactory.reader(encoding);
    registerResource(reader);
    return reader;
  }

  public BufferedReader bufferedReader() throws TIoException {
    BufferedReader reader = ioFactory.bufferedReader();
    registerResource(reader);
    return reader;
  }

  public LineNumberReader lineNumberReader() throws TIoException {
    LineNumberReader reader = ioFactory.lineNumberReader();
    registerResource(reader);
    return reader;
  }

  public FileChannel inputChannel() throws TIoException {
    FileChannel channel = ioFactory.inputChannel();
    registerResource(channel);
    return channel;
  }

  public BufferedWriter bufferedWriter(OutputMode mode) throws TIoException {
    BufferedWriter bufferedWriter = ioFactory.bufferedWriter(mode);
    registerResource(bufferedWriter);
    return bufferedWriter;
  }

  public PrintWriter printWriter(OutputMode mode) throws TIoException {
    PrintWriter writer = ioFactory.printWriter(mode);
    registerResource(writer);
    return writer;
  }

  private void close() throws IOException {
    Collections.reverse(resourceList);
    for (Closeable Closeable : resourceList) {
      Closeable.close();
    }
  }

  public void registerResource(Closeable Closeable) {
    resourceList.add(Closeable);
  }

  @SuppressWarnings({"deprecation"})
  public void open(IoProcessor ioProcessor) throws TIoException {
    boolean errorOccurred = true;
    try {
      ioProcessor.process(this);
      errorOccurred = false;
    } catch (TIoException e) {
      throw e;
    } catch (IOException e) {
      throw new TIoException(ioFactory.path(), "IO Error", e);
    } finally {
      safeClose(errorOccurred);
    }
  }

  private void safeClose(boolean errorOccurred) throws TIoException {
    try {
      close();
    } catch (IOException e) {
      if (!errorOccurred) {
        throw new TIoException(ioFactory.path(), "closing resource", e);
      }
    }
  }
}
