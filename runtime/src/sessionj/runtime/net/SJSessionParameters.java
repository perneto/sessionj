package sessionj.runtime.net;

import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;

import java.util.Collections;
import java.util.List;

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

	private int boundedBufferSize = SJBoundedFifoPair.UNBOUNDED_BUFFER_SIZE;
    // HACK: SJSessionParameters are supposed to be user-configurable parameters.
    // But now using as a convenient place to store psuedo compiler-generated optimisation
    // information for now. Would be better to make a dedicated object for storing such information.
    // But that could be slow. // Factor out constant more generally?

    public SJSessionParameters()
	{
        sessionTransports = SJRuntime.getTransportManager().defaultSessionTransports();
        negotiationTransports = SJRuntime.getTransportManager().defaultSessionTransports();
	}
	
	public SJSessionParameters(int boundedBufferSize)
	{
		this();
		this.boundedBufferSize = boundedBufferSize;
	}

	public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports)
	{
		this.negotiationTransports = Collections.unmodifiableList(negotiationTransports); // Relying on implicit iterator ordering.
		this.sessionTransports = Collections.unmodifiableList(sessionTransports);
	}
	
	public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports, int boundedBufferSize)
	{
		this(negotiationTransports, sessionTransports);		
		this.boundedBufferSize = boundedBufferSize;
	}
	
	public List<SJTransport> getNegotiationTransports()
	{
        // already unmodifiableList.
        //noinspection ReturnOfCollectionOrArrayField
        return negotiationTransports;
	}
	
	public List<SJTransport> getSessionTransports()
	{
        // already unmodifiableList.
        //noinspection ReturnOfCollectionOrArrayField
        return sessionTransports;
	}
	
	public String toString()
	{
		String m = "SJSessionParameters(";
		
        m += getNegotiationTransports().toString() + ", " + getSessionTransports().toString();
		
		m += ", " + getBoundedBufferSize();
		
		return m += ")";
	}
	
	public int getBoundedBufferSize()
	{
		return boundedBufferSize;
	}

}
