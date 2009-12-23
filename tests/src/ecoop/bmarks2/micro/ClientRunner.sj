//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.ClientRunner false localhost 8888 100 1 10 JT

package ecoop.bmarks2.micro;

import ecoop.bmarks2.micro.java.thread.client.*;
//import ecoop.bmarks.java.event.client.*;
//import ecoop.bmarks.sj.client.*;

// Spawns LoadClients.
public class ClientRunner 
{
  public static void main(String [] args) throws Exception
  {
    final boolean debug = Boolean.parseBoolean(args[0]);
    final String host = args[1];
    final int port = Integer.parseInt(args[2]);

    int delay = Integer.parseInt(args[3]);
    int numClients = Integer.parseInt(args[4]);    
    final int serverMessageSize = Integer.parseInt(args[5]);
    
  	final String flag = args[6];
  	
  	if (!(flag.equals(ServerRunner.JAVA_THREAD) || flag.equals(ServerRunner.JAVA_EVENT) || flag.equals(ServerRunner.SJ_THREAD) || flag.equals(ServerRunner.SJ_EVENT)))
		{
  		System.out.println("[ClientRunner] Bad server flag: " + flag);
  		
  		return;
		}   
    
    for (int i = 0; i < numClients; i++)	
    {
      final int cid = i;
      
      new Thread() 
      {
        public void run() 
        {
        	try
        	{
        		if (flag.equals(ServerRunner.JAVA_THREAD))
        		{
        			new ecoop.bmarks2.micro.java.thread.client.LoadClient(debug, host, port, cid, serverMessageSize).run();
        		}
        		/*else if (flag.equals(ServerRunner.JAVA_EVENT))
        		{
        			new ecoop.bmarks.java.event.client.LoadClient(debug, host, port, cid, serverMessageSize).run();
        		}
        		else if (flag.equals(ServerRunner.SJ_THREAD) || flag.equals(ServerRunner.SJ_EVENT))
        		{
        			new ecoop.bmarks.sj.client.LoadClient(debug, host, port, cid, serverMessageSize).run();
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
  }
}
