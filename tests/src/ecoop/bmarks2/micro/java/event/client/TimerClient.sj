//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.java.event.client.TimerClient false localhost 8888 -1 10 2 1

package ecoop.bmarks2.micro.java.event.client;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;
import ecoop.bmarks2.micro.java.event.server.Server;

public class TimerClient extends ecoop.bmarks2.micro.TimerClient 
{
  public TimerClient(boolean debug, String host, int port, int cid, int serverMessageSize, int sessionLength, int repeats) 
  {
  	super(debug, host, port, cid, serverMessageSize, sessionLength, repeats);
  }

  public void run(boolean timer) throws Exception
  {
		Socket s = null;
		
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try
		{
			s = new Socket(getHost(), getPort());
			
			s.setTcpNoDelay(true);
			
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
	
	  	boolean debug = isDebug();
			int cid = getCid();
			int serverMessageSize = getServerMessageSize();
	    int sessionLength = getSessionLength();			
			
	    long start = System.nanoTime();
	    
	    ServerMessage sm;
	     
	    for (int iters = 0; iters < sessionLength; iters++) 
      {
	    	dos.write(Server.serializeInt(Common.REC));
  			dos.flush();
  			
  			byte[] bs = Server.serializeObject(new ClientMessage(cid, Integer.toString(iters), serverMessageSize));
            
  			dos.write(Server.serializeInt(bs.length));
        dos.write(bs);
        dos.flush();
  			
	      bs = new byte[4];
        
        dis.readFully(bs);
        
        bs = new byte[Server.deserializeInt(bs)];
        
        dis.readFully(bs);
        
        sm = (ServerMessage) Server.deserializeObject(bs);      
        
        debugPrintln("[TimerClient " + cid + "] Received: " + sm);
	      
	      if (debug)
	      {
	      	Thread.sleep(1000);
	      }
      }
      
      dos.write(Server.serializeInt(Common.QUIT));
			dos.flush();
			
      debugPrintln("[TimerClient " + cid + "] Quitting.");
	    	    
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
