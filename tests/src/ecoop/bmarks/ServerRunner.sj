//$ bin/sessionj -cp tests/classes/ ecoop.bmarks.ServerRunner false 8888 1 JT

package ecoop.bmarks;

import ecoop.bmarks.java.thread.server.*;
//import ecoop.bmarks.java.event.Server;
import ecoop.bmarks.sj.event.server.*;

// Spawns a pair of Server and SignalClient.
public class ServerRunner 
{
  public static void main(String args[]) 
  {
    final boolean debug = Boolean.parseBoolean(args[0]);
    final int port = Integer.parseInt(args[1]);
    final int numClients = Integer.parseInt(args[2]);
    final String server = args[3];
    
  	if (!(server.equals(SignalClient.JAVA_THREAD) || server.equals(SignalClient.JAVA_EVENT) || server.equals(SignalClient.SJ_THREAD) || server.equals(SignalClient.SJ_EVENT)))
		{
  		System.out.println("[ServerRunner] Bad server flag: " + server);
  		
  		return;
		}
  	
    new Thread()
    {
    	public void run()
    	{
    		try
    		{
    			if (server.equals(SignalClient.JAVA_THREAD))
    			{
    				new ecoop.bmarks.java.thread.server.Server(debug, port, numClients).run();
    			}  
    			else if (server.equals(SignalClient.SJ_EVENT))
    			{
    				new ecoop.bmarks.sj.event.server.Server(debug, port, numClients).run();
    			}
    		}
    		catch (Exception x)
    		{
    			throw new RuntimeException(x);
    		}
    	}
    }.start();
    
    new Thread()
    {
    	public void run()
    	{
    		try
    		{
    	    new SignalServer(port, server).run(); // SignalServer sorts out port offset to avoid collision with Server.
    		}
    		catch (Exception x)
    		{
    			throw new RuntimeException(x);
    		}
    	}
    }.start();
  }
}
