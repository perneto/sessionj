import java.util.concurrent.*;

abstract class Client implements Runnable{

  protected int port;
  protected String domain;
  protected int repetitions;
  protected long[] times;

  protected int threadNum;

  public Client(int port, String domain, int repetitions, int threadNum) {
    this.port = port;
    this.domain = new String(domain);
    this.repetitions = repetitions;
    this.times = new long[repetitions];
    this.threadNum = threadNum;
  }

  abstract public void run();

}
