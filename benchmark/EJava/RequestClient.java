import java.io.*;
import java.net.*;

import java.nio.*;

import java.util.Random;

public class RequestClient implements Client {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;
  private String str;
  
  public RequestClient() {
    str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send@");
  }

  public String client(String domain, int port) {

    byte[] x = new byte[128];
    try {
      Socket clientSocket = new Socket(domain, port);
      DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
      DataInputStream in = new DataInputStream(clientSocket.getInputStream());
      System.out.print(str);
      
      out.writeBytes(str);

      in.read(x);
      
      System.out.println("->" + x + "<-");
    }
    catch (IOException e) {e.printStackTrace();}
    return "" + x;
  }

    public static void main(String[] args) throws IOException {
      RequestClient c = new RequestClient();
      c.start = System.nanoTime();
      c.client("", 1234);
      c.end = System.nanoTime();
    }
}

