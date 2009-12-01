import java.io.*;
import java.net.*;

import java.util.Random;
import java.util.concurrent.*;

public class IntClient extends Client {

  private static Random generator = new Random(System.currentTimeMillis());
  private String requestString;

  public IntClient(int port, String domain, int repetitions, int threadNum) {
    super(port, domain, repetitions, threadNum);
    requestString = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
  }

  public void run() {
    int i, x = 0;
    for(i = 0; i < repetitions; i++) {
      times[i] = System.nanoTime();
      Socket clientSocket = null;      
      try {
        clientSocket = new Socket(domain, port);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        out.println("int");
        out.println(this.requestString);
        x = in.readInt();
        clientSocket.close();
      }
      catch (IOException e) {}	
      times[i] = System.nanoTime() - times[i];
      System.out.println(threadNum + ", " + i + ": " + x + "->" + times[i]);
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      System.out.println("Usage: java simpleClient <port> <domain name> <core number>");
      return;
    }

    ExecutorService exec = Executors.newFixedThreadPool(Integer.valueOf(args[2]));

    int numCores = Integer.valueOf(args[2]);
    int portNum = Integer.valueOf(args[0]);

    int i;
    for(i = 0; i < numCores; i++) {
      exec.execute(new IntClient(portNum, args[1], 10, i));
    }
    exec.shutdown();
  }

}

