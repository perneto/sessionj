import java.io.*;
import java.net.*;



public class SimpleClient implements Client {

  long start, end;

  public String client (String domain, int port) {
    int x = 0;
    try {
      Socket clientSocket = new Socket(domain, port);
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      x = in.readInt();
      System.out.println(x);
    }
    catch (IOException e) {e.printStackTrace();}
    return "" + x;

  }

    public static void main(String[] args) throws IOException {
        SimpleClient c = new SimpleClient();
        c.start = System.nanoTime();
        c.client("", 1234);
        c.end = System.nanoTime();
        System.out.println(c.end - c.start);
    }
}

