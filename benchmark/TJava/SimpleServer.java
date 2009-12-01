import java.io.*;
import java.net.*;

import java.util.concurrent.*;


public class SimpleServer {

  long start, end;

  private ExecutorService es;

  public SimpleServer() {
    es = Executors.newCachedThreadPool();
  }

  class socketWorker implements Runnable {
    Socket clientSocket;

    socketWorker(Socket s) {
      clientSocket = s;
    }

    public void run(){
      DataOutputStream out;
      try {
        out = new DataOutputStream(clientSocket.getOutputStream());
        out.writeInt(5);
      }
      catch (Exception e){}
    }
  }

  public void server(int port) {
    ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket(port);
      while(true){
        Socket clientSocket = serverSocket.accept();
        socketWorker w = new socketWorker(clientSocket);
        es.execute(w);
      }
    }
    catch (IOException e) {
      System.out.println("Accept failed: " + port);
      System.exit(-1);
    }
  }

  public static void main(String[] args) throws IOException {
    SimpleServer s = new SimpleServer();
    s.start = System.nanoTime();
    s.server(1234);
    s.end = System.nanoTime();
    System.out.println(s.end - s.start);
  }
}
