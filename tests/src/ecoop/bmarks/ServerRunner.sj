//$ bin/sessionj -cp tests/classes/ ecoop.bmarks.ServerRunner false 8888 1 JT

package ecoop.bmarks;

import ecoop.bmarks.java.thread.server.*;
import ecoop.bmarks.java.event.server.*;
import ecoop.bmarks.sj.server.thread.*;
import ecoop.bmarks.sj.server.event.*;

// Spawns a pair of Server and SignalClient.
public class ServerRunner 
{
  public static void main(String args[]) 
  {
    final boolean debug = Boolean.parseBoolean(args[0]);
    final int port = Integer.parseInt(args[1]);
    final int numClients = Integer.parseInt(args[2]); // NB: TimerClients count as two clients.
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
    			else if (server.equals(SignalClient.JAVA_EVENT))
    			{
    				new ecoop.bmarks.java.event.server.Server(debug, port, numClients).run();
    			}
    			else if (server.equals(SignalClient.SJ_THREAD))
    			{
    				new ecoop.bmarks.sj.server.thread.Server(debug, port, numClients).run();
    			}
    			else if (server.equals(SignalClient.SJ_EVENT))
    			{
    				new ecoop.bmarks.sj.server.event.Server(debug, port, numClients).run();
    			}
      		else
      		{
      			System.out.println("[ServerRunner] Unrecognised flag: " + server);
      			System.exit(0);
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
