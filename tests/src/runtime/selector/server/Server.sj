//$ bin/sessionjc tests/src/runtime/selector/server/Server.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ runtime.selector.server.Server false d d 8888

package runtime.selector.server;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.session.SJCompatibilityMode;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;

public class Server
{	
	public protocol p_selector ?(int).!<int>
	public protocol p_server sbegin.@(p_selector)
	
	
	public void run(boolean debug, String setups, String transports, int port) throws Exception
	{
		SJSessionParameters params = SJTransportUtils.createSJSessionParameters(setups, transports);
		
		final noalias SJSelector sel = SJRuntime.selectorFor(p_selector);
		
		try (sel)
		{		
			noalias SJServerSocket ss;
			
			try (ss)
			{
				//SJSessionParameters params = SJTransportUtils.createSJSessionParameters(setups, transports);
				
				ss = SJServerSocket.create(p_server, port, params);
				
				sel.registerAccept(ss);
				
				while (true)
				{
					final noalias SJSocket s;
					
					try (s)
					{
						s = sel.select();

						//System.out.println("Accepted connection from: " + s.getHostName() + ":" + s.getPort());
						
						System.out.println("Received: " + s.receiveInt());						
											
						//System.out.println("Received: " + (String) s.receive(1000));
						//System.out.println("Received: " + (String) s.receive());
									
						System.out.println("Current session type: " + s.currentSessionType());
						System.out.println("Remaining session type: " + s.remainingSessionType());
						
						s.send(12345);
						
						System.out.println("Current session type: " + s.currentSessionType());
						System.out.println("Remaining session type: " + s.remainingSessionType());					
					}
					/*catch (Exception x)
					{
						x.printStackTrace();
					}*/
					finally 
					{
						
					}
				}
			}
			finally
			{
				
			}
		}
		finally
		{
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		
		String setups = args[1];
		String transports = args[2];

		int port = Integer.parseInt(args[3]);
		
		new Server().run(debug, setups, transports, port);
	}
}
