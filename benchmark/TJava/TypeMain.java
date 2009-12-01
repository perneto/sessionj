
import java.util.concurrent.*;

public class TypeMain {


  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: java simpleClient <port> <domain name> <core number>");
      return;
    }

    ExecutorService exec = Executors.newFixedThreadPool(Integer.valueOf(args[2]));

    int numCores = Integer.valueOf(args[2]);
    int portNum = Integer.valueOf(args[0]);

    int i;
    for(i = 0; true;) {
      exec.execute(new IntClient(portNum, args[1], 10, i++));
      if (i == numCores) break;
      exec.execute(new StringClient(portNum, args[1], 10, i++));
      if (i == numCores) break;
      exec.execute(new ObjectClient(portNum, args[1], 10, i++));
      if (i == numCores) break;
    }
/*
    if (numCores % 3 == 1)
      exec.execute(new IntClient(portNum, args[1], 10, numCores - 1));
    else if (numCores % 3 == 2) {
      exec.execute(new IntClient(portNum, args[1], 10, numCores - 2));
      exec.execute(new StringClient(portNum, args[1], 10, numCores - 1));
    }
*/
    exec.shutdown();  
  }

}
