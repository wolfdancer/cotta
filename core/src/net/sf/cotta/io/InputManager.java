package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.*;
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

  public void registerResource(IoResource ioResource) {
    ioManager.registerResource(ioResource);
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
}
