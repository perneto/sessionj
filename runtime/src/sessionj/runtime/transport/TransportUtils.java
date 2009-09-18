package sessionj.runtime.transport;

import sessionj.runtime.net.SJRuntime;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.httpservlet.SJHTTPServlet;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import sessionj.runtime.transport.sharedmem.SJFifoPair;
import sessionj.runtime.transport.tcp.SJManualTCP;
import sessionj.runtime.transport.tcp.SJStreamTCP;

import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

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
			List<SJTransport> ss = new LinkedList<SJTransport>();
			List<SJTransport> ts = new LinkedList<SJTransport>();

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
			List<SJTransport> ss = new LinkedList<SJTransport>();
			List<SJTransport> ts = new LinkedList<SJTransport>();

			parseTransportFlags(ss, setups);
			parseTransportFlags(ts, transports);

			params = new SJSessionParameters(ss, ts);
		}

		return params;
	}
	
	public static void parseTransportFlags(Collection<SJTransport> ts, String transports)
	{
		if (transports.contains("d"))
		{
			ts.add(new SJFifoPair());
			ts.add(new SJStreamTCP());

			return;
		}

		char[] cs = transports.toCharArray();

        for (char c : cs) {
            switch (c) {
                case 'f':
                    ts.add(new SJFifoPair());

                    break;
                case 'b':
                    ts.add(new SJBoundedFifoPair());

                    break;
                case 's':
                    ts.add(new SJStreamTCP());

                    break;
                case 'm':
                    ts.add(new SJManualTCP());

                    break;
                case 'h':
                    ts.add(new SJHTTPServlet());

                    break;
            }
        }
	}
	
	public static void configureTransports(String setups, String transports)
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
		
		if (!setups.contains("d"))
		{
			List<SJTransport> ss = new LinkedList<SJTransport>();
			
			parseTransportFlags(ss, setups);		
			
			sjtm.configureSetups(ss);
		}
		
		if (!transports.contains("d"))
		{
			List<SJTransport> ts = new LinkedList<SJTransport>();
			
			parseTransportFlags(ts, transports);	
			
			sjtm.configureTransports(ts);
		}		
	}	
}
