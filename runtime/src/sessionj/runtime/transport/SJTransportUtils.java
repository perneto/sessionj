package sessionj.runtime.transport;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sessionj.runtime.net.SJRuntime;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.net.SJSessionParametersException;
import sessionj.runtime.session.SJCompatibilityMode;
import sessionj.runtime.session.SJCustomMessageFormatter;
import sessionj.runtime.transport.httpservlet.SJHTTPServlet;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import sessionj.runtime.transport.sharedmem.SJFifoPair;
import sessionj.runtime.transport.tcp.SJManualTCP;
import sessionj.runtime.transport.tcp.SJStreamTCP;
import sessionj.runtime.transport.tcp.SJAsyncManualTCP;

public class SJTransportUtils
{
  private SJTransportUtils() 
  {
  
  }

  public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode) throws SJSessionParametersException 
  {
  	return new SJSessionParameters(mode);
  }

  public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode, SJCustomMessageFormatter cmf) throws SJSessionParametersException 
  {
  	return new SJSessionParameters(mode, cmf);
  }
  
  public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize) throws SJSessionParametersException, IOException 
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

	public static SJSessionParameters createSJSessionParameters(String setups, String transports) throws SJSessionParametersException, IOException {
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

	public static List<SJTransport> parseTransportFlags(String transports) throws IOException {
		if (transports.contains("d")) return SJSessionParameters.defaultTransports();

        List<SJTransport> ts = new LinkedList<SJTransport>();
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
                case 't':
                    ts.add(new SJAsyncManualTCP());

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

    public static void configureTransports(String setups, String transports) throws IOException {
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
		
		if (!setups.contains("d"))
		{
            sjtm.configureNegociationTransports(parseTransportFlags(setups));
		}
		
		if (!transports.contains("d"))
		{
            sjtm.configureSessionTransports(parseTransportFlags(transports));
		}		
	}	
}