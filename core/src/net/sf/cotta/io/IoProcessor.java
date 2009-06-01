package net.sf.cotta.io;

import java.io.IOException;

/**
 * I/O Process used by TFile as call back
 *
 * @see net.sf.cotta.io.InputProcessor
 * @see net.sf.cotta.io.OutputProcessor
 * @deprecated use InputProcessor or OutputProcessor
 */
@SuppressWarnings({"deprecation"})
public interface IoProcessor {
  /**
   * Process the io
   *
   * @param io I/O manager to be used
   * @throws IOException error during the processing
   */
  @SuppressWarnings({"deprecation"})
  public void process(IoManager io) throws IOException;
}
