package net.sf.cotta.ftp;

import net.sf.cotta.TPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface FtpClient {

  FtpFile[] list(TPath path) throws IOException;

  OutputStream store(TPath path) throws IOException;

  void delete(TPath path) throws IOException;

  void makeDirectory(TPath path) throws IOException;

  void rename(TPath source, TPath destination) throws IOException;

  InputStream retrieve(TPath path) throws IOException;

  OutputStream append(TPath path) throws IOException;
}
