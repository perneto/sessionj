package sessionj.benchmark;


import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

import java.util.Random;

public class intClient{

  protocol reqRep !<String>.?(int)
  protocol clientSide cbegin.@(reqRep)

  private static Random generator = new Random(System.currentTimeMillis());

  private long start, end;

  public void client(int port, String str) throws Exception {
    final noalias SJService serv = SJService.create(clientSide, "", port);
    int x;
    noalias SJSocket s;
    try (s) {
      s = serv.request();
      s.send(str);
      x = s.receiveInt();
    } finally {}
  }

  public static void main(String args[]) throws Exception{
    intClient c = new intClient();
    String str = new String("Number " + (generator.nextInt() % 1024) + " is beeing send");
    c.start = System.nanoTime();
    c.client(1234, str);
    c.end = System.nanoTime();
    
  }

}
