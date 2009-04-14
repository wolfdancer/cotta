package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.nio.channels.FileChannel;

public class OutputManager {
  private IoManager io;
  private OutputMode mode;

  public OutputManager(StreamFactory factory, OutputMode mode) {
    this.io = new IoManager(factory);
    this.mode = mode;
  }

  public OutputStream outputStream() throws TIoException {
    return io.outputStream(mode);
  }

  public Writer writer() throws TIoException {
    return io.writer(mode);
  }

  public Writer writer(String encoding) throws TIoException {
    return io.writer(mode, encoding);
  }

  public BufferedWriter bufferedWriter() throws TIoException {
    return io.bufferedWriter(mode);
  }

  public PrintWriter printWriter() throws TIoException {
    return io.printWriter(mode);
  }

  public void registerResource(OutputStream os) {
    io.registerResource(os);
  }

  public void registerResource(Writer writer) {
    io.registerResource(writer);
  }

  public void registerResource(Closeable resource) {
    io.registerResource(resource);
  }

  public void open(final OutputProcessor processor) throws TIoException {
    io.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        processor.process(OutputManager.this);
      }
    });
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
