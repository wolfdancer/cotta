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
import java.util.List;

public class InputManager extends ResourceManager<InputProcessor> {
  private IoFactory ioFactory;

  public InputManager(StreamFactory streamFactory) {
    this(streamFactory, new ArrayList<Closeable>());
  }

  public InputManager(StreamFactory streamFactory, List<Closeable> resourceList) {
    super(resourceList);
    ioFactory = new IoFactory(streamFactory);
  }

  public InputStream inputStream() throws TIoException {
    InputStream inputStream = ioFactory.inputStream();
    registerResource(inputStream);
    return inputStream;
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

  public FileChannel channel() throws TIoException {
    FileChannel channel = ioFactory.inputChannel();
    registerResource(channel);
    return channel;
  }

  protected void process(final InputProcessor processor) throws IOException {
    processor.process(this);
  }

  protected TPath path() {
    return ioFactory.path();
  }

  /**
   * Clean up the mapped byte buffer so that the mapped file can be accessed.  This is
   * a work around so use it at your own risk.  See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">
   * orinigar bug</a> for its source and context.
   *
   * @param buffer the buffer to clean up
   */
  public void clean(final MappedByteBuffer buffer) {
    AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {
        try {
          Method getCleanerMethod = buffer.getClass
              ().getMethod("cleaner",
              new Class[0]);
          getCleanerMethod.setAccessible(true);
          sun.misc.Cleaner cleaner =
              (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object
                  [0]);
          cleaner.clean();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }

  public static Input with(final InputStream stream) {
    final InputManager manager = new InputManager(new StreamFactory() {
      public InputStream inputStream() throws TIoException {
        return stream;
      }

      public FileChannel inputChannel() throws TIoException {
        throw new UnsupportedOperationException();
      }

      public TPath path() {
        return TPath.parse("/input stream");
      }

      public OutputStream outputStream(OutputMode mode) throws TIoException {
        throw new UnsupportedOperationException();
      }
    });
    return new Input() {
      public void read(InputProcessor processor) throws TIoException {
        manager.open(processor);
      }
    };
  }

}
