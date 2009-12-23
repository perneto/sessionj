//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.java.thread.client.LoadClient false localhost 8888 -2 100  

package ecoop.bmarks2.micro.java.thread.client;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;

public class LoadClient extends ecoop.bmarks2.micro.LoadClient
{
  public LoadClient(boolean debug, String host, int port, int cid, int serverMessageSize) 
  {
  	super(debug, host, port, cid, serverMessageSize);
  }
  
	public void run() throws Exception
	{
		Socket s = null;
		
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		
		try
		{
			s = new Socket(getHost(), getPort());
			
			s.setTcpNoDelay(true);
			
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			      
      boolean debug = isDebug();
      int cid = getCid();
      int serverMessageSize = getServerMessageSize();
      
			ServerMessage sm;
      
      int iters = 0;
      
      for (boolean run = true; run; ) 
      {
  			oos.writeInt(Common.REC);
  			oos.flush();
  			
        oos.writeObject(new ClientMessage(cid, Integer.toString(iters++), serverMessageSize));
            
        sm = (ServerMessage) ois.readObject();      
        
        run = !sm.isKill();
            
	      debugPrintln("[LoadClient " + cid + "] Received: " + sm);
	
	      if (debug)
	      {
	      	Thread.sleep(1000);
	      }
      }
      
      oos.writeInt(Common.QUIT);
			oos.flush();
			
      debugPrintln("[LoadClient " + cid + "] Quitting.");
		}
		finally
		{
			Common.closeOutputStream(oos);
			Common.closeInputStream(ois);
			Common.closeSocket(s);
		}
	}
	
  public static void main(String [] args) throws Exception 
  {
  	boolean debug = Boolean.parseBoolean(args[0]);
  	String host = args[1];
    int port = Integer.parseInt(args[2]);
    int cid = Integer.parseInt(args[3]);
    int serverMessageSize = Integer.parseInt(args[4]);

    new LoadClient(debug, host, port, cid, serverMessageSize).run();  
  }
}
