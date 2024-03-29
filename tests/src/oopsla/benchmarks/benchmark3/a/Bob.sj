//$ bin/sessionjc -cp tests/classes/ tests/src/benchmarks/benchmark3/a/Bob.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ benchmarks.benchmark3.a.Bob false 8888 localhost 9999

package benchmarks.benchmark3.a;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;

import benchmarks.*;

public class Bob
{	
	private final noalias protocol p
	{
		?[?(NoAliasBinaryTree).!<NoAliasBinaryTree>]*
	}

	private final noalias protocol p_alice 
	{
		sbegin.@(p)
	}
	
	private final noalias protocol p_carol 
	{
		cbegin.!<@(p)>
	}
	
	public Bob(boolean debug, int port, String carol, int carol_port) throws Exception 
	{							
		ServerThread st = new ServerThread(debug, port, carol, carol_port);		
		
		st.start();
	}
	
	private class ServerThread extends Thread 
	{
		private boolean debug;
		private int port;
		private String carol;
		private int carol_port;
		
		public ServerThread(boolean debug, int port, String carol, int carol_port)
		{
			this.debug = debug;
			this.port = port;
			this.carol = carol;
			this.carol_port = carol_port;
		}
		
		public void run()
		{
			final noalias SJServerSocket ss;
			
			try (ss)
			{
				boolean run = true;
				
				ss = SJServerSocketImpl.create(p_alice, port);
				
				new KillThread(port + Kill.KILL_PORT_ADJUST, ss.getCloser()).start();
				
				final noalias SJService c = SJService.create(p_carol, carol, carol_port);
				
				noalias NoAliasBinaryTree bt = null;
				
				for (int counter = 0; run; counter++) 
				{																
					noalias SJSocket ds1;
					final noalias SJSocket ds2;
					
					try (ds1, ds2)
					{					
						ds1 = ss.accept(); // Dummy run.							
						ds2 = c.request();
						
						ds2.pass(ds1);	
		
						noalias SJSocket alice;
						final noalias SJSocket bob;
						
						try(alice, bob)
						{
							alice = ss.accept();
							bob = c.request();
					
							bob.pass(alice);														
						}
						finally 
						{ 
		
						}					
					}			
					catch (Exception x)
					{
						//x.printStackTrace();
						
						run = false;
					}
					finally
					{
						
					}
					
					System.out.println("Finished run: " + counter);
					
					if (debug)
					{
						System.out.println();
					}
				}
			}
			catch (Exception x)
			{
				throw new RuntimeException(x);
			}
			finally
			{
				
			}			
		}
	}
	
	private static void configureTransports()
	{
		List ss = new LinkedList();
		List ts = new LinkedList();
		
		ss.add(new SJStreamTCP());
		
		ts.add(new SJStreamTCP());
		
		SJTransportManager sjtm = SJRuntime.getTransportManager();
		
		sjtm.configureSetups(ss);
		sjtm.configureTransports(ts);				
	}
	
	public static void main(String[] args) throws Exception
	{
		configureTransports();
		
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);
		String carol = args[2];
		int carol_port = Integer.parseInt(args[3]);
	
		new Bob(debug, port, carol, carol_port);
	}
}
