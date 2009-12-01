package sessionj.benchmark;


import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;


public class simpleClient{

  protocol getInt ?(int)
  protocol clientSide cbegin.@(getInt)

  private long start, end;

  public void client(int port) throws Exception {
    final noalias SJService serv = SJService.create(clientSide, "", port);
    int x;
    noalias SJSocket s;
    try (s) {
      s = serv.request();
      x = s.receiveInt();
    } finally {}
  }

  public static void main(String args[]) throws Exception{
    simpleClient c = new simpleClient();
    c.start = System.nanoTime();
    c.client(1234);
    c.end = System.nanoTime();
    
  }

}
