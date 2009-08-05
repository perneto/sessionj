//$ bin/sessionjc tests/src/util/TransportUtils.sj -d tests/classes/

package util;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.httpservlet.*;

public class TransportUtils
{
	public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize)
	{
		SJSessionParameters params;

		if (setups.contains("d") && transports.contains("d"))
		{
			params = new SJSessionParameters(boundedBufferSize);
		}
		else
		{
			List ss = new LinkedList();
			List ts = new LinkedList();				

			parseTransportFlags(ss, setups);
			parseTransportFlags(ts, transports);

			params = new SJSessionParameters(ss, ts, boundedBufferSize);
		}

		return params;
	}

	public static SJSessionParameters createSJSessionParameters(String setups, String transports)
	{
		SJSessionParameters params;

		if (setups.contains("d") && transports.contains("d"))
		{
			params = new SJSessionParameters();
		}
		else
		{
			List ss = new LinkedList();
			List ts = new LinkedList();				

			parseTransportFlags(ss, setups);
			parseTransportFlags(ts, transports);

			params = new SJSessionParameters(ss, ts);
		}

		return params;
	}
	
	public static void parseTransportFlags(List ts, String transports)
	{
		if (transports.contains("d"))
		{
			ts.add(new SJFifoPair());
			ts.add(new SJStreamTCP());

			return;
		}

		char[] cs = transports.toCharArray();

		for (int i = 0; i < cs.length; i++)
		{
			switch (cs[i])
			{
				case 'f':
				{
					ts.add(new SJFifoPair());

					break;
				}
				case 'b':
				{
					ts.add(new SJBoundedFifoPair());

					break;
				}
				case 's':
				{
					ts.add(new SJStreamTCP());

					break;
				}					
				case 'm':
				{			
					ts.add(new SJManualTCP());

					break;
				}					
				case 'h':
				{			
					ts.add(new SJHTTPServlet());

					break;
				}
			}
		}					
	}
	
	public static void configureTransports(String setups, String transports)
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
		
		if (!setups.contains("d"))
		{
			List ss = new LinkedList();
			
			parseTransportFlags(ss, setups);		
			
			sjtm.configureSetups(ss);
		}
		
		if (!transports.contains("d"))
		{
			List ts = new LinkedList();
			
			parseTransportFlags(ts, transports);	
			
			sjtm.configureTransports(ts);
		}		
	}	
}
