package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class OutputManager extends ResourceManager<OutputProcessor> {
  private OutputMode mode;
  private IoFactory ioFactory;

  public OutputManager(StreamFactory streamFactory, OutputMode mode) {
    this(streamFactory, mode, new ArrayList<Closeable>());
  }

  public OutputManager(StreamFactory streamFactory, OutputMode mode, List<Closeable> resourceList) {
    super(resourceList);
    this.ioFactory = new IoFactory(streamFactory);
    this.mode = mode;
  }

  public OutputStream outputStream() throws TIoException {
    OutputStream outputStream = ioFactory.outputStream(mode);
    registerResource(outputStream);
    return outputStream;
  }

  public Writer writer() throws TIoException {
    Writer writer = ioFactory.writer(mode);
    registerResource(writer);
    return writer;
  }

  public Writer writer(String encoding) throws TIoException {
    Writer writer = ioFactory.writer(mode, encoding);
    registerResource(writer);
    return writer;
  }

  public BufferedWriter bufferedWriter() throws TIoException {
    BufferedWriter bufferedWriter = ioFactory.bufferedWriter(mode);
    registerResource(bufferedWriter);
    return bufferedWriter;
  }

  public PrintWriter printWriter() throws TIoException {
    PrintWriter writer = ioFactory.printWriter(mode);
    registerResource(writer);
    return writer;
  }

  protected void process(OutputProcessor processor) throws IOException {
    processor.process(this);
  }

  protected TPath path() {
    return ioFactory.path();
  }

  public static Output with(final OutputStream stream) {
    final OutputManager manager = new OutputManager(new StreamFactory() {
      public InputStream inputStream() throws TIoException {
        throw new UnsupportedOperationException();
      }

      public FileChannel inputChannel() throws TIoException {
        throw new UnsupportedOperationException();
      }

      public TPath path() {
        return TPath.parse("/output stream");
      }

      public OutputStream outputStream(OutputMode mode) throws TIoException {
        return stream;
      }
    }, OutputMode.APPEND);
    return new Output() {
      public void write(OutputProcessor processor) throws TIoException {
        manager.open(processor);
      }
    };
  }
}
