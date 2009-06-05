package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.BufferedReader;
import java.io.IOException;

public class Input {
  private InputManager manager;

  public Input(InputManager manager) {
    this.manager = manager;
  }

  /**
   * Read the file with an input processor
   *
   * @param processor processor for the input
   * @throws TIoException error in reading the file
   */
  public void read(InputProcessor processor) throws TIoException {
    manager.open(processor);
  }

  /**
   * Read the file with a line processor
   *
   * @param lineProcessor line processor for the lines
   * @throws TIoException error in reading the file
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
}
