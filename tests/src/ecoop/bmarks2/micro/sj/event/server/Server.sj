//$ bin/sessionj -Dsessionj.transports.session=a -cp tests/classes/ ecoop.bmarks2.micro.sj.event.server.Server false 8888

package ecoop.bmarks2.micro.sj.event.server;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

import ecoop.bmarks2.micro.*;

public class Server extends ecoop.bmarks2.micro.Server  
{
	public protocol pRecursion rec X [?{REC: ?(ClientMessage).!<ServerMessage>.#X, QUIT: }]
  public protocol pReceive ?(ClientMessage).!<ServerMessage>.@(pRecursion) 
  public protocol pServer sbegin.@(pRecursion)
  
  private protocol pSelector { @(pRecursion), @(pReceive) }

	volatile private boolean run = true;
	volatile private boolean kill = false;
  volatile private boolean finished = false;

  private Set clients = new HashSet(); 
  
  //private SJServerSocketCloser ssc; // Doesn't work: closing the server socket doesn't break the selector.
  private SJSelectorCloser sc;
  
  public Server(boolean debug, int port) 
  {
  	super(debug, port);
  }

  public void run() throws Exception
  {
		//SJSessionParameters params = SJTransportUtils.createSJSessionParameters("s", "a");
			
		final noalias SJSelector sel = SJRuntime.selectorFor(pSelector);
		
		this.sc = ((SJSelectorAllTransports) sel).getCloser();
		
		try (sel) 
		{
			noalias SJServerSocket ss;
			
			try (ss) 
			{
				//ss = SJServerSocket.create(pServer, port, params);
				ss = SJServerSocket.create(pServer, getPort());
				
				//this.ssc = ss.getCloser();
				
				debugPrintln("[Server] Listening on: " + getPort());
				
			  sel.registerAccept(ss);
			}
	    finally 
	    {
	    	
	    }

			noalias SJSocket s;
	    
			while (this.run) 
	    {
			  try (s) 
			  {		    
		      s = sel.select();
		      
		      typecase (s) 
		      {
		        when(@(pRecursion)) 
		        {
		        	Integer key = new Integer(s.getLocalPort());		        	
		        	
		        	if (!clients.contains(key))
		        	{		        		
		        		addClient();
		        		
		        		clients.add(key);
		        	}
		        	
		          s.recursion(X) 
		          {
		            s.inbranch() 
		            {
		              case REC:
		              {
		              	sel.registerInput(s);
		              }
		              case QUIT:
		              {
		                removeClient();
		                
		                int numClients = getNumClients();
		                
		                debugPrintln("[Server] Clients remaning: " + numClients);
		                
		                if (numClients == 0) // HACK: because the selector closer isn't working.
		                {
		                	this.run = false;
		                }
		              }
		            }
		          }
		        }
		        when(@(pReceive)) 
		        {
		        	ClientMessage cm = (ClientMessage) s.receive();
		          
		          debugPrintln("[Server] Received: " + cm);
		          
		          s.send(new ServerMessage(cm.getServerMessageSize(), this.kill));
		          
		          if (isCounting()) 
		          {
		          	incrementCount(0); // HACK: using a single counter (safe to do so for this single-threaded Server). Could store the "tids" in a map (using local ports as a key), but could be a non-neglible overhead.
		            
		           	debugPrintln("[ServerThread] Current count:" + getCountTotal());		            
		          }
		          
	            sel.registerInput(s);
	          }
	        }
	      }			  
			  finally
			  {
			  	
			  }	
		  }		  	  	    	    
		}
		catch (Exception x) // Selector closer not currently working.
	  {
	  	x.printStackTrace();
	  }
		finally
		{
			this.finished = true;
		}
  }

  public void kill() throws Exception
  {
  	int numClients = getNumClients(); 
  	
  	this.kill = true;
  	
  	System.out.println("1: ");
  	
  	while (getNumClients() > 0);

  	System.out.println("2: ");
  	
  	this.run = false; // Can stop the selector loop after all LoadClients have quit.
  	
		//ssc.close(); // Break the selecting loop forcibly if needed. // Not currently working. 
  	//sc.close(); // Also not currently working.
  	
		while (!this.finished);
  	
		System.out.println("3: ");
		
  	//debugPrintln("[Server] Finished running (" + numClients + " Clients joined).");
		System.out.println("[Server] Finished running (" + numClients + " Clients joined).");
  }
  
  public static void main(String [] args) throws Exception 
  {
  	boolean debug = Boolean.parseBoolean(args[0]);
  	int port = Integer.parseInt(args[1]);
  	
    new Server(debug, port).run();
  }
}
