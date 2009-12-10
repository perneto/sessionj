//$ bin/sessionj -cp tests/classes/ ecoop.bmarks.sj.client.LoadClient false localhost 8888 1234

package ecoop.bmarks.sj.client;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

import ecoop.bmarks.sj.common.*;
import ecoop.bmarks.sj.server.Server;

public class LoadClient 
{
  //protocol pClient cbegin.rec X[!{QUIT: , REC: !<int>.?(MyObject).#X}]
	protocol pClient ^(Server.pServer)

	private static boolean debug;
	
  private String host;
  private int port;
  private int clientNum;

  public LoadClient(boolean debug, String host, int port, int clientNum) 
  {
  	LoadClient.debug = debug;
  	
  	this.host = host;
  	this.port = port;
    this.clientNum = clientNum;
  }

  public void run() throws Exception
  {
  	SJSessionParameters params = SJTransportUtils.createSJSessionParameters("s", "a");  	
  	
  	final noalias SJService serv = SJService.create(pClient, host, port);
  	
    final noalias SJSocket s;
    
    try(s) 
    {
      s = serv.request(params);

      MyObject mo;
      
      boolean run = true; 
      int iters = 0;
      
      s.recursion(X) 
      {
        if (run) 
        {
          s.outbranch(REC) 
          {
            s.send("Client (" + clientNum + ") iteration: " + iters++);
            
            mo = (MyObject) s.receive();            
            run = !mo.killSignal();
            
            debugPrintln("[Client " + clientNum + "] Received: " + mo);

            if (debug)
            {
            	Thread.sleep(1000);
            }
            
            s.recurse(X);
          }
        }
        else 
        {
          s.outbranch(QUIT) 
          {
            debugPrintln("[Client " + clientNum + "] Quitting.");
          }
        }
      }
    }
    finally
    {
    	
    }
  }

  private static final void debugPrintln(String m)
  {
  	if (debug)
  	{
  		System.out.println(m);
  	}
  }
  
  public static void main(String [] args) throws Exception
  {
  	boolean debug = Boolean.parseBoolean(args[0]);
  	String host = args[1];
    int port = Integer.parseInt(args[2]);
    int clientNum = Integer.parseInt(args[3]);

    new LoadClient(debug, host, port, clientNum).run();
  }
}
