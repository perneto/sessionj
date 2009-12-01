import java.io.*;
import java.net.*;

import java.util.Random;

public class objectClient {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;

  public void client(int port, String str) {

    try {
      Socket clientSocket = new Socket("", port);
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

      out.println("Object");
      out.println(str);
      try {
        Object x = in.readObject();
        System.out.println((String) x);
      } catch (Exception e) {}
    }
    catch (IOException e) {}
  }

    public static void main(String[] args) throws IOException {
      objectClient c = new objectClient();
      String str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
      c.start = System.nanoTime();
      c.client(1234, str);
      c.end = System.nanoTime();
    }
}

