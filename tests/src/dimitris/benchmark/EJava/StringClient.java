import java.nio.channels.*;
import java.io.*;
import java.net.*;

import java.nio.*;
import java.nio.charset.Charset;

import java.util.Random;

public class StringClient implements Client {

  private static Random generator = new Random(System.currentTimeMillis());
  private long start, end;
  private String requestString;
  
  private static Charset charset = Charset.forName("UTF-8");

  private static int byteArrayToInt(byte []b) {
    int x = 0;
    x |= b[0]; x <<= 8;
    x |= b[1]; x <<= 8;
    x |= b[2]; x <<= 8;
    x |= b[3];
    return x;
  }

  public StringClient() {
    requestString = new String("Number " + (generator.nextInt() % 1024) + " is beeing send@");
  }

  public String client(String domain, int port) {

    ByteBuffer b = ByteBuffer.allocate(64);
    String x = null;

    try {

      InetSocketAddress socketAddress = new InetSocketAddress(domain, port);
      SocketChannel sc = SocketChannel.open(socketAddress);

      sc.write(charset.encode("s"));
      sc.write(charset.encode(requestString));

      int numRead  = 0;

      b.clear();
      while (numRead != -1) 
        numRead = sc.read(b);

      b.flip();
      x = charset.decode(b).toString();
      sc.close();
    }
    catch (IOException e) {e.printStackTrace();}
    return x;
  }

    public static void main(String[] args) throws IOException {
      new StringClient().client("", 1234);
    }
}

