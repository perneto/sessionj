import java.io.*;
import java.net.*;

public class Signal {

  public static void sendSignal(String host, int port, int signal) {
    try {
      Socket clientSocket = new Socket(host, port);
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      out.writeInt(signal);
      out.flush();
      clientSocket.close();
    }
    catch (Exception e){e.printStackTrace();}
  }

  public static void main(String args[]) {
    if (args.length < 3) {
      System.out.println("Usage: java Sigal <host> <port> <signal>");
    }

    if (args[2].equals("Kill")) {
      sendSignal(args[0], Integer.parseInt(args[1]), MyObject.KILL_LOAD);
    }
    if (args[2].equals("Time")) {
      sendSignal(args[0], Integer.parseInt(args[1]), MyObject.BEGIN_TIMING);
    }
  }

}
