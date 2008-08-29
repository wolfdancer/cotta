package net.sf.cotta.memory;

import java.nio.channels.FileChannel;

public class AccesssUtil {
  public static FileChannel createInMemoryOutputChannel() {
    return new InMemoryOutputFileChannel(new ByteArrayBuffer());
  }
}
