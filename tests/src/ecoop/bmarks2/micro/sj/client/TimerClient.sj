//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.sj.client.TimerClient false localhost 8888 -1 10 2 1
//$ bin/sessionj -Dsessionj.transports.session=a -cp tests/classes/ ecoop.bmarks2.micro.sj.client.TimerClient false localhost 8888 -1 10 2 1

package ecoop.bmarks2.micro.sj.client;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

import ecoop.bmarks2.micro.*;

public class TimerClient extends ecoop.bmarks2.micro.TimerClient 
{
	//protocol pClient ^(ecoop.bmarks2.micro.sj.thread.server.Server.pServer)
	protocol pClient cbegin.rec X [!{REC: !<ClientMessage>.?(ServerMessage).#X, QUIT: }]

  public TimerClient(boolean debug, String host, int port, int cid, int serverMessageSize, int sessionLength, int repeats) 
  {
  	super(debug, host, port, cid, serverMessageSize, sessionLength, repeats);
  }

  public void run(boolean timer) throws Exception
  {
  	//SJSessionParameters params = SJTransportUtils.createSJSessionParameters("s", "sa");
  	
	  final noalias SJService c = SJService.create(pClient, getHost(), getPort());
		
	  final noalias SJSocket s;
	  	  
	  try (s) 
	  {
	    //s = serv.request(params);
	  	s = c.request();
	
	  	boolean debug = isDebug();
			int cid = getCid();
			int serverMessageSize = getServerMessageSize();
	    int sessionLength = getSessionLength();
	  	
	    long start = System.nanoTime();
	    
	    ServerMessage sm;
	     
	    int iters = 0;
	    
	    s.recursion(X) 
	    {
	      if (iters < sessionLength) 
	      {
	        s.outbranch(REC) 
	        {
	        	s.send(new ClientMessage(cid, Integer.toString(iters), serverMessageSize));
	          
	          sm = (ServerMessage) s.receive();            
	          
	          debugPrintln("[TimerClient " + cid + "] Received: " + sm);
	
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
	          debugPrintln("[TimerClient " + cid + "] Quitting.");
	        }
	      }
	      
	      iters++;
	    }
	    	    
	    long finish = System.nanoTime();
	    
	    if (timer)
	    {
	    	System.out.println("[TimerClient] Session duration: " + (finish - start) + " nanos");
	    }
	  }
	  finally
	  {

	  }
	}
  
  public static void main(String [] args) throws Exception
  {
  	boolean debug = Boolean.parseBoolean(args[0].toLowerCase());
  	String host = args[1];
    int port = Integer.parseInt(args[2]);
    int cid = Integer.parseInt(args[3]);
    int serverMessageSize = Integer.parseInt(args[4]);
    int sessionLength = Integer.parseInt(args[5]);
    int repeats = Integer.parseInt(args[6]);

    new TimerClient(debug, host, port, cid, serverMessageSize, sessionLength, repeats).run();
  }
}
