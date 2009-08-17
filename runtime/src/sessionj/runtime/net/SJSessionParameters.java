package sessionj.runtime.net;

import java.io.Serializable;
import java.util.*;

import sessionj.runtime.transport.*;
import sessionj.runtime.transport.sharedmem.*;

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
	
	private List<SJTransport> setups; 
	private List<SJTransport> transports;

	private boolean useDefault = false;
	
	//public static final int UNBOUNDED_BUFFER_SIZE = -1;
	
	private int boundedBufferSize = SJBoundedFifoPair.UNBOUNDED_BUFFER_SIZE; // HACK: SJSessionParameters are supposed to be user-configurable parameters. But now using as a convenient place to store psuedo compiler-generated optimisation information for now. Would be better to make a dedicated object for storing such information. But that could be slow. // Factor out constant more generally?   
	
	public SJSessionParameters()
	{
		/*this.setups = new LinkedList<SJTransport>();
		this.transports = new LinkedList<SJTransport>();*/
		
		this.useDefault = true;
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
			this.setups = new LinkedList<SJTransport>();
			this.transports = new LinkedList<SJTransport>();
		}
		
		this.useDefault = useDefault;
	}*/
	
	public SJSessionParameters(List<SJTransport> setups, List<SJTransport> transports)
	{
		/*this.setups = setups;
		this.transports = transports;*/
		
		this.setups = new LinkedList<SJTransport>(setups); // Relying on implicit iterator ordering.
		this.transports = new LinkedList<SJTransport>(transports);
		
		/*Collections.copy(this.setups, setups);
		Collections.copy(this.transports, transports);*/
	}
	
	public SJSessionParameters(List<SJTransport> setups, List<SJTransport> transports, int boundedBufferSize)
	{
		this(setups, transports);
		
		this.boundedBufferSize = boundedBufferSize;
	}
	
	/*public void addSetup(SJTransport setup) // Work towards making this class immutable.
	{
		setups.add(setup);
	}
	
	public void addSetups(List<SJTransport> setups)
	{
		this.setups = setups;
	}*/
	
	public List<SJTransport> getSetups() 
	{
		return new LinkedList<SJTransport>(setups);
	}
	
	/*public void addTransport(SJTransport transport)
	{
		transports.add(transport);
	}
	
	public void addTransports(List<SJTransport> transports)
	{
		this.transports = transports;
	}*/
	
	public List<SJTransport> getTransports() 
	{
		return new LinkedList<SJTransport>(transports);
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
			m += getSetups().toString() + ", " + getTransports().toString();
		}
		
		m += ", " + getBoundedBufferSize();
		
		return m += ")";
	}
	
	public int getBoundedBufferSize()
	{
		return boundedBufferSize;
	}
}
