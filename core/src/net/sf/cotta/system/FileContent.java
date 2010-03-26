package net.sf.cotta.system;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public interface FileContent {
  OutputStream outputStream();

  InputStream inputStream();

  FileChannel inputChannel();

  long lastModified();
}
