import java.util.concurrent.*;

//sessionj -cp . -Dsessionj.transports.session=a ServerRunner

public class ClientRunner implements Runnable {
  private static int port;
  private static String host;

  private static ExecutorService exec = null;
  
  private boolean loadClient;
  private static int iterations;

  private int clientNum;

  private static long [][]times;

  private ClientRunner(int clientNum, boolean b) {
    this.clientNum = clientNum;
    loadClient = b;
  }

  private Client nextClient() {
    if (loadClient)
      return new Client();
    else
      return new Client(iterations);
  }

  public void run() {
    Client c = nextClient();
    c.client(host, port, times, clientNum);
    if (c.getTiming())
      System.out.println("Client Number: " + clientNum + ". Time: " + times[clientNum][0] + ".");
  }

  public static void main(String [] args) {
    int i;

    if (args.length < 5) {
      System.out.println("Usage: sessionj ClientRunner <host> <port> <Load Clients> <Timed Clients> <session length>");
      return;
    }

    host = args[0];
    port = Integer.parseInt(args[1]);

    int loadClients = Integer.parseInt(args[2]);
    int timedClients = Integer.parseInt(args[3]);
    iterations = Integer.parseInt(args[4]);

    /*exec = Executors.newFixedThreadPool(loadClients + timedClients);

    times = new long[loadClients + timedClients][iterations];

    for(i = 0; i < loadClients; i++)
      exec.execute(new ClientRunner(i, true));

    for(; i < loadClients + timedClients; i++)
      exec.execute(new ClientRunner(i, false));*/      

		foo(loadClients);
		foo(timedClients);   
  }
  
  private static void foo(int num) 
  {
		for (int i = 0; i < num; i++)
		{
			new Thread() {
				public void run()
				{
					new Client();
				}
			}.start();
		}
  }
}
