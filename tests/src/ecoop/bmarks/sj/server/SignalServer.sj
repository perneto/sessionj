package ecoop.bmarks.sj.server;

import java.io.*;
import java.net.*;

import ecoop.bmarks.sj.common.*;

public class SignalServer 
{
	public static final int SIGNAL_SERVER_PORT_OFFSET = 1000;
	
  private int port;

  public SignalServer(int port) 
  {
    this.port = port;
  }

  public void run() throws Exception
  {
  	ServerSocket ss = null;
  	
  	Socket s  = null; 
    ObjectInputStream is = null;
    
    try 
    {
      ss = new ServerSocket(port + SIGNAL_SERVER_PORT_OFFSET);
      
      //System.out.println("[SignalServer] Listening on: " + (port + SIGNAL_SERVER_PORT_OFFSET));
      
      for (boolean run = true; run; ) 
      {
        s = ss.accept();

        is = new ObjectInputStream(s.getInputStream());
        
        int x = is.readInt();

        //System.out.println("[SignalServer] Read: " + x);
        
        if ((x & MyObject.BEGIN_TIMING) != 0)
        {
          //Server.sendTiming();
        	Server.signal |= MyObject.BEGIN_TIMING;
        }
        
        if ((x & MyObject.BEGIN_COUNTING) != 0)
        {
          //Server.sendCounting();
        	Server.counting = true;
        }
        
        if ((x & MyObject.KILL_LOAD) != 0) 
        {
          //Server.sendKill();
        	Server.signal |= MyObject.KILL_LOAD;
        	
          run = false;
        }
      }
    }
    finally
    {
    	if (ss != null)
    	{
    		ss.close();
    	}
    	
    	if (s != null)
    	{
    		s.close();
    	}
    	
    	if (is != null)
    	{
    		is.close();
    	}
    }
    
    //System.out.println("[SignalServer] Finished.");
  }

  public static void main(String args[]) throws Exception
  {
  	int port = Integer.parseInt(args[0]);
  	
    new SignalServer(port).run();
  }
}
