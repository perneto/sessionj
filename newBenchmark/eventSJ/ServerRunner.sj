import java.util.concurrent.*;

//sessionj -cp . -Dsessionj.transports.session=a ServerRunner

public class ServerRunner {

  private static ExecutorService exec = null;

  public static void main(String args[]) {
    if(args.length < 2) {
      System.out.println("Usage: sessionj ServerRunner <port> <clientNum>");
      return;
    }

    int port = Integer.parseInt(args[0]);
//    int numClients = Integer.parseInt(args[1]);
    exec = Executors.newFixedThreadPool(2);

    exec.execute(new Server(port, Integer.parseInt(args[1])));
    exec.execute(new SignalServer(port + 1));
  }
}
