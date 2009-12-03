import java.nio.channels.*;
import java.io.*;
import java.net.*;
import java.util.Iterator;

public class SimpleServer implements Server {

  private int throughput = 0;

  private long start, end;

  public void server(int port, int numClients) {

    Selector sel = null;
    ServerSocketChannel ss = null;
    Socket s;

    try {
        sel = Selector.open();

        ss = ServerSocketChannel.open();
        ss.configureBlocking(false);
        ss.socket().bind(new InetSocketAddress(port));
    
        ss.register(sel, SelectionKey.OP_ACCEPT);        
    } catch (IOException e) {}

    while (numClients-- != 0) {
      try {
        sel.select();
        // Get list of selection keys with pending events
        Iterator it = sel.selectedKeys().iterator();
      
        while (it.hasNext()) {
          SelectionKey selKey = (SelectionKey)it.next();
          it.remove();
          if (selKey.isAcceptable()) {
            ss = (ServerSocketChannel)selKey.channel();
            s = ss.socket().accept();
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeInt(5);
          }
        }
      } catch (Exception e) {e.printStackTrace();}
    }
  }

  public static void main(String args[]) throws Exception{
    SimpleServer s = new SimpleServer();
    s.start = System.nanoTime();
    s.server(1234, 1234);
    s.end = System.nanoTime();
  }

}
