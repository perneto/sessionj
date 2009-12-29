//$ bin/sessionj -cp tests/classes/ ecoop.bmarks2.macro.smtp.ServerRunner false 2525 s ST
//$ bin/sessionj -Dsessionj.transports.session=a -cp tests/classes/ ecoop.bmarks2.macro.smtp.ServerRunner false 2525 a SE

package ecoop.bmarks2.macro.smtp;

import ecoop.bmarks2.macro.smtp.sj.*;

// Spawns a pair of Server and SignalServer.
public class ServerRunner 
{
	public static final String SJ_THREAD = "ST";
	public static final String SJ_EVENT = "SE";
	
  public static void main(String args[]) 
  {
    final boolean debug = Boolean.parseBoolean(args[0]);
    final int port = Integer.parseInt(args[1]);
    final String setups = args[2];
    final String flag = args[3];
    
  	if (!(flag.equals(SJ_THREAD) || flag.equals(SJ_EVENT)))
		{
  		System.out.println("[ServerRunner] Bad server flag: " + flag);
  		
  		return;
		}
  	
  	final Server server;
  	
  	if (flag.equals(SJ_THREAD))
		{
			server = new ecoop.bmarks2.macro.smtp.sj.thread.server.Server(debug, port, setups);
		}
		/*else if (flag.equals(SJ_EVENT))
		{
			server = new ecoop.bmarks2.macro.smtp.sj.event.server.Server(debug, port, setups);
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
    	    new ecoop.bmarks2.micro.SignalServer(debug, port, server).run(); // SignalServer sorts out port offset internally to avoid collision with Server.
    		}
    		catch (Exception x)
    		{
    			throw new RuntimeException(x);
    		}
    	}
    }.start();
  }
}
