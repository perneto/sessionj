import java.io.*;
import java.net.*;

import java.util.Random;

public class intClient {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;

  public void client(int port, String str) {

    try {
      Socket clientSocket = new Socket("", port);
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      
      out.println("int");
      out.println(str);
      System.out.println("Ray");
      int x = in.readInt();
      System.out.println("Ray2");
      System.out.println(x);
    }
    catch (IOException e) {}
  }

    public static void main(String[] args) throws IOException {
      intClient c = new intClient();
      String str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
      c.start = System.nanoTime();
      c.client(1234, str);
      c.end = System.nanoTime();
    }
}

