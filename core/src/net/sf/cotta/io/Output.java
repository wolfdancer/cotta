package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.OutputStream;

public class Output {
  private OutputManager manager;

  public Output(OutputManager manager) {
    this.manager = manager;
  }

  public void write(OutputProcessor processor) throws TIoException {
    manager.open(processor);
  }

  /**
   * Creates the output instance with the given output stream and use system encoding
   *
   * @param stream output stream
   * @return output instance
   */
  public static Output with(final OutputStream stream) {
    return with(stream, null);
  }

  /**
   * Create the output instance with the given output steram and default encoding
   *
   * @param stream          output stream
   * @param defaultEncoding default encoding for creating the writers
   * @return output instance
   */
  public static Output with(final OutputStream stream, String defaultEncoding) {
    return with(new OutputStreamFactory() {
      public TPath path() {
        return TPath.parse("/output stream");
      }

      public OutputStream outputStream() throws TIoException {
        return stream;
      }
    }, defaultEncoding);
  }

  /**
   * Create the output instance with the stream factory and use system encoding
   *
   * @param streamFactory stream factory
   * @return output instance
   */
  public static Output with(OutputStreamFactory streamFactory) {
    return with(streamFactory, null);
  }

  /**
   * Creates the output instance
   *
   * @param streamFactory   output stream factory
   * @param defaultEncoding default encoding for the writers
   * @return output instance
   */
  public static Output with(OutputStreamFactory streamFactory, String defaultEncoding) {
    return new Output(new OutputManager(streamFactory, defaultEncoding));
  }
}
