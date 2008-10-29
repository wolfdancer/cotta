package net.sf.cotta;

import net.sf.cotta.io.*;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * The class that represents the file.  Even though the constructor is public, the usual
 * way is to create TFile through TFile, TDirectory, and TFileFactory
 *
 * @see TFileFactory#file(String)
 * @see TFileFactory#fileFromJavaFile(java.io.File)
 * @see TDirectory#file(String)
 * @see TDirectory#file(TPath)
 */
public class TFile extends TEntry {
  private static final int READ_BUFFER_SIZE = 64;

  /**
   * Create TFile instance backed up by the file system
   * @param fileSystem file system backing the file
   * @param path path for the file
   * @deprecated use the other constructor for default encoding support
   * @see #TFile(TFileFactory, TPath)
   */
  public TFile(FileSystem fileSystem, TPath path) {
    this(new TFileFactory(fileSystem), path);
  }

  /**
   * Create TFile instance backed up by the factory
   * @param factory file factory as the file system
   * @param path path for the file
   */
  public TFile(TFileFactory factory, TPath path) {
    super(factory, path);
  }

  public boolean exists() {
    return filesystem().fileExists(path);
  }

  public TFile create() throws TIoException {
    parent().ensureExists();
    filesystem().createFile(path);
    return this;
  }

  public String extname() {
    String name = name();
    int index = name.lastIndexOf('.');
    return index == -1 ? "" : name.substring(index + 1);
  }

  public String basename() {
    String name = name();
    int index = name.lastIndexOf('.');
    return index == -1 ? name : name.substring(0, index);
  }

  public void delete() throws TIoException {
    filesystem().deleteFile(path);
  }

  private StreamFactory streamFactory() {
    return new StreamFactory() {
      public InputStream inputStream() throws TIoException {
        return TFile.this.inputStream();
      }

      public FileChannel inputChannel() throws TIoException {
        return TFile.this.inputChannel();
      }

      public OutputStream outputStream(OutputMode mode) throws TIoException {
        return TFile.this.outputStream(mode);
      }

      public TPath path() {
        return path;
      }

    };
  }

  private FileChannel inputChannel() throws TIoException {
    return filesystem().createInputChannel(path);
  }

  private OutputStream outputStream(OutputMode mode) throws TIoException {
    parent().ensureExists();
    return filesystem().createOutputStream(path, mode);
  }

  private InputStream inputStream() throws TIoException {
    return filesystem().createInputStream(path);
  }

  public void copyTo(final TFile target) throws TIoException {
    target.write(new OutputProcessor() {
      public void process(OutputManager outputManager) throws IOException {
        copyTo(outputManager.outputStream());
      }
    });
  }

  public void copyTo(final OutputStream outputStream) throws TIoException {
    read(new InputProcessor() {
      public void process(InputManager inputManager) throws IOException {
        copy(inputManager.inputStream(), outputStream);
      }
    });
  }

  private void copy(InputStream is, OutputStream os) throws IOException {
    byte[] buffer = new byte[256];
    int read = is.read(buffer, 0, buffer.length);
    while (read > -1) {
      os.write(buffer, 0, read);
      read = is.read(buffer, 0, buffer.length);
    }
  }

  public void moveTo(TFile destination) throws TIoException {
    if (!exists()) {
      throw new TFileNotFoundException(path);
    }
    if (destination.exists()) {
      throw new TIoException(destination.path, "Destination exists");
    }
    if (filesystem() == destination.filesystem() || filesystem().equals(destination.filesystem())) {
      filesystem().moveFile(this.path, destination.path);
    } else {
      this.copyTo(destination);
      delete();
    }
  }

  public long length() {
    return filesystem().fileLength(path);
  }

  public TFile ensureExists() throws TIoException {
    if (!exists()) {
      create();
    }
    return this;
  }

  public IoFactory io() {
    return new IoFactory(streamFactory());
  }

  public void open(IoProcessor processor) throws TIoException {
    new IoManager(streamFactory()).open(processor);
  }

  public void read(final InputProcessor processor) throws TIoException {
    new InputManager(streamFactory()).open(processor);
  }

  public void append(final OutputProcessor processor) throws TIoException {
    new OutputManager(streamFactory(), OutputMode.APPEND).open(processor);
  }

  public void write(final OutputProcessor processor) throws TIoException {
    new OutputManager(streamFactory(), OutputMode.OVERWRITE).open(processor);
  }

  public void open(final LineProcessor lineProcessor) throws TIoException {
    open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        BufferedReader reader = io.bufferedReader();
        String line = reader.readLine();
        while (line != null) {
          lineProcessor.process(line);
          line = reader.readLine();
        }
      }
    });
  }

  public String load() throws TIoException {
    final StringBuffer buffer = new StringBuffer();
    open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
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
   * Saves the content to the file
   *
   * @param content content to save
   * @return the file instance
   * @throws TIoException if there are any exception thrown during the operation
   */
  public TFile save(final String content) throws TIoException {
    open(new IoProcessor() {
      public void process(IoManager io) throws IOException {
        Writer writer = io.writer(OutputMode.OVERWRITE);
        writer.write(content);
        writer.flush();
      }
    });
    return this;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final TFile file = (TFile) o;

    return filesystem().equals(file.filesystem()) && path.equals(file.path);
  }

  public String toString() {
    return "TFile:" + path();
  }
}