package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.*;

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
}
