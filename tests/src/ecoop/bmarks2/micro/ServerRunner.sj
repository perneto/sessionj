//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.micro.ServerRunner false 8888 JT
//$ bin/sessionj -Dsessionj.transports.session=a -cp tests/classes/ ecoop.bmarks2.micro.ServerRunner false 8888 SE

package ecoop.bmarks2.micro;

import ecoop.bmarks2.micro.java.thread.server.*;
//import ecoop.bmarks.java.event.flag.*;
//import ecoop.bmarks.sj.flag.thread.*;
//import ecoop.bmarks.sj.flag.event.*;

// Spawns a pair of Server and SignalClient.
public class ServerRunner 
{
	public static final String JAVA_THREAD = "JT";
	public static final String JAVA_EVENT = "JE";
	public static final String SJ_THREAD = "ST";
	public static final String SJ_EVENT = "SE";
	
  public static void main(String args[]) 
  {
    final boolean debug = Boolean.parseBoolean(args[0]);
    final int port = Integer.parseInt(args[1]);
    //final int numClients = Integer.parseInt(args[2]); // NB: TimerClients count as two clients.
    final String flag = args[2];
    
  	if (!(flag.equals(JAVA_THREAD) || flag.equals(JAVA_EVENT) || flag.equals(SJ_THREAD) || flag.equals(SJ_EVENT)))
		{
  		System.out.println("[ServerRunner] Bad server flag: " + flag);
  		
  		return;
		}
  	
  	final Server server;
  	
  	if (flag.equals(JAVA_THREAD))
		{
			server = new ecoop.bmarks2.micro.java.thread.server.Server(debug, port);
		}  
		/*else if (flag.equals(SignalClient.JAVA_EVENT))
		{
			new ecoop.bmarks.java.event.flag.Server2(debug, port, numClients).run();
		}
		else if (flag.equals(SignalClient.SJ_THREAD))
		{
			new ecoop.bmarks.sj.flag.thread.Server2(debug, port, numClients).run();
		}
		else if (flag.equals(SignalClient.SJ_EVENT))
		{
			new ecoop.bmarks.sj.flag.event.Server2(debug, port, numClients).run();
		}*/
  	else
  	{
  		throw new RuntimeException("[ServerRunner] Bad server flag: " + flag);
  	}
  	
    new Thread()
    {
    	public void run()
    	{
    		try
    		{
    			server.run();
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
    	    new SignalServer(debug, port, server).run(); // SignalServer sorts out port offset internally to avoid collision with Server.
    		}
    		catch (Exception x)
    		{
    			throw new RuntimeException(x);
    		}
    	}
    }.start();
  }
}