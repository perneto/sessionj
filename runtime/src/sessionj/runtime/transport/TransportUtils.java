package sessionj.runtime.transport;

import sessionj.runtime.net.SJRuntime;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.SJIOException;

import java.io.IOException;
import java.util.List;

public class TransportUtils
{
    private TransportUtils() {}

    public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize) throws SJIOException {
        List<SJTransport> ss = parseTransportFlags(setups);
        List<SJTransport> ts = parseTransportFlags(transports);

        return new SJSessionParameters(ss, ts, boundedBufferSize);
	}

	public static SJSessionParameters createSJSessionParameters(String setups, String transports) throws SJIOException {
        List<SJTransport> ss = parseTransportFlags(setups);
        List<SJTransport> ts = parseTransportFlags(transports);

        return new SJSessionParameters(ss, ts);
	}

	public static List<SJTransport> parseTransportFlags(String transports) throws SJIOException {
		SJTransportManager sjtm = SJRuntime.getTransportManager();
        
        // FIXME: hacked, to avoid changing the method signature, as many SJ test programs use it.
        // May load more transports than needed, but no harm done.
        sjtm.loadSessionTransports(transports);
        return sjtm.loadNegotiationTransports(transports);
	}

    public static void configureTransports(String setups, String transports) throws SJIOException {
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
		
        sjtm.loadNegotiationTransports(setups);
        sjtm.loadSessionTransports(transports);
	}


}
