import java.io.*;
import java.net.*;

import java.util.Random;
import java.util.concurrent.*;

public class ObjectClient extends Client {

  private static Random generator = new Random(System.currentTimeMillis());
  private String requestString;

  public ObjectClient(int port, String domain, int repetitions, int threadNum) {
    super(port, domain, repetitions, threadNum);
    requestString = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
  }

  public void run() {
    int i;
    Object x = null;
    for(i = 0; i < repetitions; i++) {
      times[i] = System.nanoTime();
      Socket clientSocket = null;      
      try {
        clientSocket = new Socket(domain, port);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        out.println("Object");
        out.println(this.requestString);

        try {
          x = in.readObject();
        } catch (Exception e) {}

        clientSocket.close();
      }
      catch (IOException e) {}	
      times[i] = System.nanoTime() - times[i];
      System.out.println(threadNum + ", " + i + ": " + (String) x + "->" + times[i]);
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
      exec.execute(new ObjectClient(portNum, args[1], 10, i));
    }
    exec.shutdown();
  }

}

