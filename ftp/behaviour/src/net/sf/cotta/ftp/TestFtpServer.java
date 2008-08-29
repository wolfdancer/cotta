package net.sf.cotta.ftp;

import com.coldcore.coloradoftp.core.Core;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.impl.SpringFactory;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.memory.InMemoryFileSystem;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFtpServer {

  private ClassPathXmlApplicationContext applicationContext;

  public TestFtpServer() {
    applicationContext = new ClassPathXmlApplicationContext("testFtpServer.xml");
    ObjectFactory.setInternalFactory(new SpringFactory(applicationContext));
  }

  public void cleanFileSystem(TFileFactory fileFactory) {
    TestFtpServerFileSystem cottaFileSystem = (TestFtpServerFileSystem) applicationContext.getBean("filesystem");
    cottaFileSystem.setFileFactory(fileFactory);
  }

  public static void main(String[] args) {
    TestFtpServer ftpServer = new TestFtpServer();
    ftpServer.cleanFileSystem(new TFileFactory(new InMemoryFileSystem()));
    ftpServer.start();
  }

  public void start() {
    getCore().start();
    System.out.println(getCore().getStatus());
  }

  private Core getCore() {
    return (Core) applicationContext.getBean("core");
  }

  public void stop() {
    getCore().stop();
  }
}
