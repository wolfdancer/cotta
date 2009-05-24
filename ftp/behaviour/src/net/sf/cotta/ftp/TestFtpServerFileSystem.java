package net.sf.cotta.ftp;

import com.coldcore.coloradoftp.filesystem.FailedActionException;
import com.coldcore.coloradoftp.filesystem.FailedActionReason;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import net.sf.cotta.*;
import net.sf.cotta.io.OutputMode;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestFtpServerFileSystem implements FileSystem {

  public final static String FILE_SEPARATOR = "/";
  private TDirectory workingDirectory;
  private TFileFactory fileFactory;

  public String getCurrentDirectory(Session userSession) throws FailedActionException {
    return workingDirectory.path();
  }

  public String toAbsolute(String pathString, Session userSession) throws FailedActionException {
    throw new FailedActionException(FailedActionReason.NOT_IMPLEMENTED);
  }

  public String getParent(String pathString, Session userSession) throws FailedActionException {
    TPath parent = TPath.parse(pathString).parent();
    if (parent == null) {
      return pathString;
    }
    return parent.toPathString();
  }

  public Set<ListingFile> listDirectory(String pathString, Session userSession) throws FailedActionException {
    try {
      return internalListDirectory(pathToDirectory(pathString));
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  private Set<ListingFile> internalListDirectory(TDirectory directory) throws TIoException {
    HashSet<ListingFile> listingFiles = new HashSet<ListingFile>();
    List<TFile> files = directory.listFiles();
    for (TFile file : files) {
      listingFiles.add(new FtpFile(file));
    }
    List<TDirectory> dirs = directory.listDirs();
    for (TDirectory dir : dirs) {
      listingFiles.add(new FtpDirectory(dir));
    }
    return listingFiles;
  }

  public ListingFile getPath(String pathString, Session userSession) throws FailedActionException {
    try {
      return pathToEntry(pathString);
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public String changeDirectory(String pathSstring, Session userSession) throws FailedActionException {
    workingDirectory = pathToDirectory(pathSstring);
    return "";
  }

  public void deletePath(String pathString, Session userSession) throws FailedActionException {
    try {
      pathToEntry(pathString).delete();
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public String createDirectory(String dir, Session userSession) throws FailedActionException {
    try {
      pathToDirectory(dir).ensureExists();
      return "";
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public String renamePath(String from, String to, Session userSession) throws FailedActionException {
    try {
      TestFtpServerFileSystem.FtpEntry fromEntry = pathToEntry(from);
      if (fromEntry.isDirectory()) {
        fromEntry.moveTo(pathToDirectory(to));
      } else {
        fromEntry.moveTo(pathToFile(to));
      }
      return "";
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public ReadableByteChannel readFile(String filename, long position, Session userSession) throws FailedActionException {
    try {
      return Channels.newChannel(pathToFile(filename).io().inputStream());
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public WritableByteChannel saveFile(String filename, boolean append, Session userSession) throws FailedActionException {
    try {
      return Channels.newChannel(pathToFile(filename).io().outputStream(append ? OutputMode.APPEND : OutputMode.OVERWRITE));
    } catch (TIoException e) {
      throw reportError(e);
    }
  }

  public String getFileSeparator() {
    return FILE_SEPARATOR;
  }

  private TDirectory pathToDirectory(String pathString) {
    return pathToDirectory(TPath.parse(pathString));
  }

  private TDirectory pathToDirectory(TPath path) {
    if (path.isRelative()) {
      return workingDirectory.dir(path);
    }
    return fileFactory.dir(path.toPathString());
  }

  private TFile pathToFile(String pathString) {
    return pathToFile(TPath.parse(pathString));
  }

  private TFile pathToFile(TPath path) {
    if (path.isRelative()) {
      return workingDirectory.file(path);
    }
    return fileFactory.file(path.toPathString());
  }

  private FtpEntry pathToEntry(String pathString) throws TIoException {
    TPath path = TPath.parse(pathString);
    TFile file = pathToFile(path);
    if (file.exists()) {
      return new FtpFile(file);
    }
    return new FtpDirectory(pathToDirectory(path));
  }

  public void setFileFactory(TFileFactory fileFactory) {
    workingDirectory = fileFactory.dir(FILE_SEPARATOR);
    this.fileFactory = fileFactory;
  }

  private FailedActionException reportError(TIoException e) {
    e.printStackTrace();
    throw new FailedActionException(FailedActionReason.SYSTEM_ERROR, e.toString());
  }

  private abstract class ReadonlyListingFile implements ListingFile {

    public void setOwner(String owner) {
      throw new UnsupportedOperationException("read only");
    }

    public void setDirectory(boolean b) {
      throw new UnsupportedOperationException("read only");
    }

    public void setPermissions(String permissions) {
      throw new UnsupportedOperationException("read only");
    }

    public void setMlsxFacts(String facts) {
      throw new UnsupportedOperationException("read only");
    }

    public void setSize(long size) {
      throw new UnsupportedOperationException("read only");
    }

    public void setName(String name) {
      throw new UnsupportedOperationException("read only");
    }

    public void setAbsolutePath(String path) {
      throw new UnsupportedOperationException("read only");
    }

    public void setLastModified(Date date) {
      throw new UnsupportedOperationException("read only");
    }
  }

  private abstract class FtpEntry extends ReadonlyListingFile {

    private final TEntry entry;
    private final String mlsxFacts;

    public FtpEntry(TEntry entry, String mlsxFacts) {
      this.entry = entry;
      this.mlsxFacts = mlsxFacts;
    }

    public String getOwner() {
      return "ftp";
    }

    public String getPermissions() {
      return "rwxrwxrwx";
    }

    public String getMlsxFacts() {
      return mlsxFacts;
    }

    public String getName() {
      return entry.name();
    }

    public String getAbsolutePath() {
      return toAbsolute(entry.path(), null);
    }

    public Date getLastModified() {
      return new Date();
    }

    public abstract void delete() throws TIoException;

    public abstract void moveTo(TEntry entry) throws TIoException;
  }

  private class FtpFile extends FtpEntry {

    private final TFile file;

    public FtpFile(TFile file) {
      super(file, "adfrw");
      this.file = file;
    }

    public boolean isDirectory() {
      return false;
    }

    public long getSize() {
      try {
        return file.length();
      } catch (TIoException e) {
        throw new TIoRuntimeException(e);
      }
    }

    public void delete() throws TIoException {
      file.delete();
    }

    public void moveTo(TEntry entry) throws TIoException {
      file.moveTo((TFile) entry);
    }
  }

  private class FtpDirectory extends FtpEntry {
    private final TDirectory dir;

    public FtpDirectory(TDirectory dir) {
      super(dir, "cdeflp");
      this.dir = dir;
    }

    public boolean isDirectory() {
      return true;
    }

    public long getSize() {
      return 0;
    }

    public void delete() throws TIoException {
      dir.delete();
    }

    public void moveTo(TEntry entry) throws TIoException {
      dir.moveTo((TDirectory) entry);
    }
  }
}
