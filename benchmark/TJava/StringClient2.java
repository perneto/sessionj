import java.io.*;
import java.net.*;

import java.util.Random;

public class stringClient {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;

  public void client(int port, String str) {

    try {
      Socket clientSocket = new Socket("", port);
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
   //   BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
    
      out.println("String");
      out.println(str);
      String x = in.readUTF();
      System.out.println(x);
    }
    catch (IOException e) {}
  }

    public static void main(String[] args) throws IOException {
      stringClient c = new stringClient();
      String str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
      c.start = System.nanoTime();
      c.client(1234, str);
      c.end = System.nanoTime();
    }
}

