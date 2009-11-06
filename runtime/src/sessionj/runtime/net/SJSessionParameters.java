package sessionj.runtime.net;

import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import sessionj.runtime.transport.sharedmem.SJFifoPair;
import sessionj.runtime.transport.tcp.SJAsyncManualTCP;
import sessionj.runtime.transport.tcp.SJStreamTCP;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Raymond
 *
 * FIXME: the default is mostly a performance hack. It can't be used to e.g. see which transports were actually used (the ones that were the defaults at the time). 
 * 
 */
public class SJSessionParameters
{
	public static final SJSessionParameters DEFAULT_PARAMETERS = new SJSessionParameters();
	
	private List<SJTransport> negotiationTransports;
	private List<SJTransport> sessionTransports;

	private boolean useDefault = false;
	
	private int boundedBufferSize = SJBoundedFifoPair.UNBOUNDED_BUFFER_SIZE;
    private static final Logger logger = Logger.getLogger(SJSessionParameters.class.getName());
    // HACK: SJSessionParameters are supposed to be user-configurable parameters.
    // But now using as a convenient place to store psuedo compiler-generated optimisation
    // information for now. Would be better to make a dedicated object for storing such information.
    // But that could be slow. // Factor out constant more generally?

    public SJSessionParameters()
	{
        useDefault = true;
        sessionTransports = defaultTransports();
        negotiationTransports = defaultTransports();
	}
	
	public SJSessionParameters(int boundedBufferSize)
	{
		this();
		this.boundedBufferSize = boundedBufferSize;
	}

	public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports)
	{
		this.negotiationTransports = new LinkedList<SJTransport>(negotiationTransports); // Relying on implicit iterator ordering.
		this.sessionTransports = new LinkedList<SJTransport>(sessionTransports);
	}
	
	public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports, int boundedBufferSize)
	{
		this(negotiationTransports, sessionTransports);		
		this.boundedBufferSize = boundedBufferSize;
	}
	
	public List<SJTransport> getNegotiationTransports()
	{
		return new LinkedList<SJTransport>(negotiationTransports);
	}
	
	public List<SJTransport> getSessionTransports()
	{
		return Collections.unmodifiableList(sessionTransports);
	}
	
	public boolean useDefault()
	{
		return useDefault;
	}
	
	public String toString()
	{
		String m = "SJSessionParameters(";
		
		if (useDefault())
		{
			m += "DEFAULT";
		}
		else
		{
			m += getNegotiationTransports().toString() + ", " + getSessionTransports().toString();
		}
		
		m += ", " + getBoundedBufferSize();
		
		return m += ")";
	}
	
	public int getBoundedBufferSize()
	{
		return boundedBufferSize;
	}

    public static List<SJTransport> defaultTransports() {
        List<SJTransport> ts = new LinkedList<SJTransport>();
        ts.add(new SJFifoPair());
        ts.add(new SJStreamTCP());
        try {
            ts.add(new SJAsyncManualTCP());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Async TCP transport will be unavailable", e);
        }

        return ts;
    }
}
