package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.util.ArrayList;

/**
 * Output resource management class to be used through {@link net.sf.cotta.io.Output}
 */
public class OutputManager extends ResourceManager<OutputProcessor> {
  private OutputFactory output;

  protected OutputManager(OutputStreamFactory streamFactory, String defaultEncoding) {
    super(new ArrayList<Closeable>());
    this.output = new OutputFactory(streamFactory, defaultEncoding);
  }

  public OutputStream outputStream() throws TIoException {
    OutputStream outputStream = output.outputStream();
    registerResource(outputStream);
    return outputStream;
  }

  public Writer writer() throws TIoException {
    Writer writer = output.writer();
    registerResource(writer);
    return writer;
  }

  public Writer writer(String encoding) throws TIoException {
    Writer writer = output.writer(encoding);
    registerResource(writer);
    return writer;
  }

  public BufferedWriter bufferedWriter() throws TIoException {
    BufferedWriter bufferedWriter = output.bufferedWriter();
    registerResource(bufferedWriter);
    return bufferedWriter;
  }

  public PrintWriter printWriter() throws TIoException {
    PrintWriter writer = output.printWriter();
    registerResource(writer);
    return writer;
  }

  protected void process(OutputProcessor processor) throws IOException {
    processor.process(this);
  }

  protected TPath path() {
    return output.path();
  }

  /**
   * Creates the output instance with the given output stream and use system encoding
   *
   * @param stream output stream
   * @return output instance
   * @deprecated moved to Output
   */
  @Deprecated
  public static Output with(final OutputStream stream) {
    return Output.with(stream);
  }

  /**
   * Create the output instance with the given output steram and default encoding
   *
   * @param stream          output stream
   * @param defaultEncoding default encoding for creating the writers
   * @return output instance
   * @deprecated moved to Output
   */
  @Deprecated
  public static Output with(final OutputStream stream, String defaultEncoding) {
    return Output.with(stream, defaultEncoding);
  }

  /**
   * Create the output instance with the stream factory and use system encoding
   *
   * @param streamFactory stream factory
   * @return output instance
   * @deprecated moved to Output
   */
  @Deprecated
  public static Output with(OutputStreamFactory streamFactory) {
    return Output.with(streamFactory);
  }

  /**
   * Creates the output instance
   *
   * @param streamFactory   output stream factory
   * @param defaultEncoding default encoding for the writers
   * @return output instance
   * @deprecated
   */
  @Deprecated
  public static Output with(OutputStreamFactory streamFactory, String defaultEncoding) {
    return Output.with(streamFactory, defaultEncoding);
  }
}
