import java.io.*;
import java.net.*;



public class simpleClient {

  long start, end;

  public void client (int port) {

    try {
      Socket clientSocket = new Socket("", port);
      DataInputStream in = new DataInputStream(clientSocket.getInputStream());
      int x = in.readInt();
      System.out.println(x);
    }
    catch (IOException e) {}


  }

    public static void main(String[] args) throws IOException {
        simpleClient c = new simpleClient();
        c.start = System.nanoTime();
        c.client(1234);
        c.end = System.nanoTime();
        System.out.println(c.end - c.start);
    }
}

