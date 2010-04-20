package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;

/**
 * Input processing class used to expose the API in the right context
 */
public class Input {

  private static final int READ_BUFFER_SIZE = 64;

  private InputManager manager;

  public Input(InputManager manager) {
    this.manager = manager;
  }

  /**
   * Read the input with an input processor
   *
   * @param processor processor for the input
   * @throws TIoException error in reading the input
   */
  public void read(InputProcessor processor) throws TIoException {
    manager.open(processor);
  }

  /**
   * Read the input with a line processor
   *
   * @param lineProcessor line processor for the lines
   * @throws TIoException error in reading the input
   */
  public void readLines(final LineProcessor lineProcessor) throws TIoException {
    read(new InputProcessor() {
      public void process(InputManager manager) throws IOException {
        BufferedReader reader = manager.bufferedReader();
        String line = reader.readLine();
        while (line != null) {
          lineProcessor.process(line);
          line = reader.readLine();
        }
      }
    });
  }

  /**
   * Load the content of the input into string using system default encoding
   *
   * @return content of the input
   * @throws TIoException error in reading the input
   */
  public String load() throws TIoException {
    final StringBuffer buffer = new StringBuffer();
    read(new InputProcessor() {
      public void process(InputManager io) throws IOException {
        loadContent(buffer, io.reader());
      }
    });
    return buffer.toString();
  }

  private void loadContent(StringBuffer content, Reader reader) throws IOException {
    char[] buffer = new char[READ_BUFFER_SIZE];
    int read = 0;
    while (read != -1) {
      content.append(buffer, 0, read);
      read = reader.read(buffer, 0, buffer.length);
    }
  }

  /**
   * A static factory to create an Input instance for processing the input stream with system default encoding
   *
   * @param stream the input stream to process
   * @return The Input instance
   */
  public static Input with(final InputStream stream) {
    return with(stream, null);
  }

  /**
   * A static factory to create an Input instance for processing the input stream with the given encoding when creating readers
   *
   * @param stream   the input stream to process
   * @param encoding encoding used when creating readers
   * @return The Input instance
   */
  public static Input with(final InputStream stream, String encoding) {
    return with(new InputStreamFactory() {
      public InputStream inputStream() throws TIoException {
        return stream;
      }

      public FileChannel inputChannel() throws TIoException {
        throw new UnsupportedOperationException();
      }

      public TPath path() {
        return TPath.parse("/input stream");
      }

    }, encoding);
  }

  /**
   * A static factory to create an Input instance for processing the input stream
   * to be created by the InputStreamFactory with system default encoding
   *
   * @param streamFactory input stream factory
   * @return the Input instance
   */
  public static Input with(InputStreamFactory streamFactory) {
    return with(streamFactory, null);
  }

  /**
   * A static factory to create an Input instance for processing the input stream
   * to be created by the InputStreamFactory with provided encoding
   *
   * @param streamFactory input stream factory
   * @param encoding      encoding used to create readers
   * @return the Input instance
   */
  public static Input with(InputStreamFactory streamFactory, String encoding) {
    final InputManager manager = new InputManager(streamFactory, encoding);
    return new Input(manager);
  }
}
