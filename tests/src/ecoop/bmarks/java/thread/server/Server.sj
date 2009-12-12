//$ bin/sessionj -cp tests/classes/ ecoop.bmarks.java.thread.server.Server false 8888 1 

package ecoop.bmarks.java.thread.server;

import java.io.*;
import java.net.*;
import java.util.*;

import ecoop.bmarks.*;

public class Server  
{
	public static final int REC = 1;
	public static final int QUIT = 2;

	public static int signal = MyObject.NO_SIGNAL;
	public static boolean counting = false;
	
	private static boolean debug;
		
  private int port;
  private int numClients; // NB: a TimerClient counts as two clients.
  
  private long count = 0;  

  public Server(boolean debug, int port, int numClients) 
  {
  	Server.debug = debug;
  	
    this.port = port;
    this.numClients = numClients;
  }

  class ServerThread extends Thread
  {
  	private Socket s;
  	
  	public ServerThread(Socket s)
  	{
  		this.s = s;
  	}
  	
  	public void run()
  	{
  		ObjectInputStream ois = null;
  		ObjectOutputStream oos = null;
  		
  		try
  		{
  			ois = new ObjectInputStream(s.getInputStream());
  			oos = new ObjectOutputStream(s.getOutputStream());
  			
  			for (boolean run = true; run; )
  			{  			
	  			int i = ois.readInt();
	  			
	  			if (i == REC)
	  			{
	  				ClientMessage m = (ClientMessage) ois.readObject();
	          
	          debugPrintln("[Server] Received: " + m);
	          
	          oos.writeObject(new MyObject(signal, m.getSize()));
	          
	          if (counting) 
	          {
	            count++;
	            
	            debugPrintln("[Server] Current count:" + count);		            
	          }				
	  			}
	  			else //if (i == QUIT)
	  			{
	  				numClients--;
	  				
	  				debugPrintln("[Server] Clients remaning: " + numClients);
	  				
	  				run = false;
	  			}
  			}
  		}
  		catch(Exception x)
  		{
  			throw new RuntimeException(x);
  		}
  		finally
  		{
  			try
  			{
	  			if (oos != null)
	  			{
	  				oos.flush();
	  				oos.close();
	  			}
	  			
	  			if (ois != null)
	  			{
	  				ois.close();
	  			}
	  			
	  			if (s != null)
	  			{
	  				s.close();
	  			}
  			}
  			catch (Exception x)
  			{
  				// Have to swallow.
  			}
  		}
  	}
  }
  
  public void run() throws Exception
  {		
  	ServerSocket ss = null;
  	
  	Socket s = null;
  	
		try 
		{
			ss = new ServerSocket(port);
			
			List threads = new LinkedList();
			
			for (int i = 0; i < numClients; i++)
			{
				ServerThread st = new ServerThread(ss.accept());
				
				st.start();
				
				threads.add(st);
			}
			
			for(Iterator i = threads.iterator(); i.hasNext(); )
			{
				((ServerThread) i.next()).join();				
			}
		}
		finally
		{
			if (counting)
			{
				System.out.println("[Server] Total count: " + count);
			}
			
			if (ss != null)
			{
				ss.close();
			}
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
  	int port = Integer.parseInt(args[1]);
  	int numClients = Integer.parseInt(args[2]);
  	
    new Server(debug, port, numClients).run();
  }
}
