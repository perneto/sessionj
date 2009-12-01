import java.nio.channels.*;
import java.io.*;
import java.net.*;
import java.util.*;

import java.nio.*;

public class Server{

  private int throughput = 0;

  private long start, end;


  private //int 
            String parse(String str){
      int numEnd = str.indexOf(' ', 7);
//      String sub = 
return str.substring(7, numEnd);
    //  return Integer.valueOf(sub);
  }


  /*Converts an int to byte array*/
  private static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
  }


  public void server(int port) throws Exception{

    Selector sel = null;
    ServerSocketChannel ss = null;
    SocketChannel sc = null;
    Socket s = null;
    ByteBuffer b = null;
//    Map<SocketChannel, StringBuilder> builders = new HashMap<SocketChannel, StringBuilder>();
    Map<SocketChannel, ByteBuffer> byteBuffers = new HashMap<SocketChannel, ByteBuffer>();
//    ByteBuffer b = ByteBuffer.allocate(4096);

    try {
        sel = Selector.open();

        ss = ServerSocketChannel.open();
        ss.configureBlocking(false);
        ss.socket().bind(new InetSocketAddress(port));
    
        ss.register(sel, SelectionKey.OP_ACCEPT);        
    } catch (IOException e) {}

    while (true) {
      sel.select(); //System.out.println("Select again");
      // Get list of selection keys with pending events
      Iterator it = sel.selectedKeys().iterator();
      
      while (it.hasNext()) {
        SelectionKey selKey = (SelectionKey)it.next();
        it.remove();

        if (selKey.isAcceptable()) {
          ss = (ServerSocketChannel)selKey.channel();
          sc = ss.accept();
          sc.configureBlocking(false);
          sc.register(sel, SelectionKey.OP_READ);
	  byteBuffers.put(sc, b = ByteBuffer.allocate(4096));
        }
        else if (selKey.isReadable()) {
          sc = (SocketChannel)selKey.channel();
          b = byteBuffers.get(sc);
	  int numRead = sc.read(b);
          
	  if (numRead == -1) {
	  	selKey.cancel();
		sc.close();
	  }
          
          int i;
          char [] charray = new char[6098];
          
          String st = new String(b.array());
          st.getChars(0, st.length() - 1, charray, 0);
          for (i = 0; charray[i] != '\0'; i++);

          if (i != 0 && charray[i-1] == '@') {
            String x = parse(st);
            ByteBuffer bb = ByteBuffer.allocate(128);
            bb.put(x.getBytes());
            System.out.println(x.getBytes());
            sc.write(bb);
            selKey.cancel();
	    sc.close();
          }
          
        }
      }
    }
  }

  public static void main(String args[]) throws Exception{
    Server s = new Server();
    s.start = System.nanoTime();
    s.server(1234);
    s.end = System.nanoTime();
  }

}
