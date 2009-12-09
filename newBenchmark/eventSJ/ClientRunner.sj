import java.util.concurrent.*;

//sessionj -cp . -Dsessionj.transports.session=a ServerRunner // RAY: no need to specify transports anymore.
//$ bin/sessionj -cp tests/classes/ ClientRunner localhost 2000 0 1 1

public class ClientRunner {

  public static void main(String [] args) {

    if (args.length < 5) {
      System.out.println("Usage: sessionj ClientRunner <host> <port> <Load Clients> <Timed Clients> <session length>");
      return;
    }

    Client.host = args[0];
    Client.port = Integer.parseInt(args[1]);

    int loadClients = Integer.parseInt(args[2]);
    int timedClients = Integer.parseInt(args[3]);
    final int iterations = Integer.parseInt(args[4]);
    
    int i;

    for (i = 0; i < loadClients; i++)	{
      final int j = i;			
      new Thread() {
        public void run() {
          new Client(j).client();
        }
      }.start();
    }

    for (; i < loadClients + timedClients; i++)	{
      final int j = i;
      new Thread() {
        public void run() {
          new Client(j, iterations).client();
        }
      }.start();
    }

  }
}
