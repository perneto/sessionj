package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;

abstract public class TimerClient extends Client
{
	public static final String BODY = "BODY";
	public static final String INIT = "INIT";
	public static final String FULL = "FULL";
	
  private int sessionLength;
  private String flag; // Set whether we want to include initialisation/close or not.
	private int repeats; // This means "inner repeats", i.e. the number of measurements to take per Server instance.
  
  private long start;
  private long finish;
	
  public TimerClient(boolean debug, String host, int port, int cid, int serverMessageSize, int sessionLength, String flag, int repeats) 
  {
  	super(debug, host, port, cid, serverMessageSize);
  	
    this.sessionLength = sessionLength;
    this.flag = flag.toUpperCase();
  	this.repeats = repeats;
  }

  public int getSessionLength()
  {
  	return sessionLength;
  }
  
  public boolean includeInitialisation() 
  {
  	return (flag.equals(INIT) || flag.equals(FULL));
  }
  
  public boolean includeClose() 
  {
  	return (flag.equals(FULL));
  }
  
  public long startTimer()
  {
  	start = System.nanoTime(); 
  	
  	return start;
  }
  
  public long stopTimer()
  {
  	finish = System.nanoTime();
  	
  	return finish;
  }

  public long printTimer()
  {
  	long duration = finish - start;
  	
  	System.out.println("[TimerClient] Session duration: " + duration + " nanos");
  	
  	return duration;
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
		    
		  	Thread.sleep(50);
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
