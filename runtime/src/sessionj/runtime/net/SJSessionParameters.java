package sessionj.runtime.net;

import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Raymond
 *
 * FIXME: the default is mostly a performance hack. It can't be used to e.g. see which transports were actually used (the ones that were the defaults at the time). 
 * 
 */
public class SJSessionParameters //implements Serializable
{
	public static final SJSessionParameters DEFAULT_PARAMETERS = new SJSessionParameters();
	
	private List<SJTransport> negociationTransports; // FIXME: should be "negotiation".
	private List<SJTransport> sessionTransports;

	private boolean useDefault = false;
	
	//public static final int UNBOUNDED_BUFFER_SIZE = -1;
	
	private int boundedBufferSize = SJBoundedFifoPair.UNBOUNDED_BUFFER_SIZE; // HACK: SJSessionParameters are supposed to be user-configurable parameters. But now using as a convenient place to store psuedo compiler-generated optimisation information for now. Would be better to make a dedicated object for storing such information. But that could be slow. // Factor out constant more generally?

    public SJSessionParameters()
	{
		/*this.negociationTransports = new LinkedList<SJTransport>();
		this.sessionTransports = new LinkedList<SJTransport>();*/

        useDefault = true;
	}
	
	public SJSessionParameters(int boundedBufferSize)
	{
		this();
		
		this.boundedBufferSize = boundedBufferSize;
	}
	
	/*public SJSessionParameters(boolean useDefault)
	{
		//if (!useDefault)
		{
			this.negociationTransports = new LinkedList<SJTransport>();
			this.sessionTransports = new LinkedList<SJTransport>();
		}
		
		this.useDefault = useDefault;
	}*/
	
	public SJSessionParameters(List<SJTransport> negociationTransports, List<SJTransport> sessionTransports)
	{
		/*this.negociationTransports = negociationTransports;
		this.sessionTransports = sessionTransports;*/
		
		this.negociationTransports = new LinkedList<SJTransport>(negociationTransports); // Relying on implicit iterator ordering.
		this.sessionTransports = new LinkedList<SJTransport>(sessionTransports);
		
		/*Collections.copy(this.negociationTransports, negociationTransports);
		Collections.copy(this.sessionTransports, sessionTransports);*/
	}
	
	public SJSessionParameters(List<SJTransport> negociationTransports, List<SJTransport> sessionTransports, int boundedBufferSize)
	{
		this(negociationTransports, sessionTransports);
		
		this.boundedBufferSize = boundedBufferSize;
	}
	
	/*public void addSetup(SJTransport setup) // Work towards making this class immutable.
	{
		negociationTransports.add(setup);
	}
	
	public void addSetups(List<SJTransport> negociationTransports)
	{
		this.negociationTransports = negociationTransports;
	}*/
	
	public List<SJTransport> getNegociationTransports()
	{
		return new LinkedList<SJTransport>(negociationTransports);
	}
	
	/*public void addTransport(SJTransport transport)
	{
		sessionTransports.add(transport);
	}
	
	public void addTransports(List<SJTransport> sessionTransports)
	{
		this.sessionTransports = sessionTransports;
	}*/
	
	public List<SJTransport> getSessionTransports()
	{
		return new LinkedList<SJTransport>(sessionTransports);
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
			m += getNegociationTransports().toString() + ", " + getSessionTransports().toString();
		}
		
		m += ", " + getBoundedBufferSize();
		
		return m += ")";
	}
	
	public int getBoundedBufferSize()
	{
		return boundedBufferSize;
	}
}
