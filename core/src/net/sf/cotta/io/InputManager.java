package net.sf.cotta.io;

import net.sf.cotta.TIoException;
import net.sf.cotta.TPath;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InputManager {
  private IoManager ioManager;

  public InputManager(StreamFactory streamFactory) {
    ioManager = new IoManager(streamFactory);
  }

  public InputStream inputStream() throws TIoException {
    return ioManager.inputStream();
  }

  public Reader reader() throws TIoException {
    return ioManager.reader();
  }

  public Reader reader(String encoding) throws TIoException {
    return ioManager.reader(encoding);
  }

  public BufferedReader bufferedReader() throws TIoException {
    return ioManager.bufferedReader();
  }

  public LineNumberReader lineNumberReader() throws TIoException {
    return ioManager.lineNumberReader();
  }

  public FileChannel channel() throws TIoException {
    return ioManager.inputChannel();
  }

  public void registerResource(InputStream is) {
    ioManager.registerResource(is);
  }

  public void registerResource(Reader reader) {
    ioManager.registerResource(reader);
  }

  public void registerResource(Closeable Closeable) {
    ioManager.registerResource(Closeable);
  }

  public void open(final InputProcessor processor) throws TIoException {
    ioManager.open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        processor.process(InputManager.this);
      }
    });
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
