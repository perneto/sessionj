import java.io.*;
import java.net.*;

import java.nio.*;

import java.util.Random;

public class Client {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;

  public void client(int port, String str) {

    try {
      Socket clientSocket = new Socket("", port);
      DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
      DataInputStream in = new DataInputStream(clientSocket.getInputStream());
      System.out.print(str);
      
      out.writeBytes(str); 

      byte[] x = new byte[128];
      in.read(x);
      
      System.out.println("->" + x + "<-");
    }
    catch (IOException e) {}
  }

    public static void main(String[] args) throws IOException {
      Client c = new Client();
      String str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send@");
      c.start = System.nanoTime();
      c.client(1234, str);
      c.end = System.nanoTime();
    }
}

