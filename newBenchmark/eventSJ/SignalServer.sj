
import java.io.*;
import java.net.*;

public class SignalServer implements Runnable {

  //public static final int QUIT = 0x00000100;

  private int port;

  public SignalServer(int port) {
    this.port = port;
  }

  public void run() {
    ObjectInputStream in;

    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true) {
        Socket clientSocket = serverSocket.accept();

        in = new ObjectInputStream(clientSocket.getInputStream());
        int x = in.readInt();

        if ((x & MyObject.BEGIN_TIMING) != 0)
          Server.sendTiming();
        if ((x & MyObject.KILL_LOAD) != 0) {
          Server.sendKill();

          break;
        }
      }
    }
    catch(Exception e){e.printStackTrace();}
  }

  public static void main(String args[]) {
    new SignalServer(2020).run();
  }

}
