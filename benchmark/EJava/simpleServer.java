import java.nio.channels.*;
import java.io.*;
import java.net.*;
import java.util.Iterator;

public class simpleServer{

  private int throughput = 0;

  private long start, end;

  public void server(int port) throws Exception{

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

    while (true) {
      sel.select();
      // Get list of selection keys with pending events
      Iterator it = sel.selectedKeys().iterator();
      
      while (it.hasNext()) {
        SelectionKey selKey = (SelectionKey)it.next();
        it.remove();
        if (selKey.isAcceptable()) {
          ss = (ServerSocketChannel)selKey.channel();
          s = ss.socket().accept();
          DataOutputStream out = new DataOutputStream(s.getOutputStream());
          out.writeInt(5);
        }
      }
    }
  }

  public static void main(String args[]) throws Exception{
    simpleServer s = new simpleServer();
    s.start = System.nanoTime();
    s.server(1234);
    s.end = System.nanoTime();
  }

}
