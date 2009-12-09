import java.util.concurrent.*;

//sessionj -cp . -Dsessionj.transports.session=a ServerRunner // RAY: no need to specify transports anymore.
//$ bin/sessionj -cp tests/classes/ ServerRunner 2000 1

public class ServerRunner {

  private static ExecutorService exec = null;

  public static void main(String args[]) {
    if(args.length < 2) {
      System.out.println("Usage: sessionj ServerRunner <port> <clientNum>");
      return;
    }

    final int port = Integer.parseInt(args[0]);
//    int numClients = Integer.parseInt(args[1]);
    exec = Executors.newFixedThreadPool(2);

    exec.execute(new Server(port, Integer.parseInt(args[1])));
    exec.execute(new SignalServer(port + 1));
    
    /*final int numClients = Integer.parseInt(args[1]);
    
    new Thread(new Server(port, numClients)).start();*/
  }
}
