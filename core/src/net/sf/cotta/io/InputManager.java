package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * InputManager for input resource management.  This class is to be used
 * through {@link net.sf.cotta.io.Input}
 */
public class InputManager extends ResourceManager<InputProcessor> {
  private InputFactory inputFactory;

  protected InputManager(InputStreamFactory streamFactory, String defaultEncoding) {
    super(new ArrayList<Closeable>());
    inputFactory = new InputFactory(streamFactory, defaultEncoding);
  }

  public InputStream inputStream() throws TIoException {
    InputStream inputStream = inputFactory.inputStream();
    registerResource(inputStream);
    return inputStream;
  }

  public Reader reader() throws TIoException {
    Reader reader = inputFactory.reader();
    registerResource(reader);
    return reader;
  }

  public Reader reader(String encoding) throws TIoException {
    Reader reader = inputFactory.reader(encoding);
    registerResource(reader);
    return reader;
  }

  public BufferedReader bufferedReader() throws TIoException {
    BufferedReader reader = inputFactory.bufferedReader();
    registerResource(reader);
    return reader;
  }

  public LineNumberReader lineNumberReader() throws TIoException {
    LineNumberReader reader = inputFactory.lineNumberReader();
    registerResource(reader);
    return reader;
  }

  public FileChannel channel() throws TIoException {
    FileChannel channel = inputFactory.inputChannel();
    registerResource(channel);
    return channel;
  }

  protected void process(final InputProcessor processor) throws IOException {
    processor.process(this);
  }

  protected TPath path() {
    return inputFactory.path();
  }

  /**
   * Clean up the mapped byte buffer so that the mapped file can be accessed.  This is
   * a work around so use it at your own risk.  See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">
   * orinigar bug</a> for its source and context.
   *
   * @param buffer the buffer to clean up
   */
  @SuppressWarnings("unchecked")
  public void clean(final MappedByteBuffer buffer) {
    AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {
        try {
          Method getCleanerMethod = buffer.getClass
              ().getMethod("cleaner");
          getCleanerMethod.setAccessible(true);
          sun.misc.Cleaner cleaner =
              (sun.misc.Cleaner) getCleanerMethod.invoke(buffer);
          cleaner.clean();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }

  /**
   * A static factory to create an Input instance for processing the input stream with system default encoding
   *
   * @param stream the input stream to process
   * @return The Input instance
   * @deprecated Moved to Input
   */
  @Deprecated
  public static Input with(final InputStream stream) {
    return Input.with(stream);
  }

  /**
   * A static factory to create an Input instance for processing the input stream with the given encoding when creating readers
   *
   * @param stream   the input stream to process
   * @param encoding encoding used when creating readers
   * @return The Input instance
   * @deprecated Moved to Input
   */
  @Deprecated
  public static Input with(final InputStream stream, String encoding) {
    return Input.with(stream, encoding);
  }

  /**
   * A static factory to create an Input instance for processing the input stream
   * to be created by the InputStreamFactory with system default encoding
   *
   * @param streamFactory input stream factory
   * @return the Input instance
   * @deprecated Moved to Input
   */
  @Deprecated
  public static Input with(InputStreamFactory streamFactory) {
    return Input.with(streamFactory);
  }

  /**
   * A static factory to create an Input instance for processing the input stream
   * to be created by the InputStreamFactory with provided encoding
   *
   * @param streamFactory input stream factory
   * @param encoding      encoding used to create readers
   * @return the Input instance
   * @deprecated Moved to Input
   */
  @Deprecated
  public static Input with(InputStreamFactory streamFactory, String encoding) {
    return Input.with(streamFactory, encoding);
  }

}
