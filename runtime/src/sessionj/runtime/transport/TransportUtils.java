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
			List<SJTransport> ss = parseTransportFlags(setups);
			List<SJTransport> ts = parseTransportFlags(transports);

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
			List<SJTransport> ss = parseTransportFlags(setups);
			List<SJTransport> ts = parseTransportFlags(transports);

			params = new SJSessionParameters(ss, ts);
		}

		return params;
	}

	public static List<SJTransport> parseTransportFlags(String transports)
	{
        List<SJTransport> ts = new LinkedList<SJTransport>();
		if (transports.contains("d"))
		{
			ts.add(new SJFifoPair());
			ts.add(new SJStreamTCP());

			return ts;
		}

        for (char c : transports.toCharArray()) {
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
        return ts;
	}
	
	public static void configureTransports(String setups, String transports)
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
		
		if (!setups.contains("d"))
		{
            sjtm.configureSetups(parseTransportFlags(setups));
		}
		
		if (!transports.contains("d"))
		{
            sjtm.configureTransports(parseTransportFlags(transports));
		}		
	}	
}
