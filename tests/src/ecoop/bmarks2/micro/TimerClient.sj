package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;

abstract public class TimerClient extends Client
{
  private int sessionLength;
	private int repeats; // This means "inner repeats", i.e. the number of measurements to take per Server instance.
  
  public TimerClient(boolean debug, String host, int port, int cid, int serverMessageSize, int sessionLength, int repeats) 
  {
  	super(debug, host, port, cid, serverMessageSize);
  	
    this.sessionLength = sessionLength;     	
  	this.repeats = repeats;
  }

  public int getSessionLength()
  {
  	return sessionLength;
  }
  
  public void run() throws Exception
  {
  	try
  	{
	  	run(false); // Dummy run for warm up.
	  	
	  	debugPrintln("[TimerClient] Finished dummy run, now taking measurements.");
  		
  		for (int i = 0; i < repeats; i++)
  		{		  	
		  	run(true);
  		}
  	}
  	finally
  	{
	  	/*debugPrintln("[TimerClient] Sending KILL.");
	  	
	  	new SignalClient().sendSignal(host, port, MyObject.KILL);*/  		
  	}
  }
  
  abstract public void run(boolean timer) throws Exception;
}
