package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class IoManager {
  private static final int INITIAL_CAPACITY = 3;

  private IoFactory ioFactory;
  private List resourceList = new ArrayList(INITIAL_CAPACITY);

  public IoManager(StreamFactory streamFactory) {
    this.ioFactory = new IoFactory(streamFactory);
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

  public Writer writer(OutputMode append) throws TIoException {
    Writer writer = ioFactory.writer(append);
    registerResource(writer);
    return writer;
  }

  public Reader reader() throws TIoException {
    Reader reader = ioFactory.reader();
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
    for (Iterator iterator = resourceList.iterator(); iterator.hasNext();) {
      IoResource ioResource = (IoResource) iterator.next();
      ioResource.close();
    }
  }

  public void registerResource(final InputStream is) {
    registerResource(new IoResource() {
      public void close() throws IOException {
        is.close();
      }
    });
  }

  public void registerResource(final OutputStream os) {
    registerResource(new IoResource() {
      public void close() throws IOException {
        os.close();
      }
    });
  }

  public void registerResource(final Reader reader) {
    IoResource ioResource = new IoResource() {
      public void close() throws IOException {
        reader.close();
      }
    };
    registerResource(ioResource);
  }

  public void registerResource(final Writer writer) {
    registerResource(new IoResource() {
      public void close() throws IOException {
        writer.close();
      }
    });
  }

  private void registerResource(final FileChannel channel) {
    registerResource(new IoResource() {
      public void close() throws IOException {
        channel.close();
      }
    });
  }

  public void registerResource(IoResource ioResource) {
    resourceList.add(ioResource);
  }

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
