//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.ClientRunner false localhost 8888 -1 100 1 10 JT

package ecoop.bmarks2.micro;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import ecoop.bmarks2.micro.java.thread.client.*;
//import ecoop.bmarks.java.event.client.*;
//import ecoop.bmarks.sj.client.*;

// Spawns LoadClients.
public class ClientRunner 
{
  public static void main(String [] args) throws Exception
  {
    final boolean debug = Boolean.parseBoolean(args[0].toLowerCase());
    final String host = args[1];
    final int serverPort = Integer.parseInt(args[2]);
    final int scriptPort = Integer.parseInt(args[3]);
    
    int delay = Integer.parseInt(args[4]);
    final int numClients = Integer.parseInt(args[5]);    
    final int serverMessageSize = Integer.parseInt(args[6]);
    
  	final String flag = args[7];
  	
  	if (!(flag.equals(ServerRunner.JAVA_THREAD) || flag.equals(ServerRunner.JAVA_EVENT) || flag.equals(ServerRunner.SJ_THREAD) || flag.equals(ServerRunner.SJ_EVENT)))
		{
  		System.out.println("[ClientRunner] Bad server flag: " + flag);
  		
  		return;
		}   
    
  	final boolean[] ack = new boolean[1];
  	
    for (int i = 0; i < numClients; i++)	
    {
      final int cid = i;
      
      new Thread() 
      {
        public void run() 
        {
        	try
        	{
        		boolean[] foo = null;
        		
        		if (cid == numClients - 1)
        		{
        			foo = ack;
        		}
        		
        		if (flag.equals(ServerRunner.JAVA_THREAD))
        		{
        			new ecoop.bmarks2.micro.java.thread.client.LoadClient(debug, host, serverPort, cid, serverMessageSize, foo).run();
        		}
        		/*else if (flag.equals(ServerRunner.JAVA_EVENT))
        		{
        			new ecoop.bmarks.java.event.client.LoadClient(debug, host, serverPort, cid, serverMessageSize).run();
        		}
        		else if (flag.equals(ServerRunner.SJ_THREAD) || flag.equals(ServerRunner.SJ_EVENT))
        		{
        			new ecoop.bmarks.sj.client.LoadClient(debug, host, serverPort, cid, serverMessageSize).run();
        		}*/
        		else
        		{
        			System.out.println("[ClientRunner] Unrecognised flag: " + flag);
        			System.exit(0);
        		}
        	}
        	catch (Exception x)
        	{
        		throw new RuntimeException(x);
        	}
        }
      }.start();
      
      try
      {
      	Thread.sleep(delay);
      }
      finally
      {
      	
      }
    }
    
    // Here, threads have been created (and started?) but the LoadClients are not necessarily connected yet. 
    if (scriptPort > 0) 
    {
    	synchronized (ack)
    	{
    		while (!ack[0])
    		{
    			ack.wait();
    		}
    	}
    	
    	System.out.println("[ClientRunner] ack received.");
    	
	    Socket s = null;
	    //DataOutputStream dos = null;
	    
	    try
	    {
	    	String script;
	    	
	    	if (host.equals("localhost")) // FIXME: stupid hack for python server sockets.
	    	{
	    		script = host;
	    	}
	    	else
	    	{
	    		script = InetAddress.getLocalHost().getHostName();
	    	}
	    	
	    	s = new Socket(script, scriptPort);
	    	
	    	//dos = new DataOutputStream(s.getOutputStream());
	    	
	    	//dos.writeInt(3);
	    	//dos.flush();
	    }
	    finally
	    {
				//Common.closeOutputStream(dos);
				Common.closeSocket(s);    	
	    }
    }
  }
}
