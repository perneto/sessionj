package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJRuntime;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.net.SJSessionParametersException;
import sessionj.runtime.session.SJCompatibilityMode;
import sessionj.runtime.session.SJCustomMessageFormatter;

import java.util.List;

public class SJTransportUtils {
    private SJTransportUtils() {

    }

    public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode) throws SJSessionParametersException {
        return new SJSessionParameters(mode);
    }

    public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode, SJCustomMessageFormatter cmf) throws SJSessionParametersException {
        return new SJSessionParameters(mode, cmf);
    }

    public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize) throws SJSessionParametersException, SJIOException {
        List<SJTransport> ss = parseTransportFlags(setups);
        List<SJTransport> ts = parseTransportFlags(transports);

        return new SJSessionParameters(ss, ts, boundedBufferSize);
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
