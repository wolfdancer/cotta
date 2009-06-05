package net.sf.cotta.io;

import net.sf.cotta.TIoException;

import java.io.OutputStream;

/**
 * Stream factory used to create streams
 *
 * @deprecated use InputStreamFactory, OutputStreamFactory or TFile itself
 */
@Deprecated
public interface StreamFactory extends InputStreamFactory {
  OutputStream outputStream(OutputMode mode) throws TIoException;
}
