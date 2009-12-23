package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import ecoop.bmarks2.micro.*;

abstract public class Server  
{
	private boolean debug;		
  private int port;

  /*protected int numClients;
  protected Object lock = new Object();*/
  private AtomicInteger numClients = new AtomicInteger(0);
  
  private long startTime; 
  private long finishTime; 
  
  private boolean count;  
  private static int[] counts; // The number of messages sent.
  
  public Server(boolean debug, int port) 
  {
  	this.debug = debug;  	
    this.port = port;
  }

  public final int getPort()
  {
  	return port;
  }
  
  abstract public void run() throws Exception; // Allow Clients to connect and run sessions. 
  abstract public void kill() throws Exception; // Wait for Clients to finish and then end. 
  
  protected final int addClient()
  {  	
  	return numClients.incrementAndGet();
  }
  
  public final int getNumClients()
  {  	
  	return numClients.get();
  }
  
  public final int removeClient()
  {
  	return numClients.decrementAndGet();
  }
  
  public final void startCounting() 
  {
  	this.counts = new int[getNumClients()]; // All clients should be connected before this is called.
  	this.count = true;
  	this.startTime = System.nanoTime();
  }
  
  public final boolean isCounting()
  {
  	return this.count; 
  }
  
  public final int incrementCount(int index) // Not synchronized because we expect only a single thread to access each element.
  {
  	counts[index]++;
  	
  	return counts[index];
  }
  
  public final void stopCountingAndReset()
  { 
  	this.finishTime = System.nanoTime();
  	this.count = false;
  	
  	System.out.println("[Server] Count duration: " + (finishTime - startTime) + " nanos");
  	System.out.println("[Server] Total count: " + getCountTotal());
  }
  
  public final long getCountTotal()
  {
  	long total = 0;
  	
  	for (int i = 0; i < counts.length; i++)
		{
			total += counts[i];
		}
		
		return total;  	
  }
  
  public final boolean isDebug()
  {
  	return debug;
  }
  
  public final void debugPrintln(String m)
  {
  	Common.debugPrintln(debug, m);
  }
}
