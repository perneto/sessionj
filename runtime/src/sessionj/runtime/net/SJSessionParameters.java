package sessionj.runtime.net;

import sessionj.runtime.SJRuntimeException;
import sessionj.runtime.session.SJCompatibilityMode;
import sessionj.runtime.session.SJCustomMessageFormatter;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;

import java.util.Collections;
import java.util.List;
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
	public static /*final*/ SJSessionParameters DEFAULT_PARAMETERS;
	
	static 
	{
		try
		{
			DEFAULT_PARAMETERS = new SJSessionParameters(); // Can we gain performance by moving exception raising parts out of here so can be declared final again?
		}
		catch (SJSessionParametersException spe)
		{
			throw new SJRuntimeException("[SJSessionParameters] Shouldn't get in here.", spe);
		}
	}
	
	private List<SJTransport> negotiationTransports;
	private List<SJTransport> sessionTransports;

	private int boundedBufferSize = SJBoundedFifoPair.UNBOUNDED_BUFFER_SIZE;
  
	private static final Logger logger = Logger.getLogger(SJSessionParameters.class.getName());
	
	private SJCompatibilityMode mode; // The default mode. Uses SJStreamSerializer where possible, SJManualSerialier otherwise. 
	
	private SJCustomMessageFormatter cmf;
	
	// HACK: SJSessionParameters are supposed to be user-configurable parameters.
  // But now using as a convenient place to store pseudo compiler-generated optimisation
  // information for now. Would be better to make a dedicated object for storing such information.
  // But that could be slow. // Factor out constant more generally?
	public SJSessionParameters() throws SJSessionParametersException
	{
		this(defaultNeg(), defaultSession()); 
	}

    private static List<SJTransport> defaultSession() {
        return SJRuntime.getTransportManager().defaultSessionTransports();
    }

    private static List<SJTransport> defaultNeg() {
        return SJRuntime.getTransportManager().defaultNegotiationTransports();
    }
    
    public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports) throws SJSessionParametersException
	{
		this(SJCompatibilityMode.SJ, negotiationTransports, sessionTransports); // SJ is the default mode. Uses SJStreamSerializer where possible, SJManualSerialier otherwise.
	}
	
	public SJSessionParameters(SJCompatibilityMode mode) throws SJSessionParametersException
	{
		this(mode, defaultNeg(), defaultSession()); 
	}
	
	// FIXME: should be generalised to support custom "deserializers" for other wire formats. Well, in principle, the programmer should add a custom SJSerializer. But this interface may be easier to use than a full serializer implemetation.
	public SJSessionParameters(SJCompatibilityMode mode, SJCustomMessageFormatter cmf) throws SJSessionParametersException
	{
		this(mode, defaultNeg(), defaultSession(), cmf); 
	}	
	
	public SJSessionParameters(SJCompatibilityMode mode, List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports) throws SJSessionParametersException
	{
		this(mode, negotiationTransports, sessionTransports, null);
	}
	
	public SJSessionParameters(SJCompatibilityMode mode, List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports, SJCustomMessageFormatter cmf) throws SJSessionParametersException
	{
		this.mode = mode;
        this.negotiationTransports = Collections.unmodifiableList(negotiationTransports); // Relying on implicit iterator ordering.
        this.sessionTransports = Collections.unmodifiableList(sessionTransports);
		this.cmf = cmf;
		
		if (!SJRuntime.checkSessionParameters(this)) // Maybe should check more "lazily" at session initiation. Might be a bit convenient from an exception handling point of view. 
		{
			//throw new... // SJRuntime.checkSessionParameters is already raising appropriate exceptions.
		}
	}
	
	public SJSessionParameters(int boundedBufferSize) throws SJSessionParametersException
	{
		this();
		
		this.boundedBufferSize = boundedBufferSize;
	}	
	
	public SJSessionParameters(List<SJTransport> negotiationTransports, List<SJTransport> sessionTransports, int boundedBufferSize) throws SJSessionParametersException
	{
		this(negotiationTransports, sessionTransports); // FIXME: "bounded-buffers" should be a mode (shouldn't be SJ default).
		
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
		String m = "SJSessionParameters{";
		
        m += negotiationTransports + ", " + sessionTransports;
		m += ", " + boundedBufferSize;
		
		return m + "}";
	}
	
	public int getBoundedBufferSize()
	{
		return boundedBufferSize;
	}

   public SJCompatibilityMode getCompatibilityMode()
   {
  	 return mode;
   }
   
	public SJCustomMessageFormatter getCustomMessageFormatter()
	{
		return cmf; 
	}
}
