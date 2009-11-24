package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJRuntime;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.net.SJSessionParametersException;
import sessionj.runtime.session.SJCompatibilityMode;
import sessionj.runtime.session.SJCustomMessageFormatter;
import sessionj.runtime.transport.httpservlet.SJHTTPServlet;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import sessionj.runtime.transport.sharedmem.SJFifoPair;
import sessionj.runtime.transport.tcp.SJAsyncManualTCP;
import sessionj.runtime.transport.tcp.SJManualTCP;
import sessionj.runtime.transport.tcp.SJStreamTCP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SJTransportUtils 
{
	private SJTransportUtils() 
	{
	
	}

	// Purpose: for session-specific parameters. Don't want to change system-wide settings.
	public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode, String setups, String transports, Class<? extends SJCustomMessageFormatter> cmf) throws SJSessionParametersException, SJIOException 
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();
		
		// FIXME: the sjtm transport load calls would make much more sense inside SJSessionParameters (which should take the transport classes as arguments), but will leave it for now.
		List<SJTransport> nts = sjtm.loadNegotiationTransports(parseTransportFlags(setups));
		List<SJTransport> sts = sjtm.loadSessionTransports(parseTransportFlags(transports));
		
		return new SJSessionParameters(mode, nts, sts, cmf);
	}
  
	// Purpose: for session-specific parameters. Don't want to change system-wide settings. 
	public static SJSessionParameters createSJSessionParameters(String setups, String transports) throws SJIOException, SJSessionParametersException 
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();
		
		List<SJTransport> nts = sjtm.loadNegotiationTransports(parseTransportFlags(setups));
		List<SJTransport> sts = sjtm.loadSessionTransports(parseTransportFlags(transports));
		
		return new SJSessionParameters(nts, sts);
	}
	
	/*// Not needed because this utility class is (or was) purely about using the transport letter codes for convenience. However, those have since been moved into SJTransportManager directly.
	// RAY: If this code gets deleted too soon, I won't remember that we don't need these.
	//public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode, SJCustomMessageFormatter cmf) throws SJSessionParametersException 
	public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode, Class<? extends SJCustomMessageFormatter> cmf) throws SJSessionParametersException 
	{
		return new SJSessionParameters(mode, cmf);
	}
	
	public static SJSessionParameters createSJSessionParameters(SJCompatibilityMode mode) throws SJSessionParametersException 
	{
		return new SJSessionParameters(mode);
	}*/

	public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize) throws SJSessionParametersException, SJIOException 
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();
    
		List<SJTransport> nts = sjtm.loadNegotiationTransports(parseTransportFlags(setups));
		List<SJTransport> sts = sjtm.loadSessionTransports(parseTransportFlags(transports));
		
		return new SJSessionParameters(nts, sts, boundedBufferSize);
	}
	
	/*// This simple routine is not intended to make a system-wide settings change.
	public static List<SJTransport> parseTransportFlags(String transports) throws SJIOException 
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();
        
    // FIXME: hacked, to avoid changing the method signature, as many SJ test programs use it. //RAY: that's OK, that's not a big priority.  
		// May load more transports than needed, but no harm done.
    sjtm.loadSessionTransports(transports); // Affects default system-wide settings?
    return sjtm.loadNegotiationTransports(transports);
	}*/

	/*// Cannot use a single routine for parsing the transport letter codes now.
	public static List<SJTransport> parseNegotiationTransportFlags(String transports) throws SJIOException 
	{
    return SJRuntime.getTransportManager().loadNegotiationTransports(transports);
	}
	
	public static List<SJTransport> parseSessionTransportFlags(String transports) throws SJIOException 
	{
    return SJRuntime.getTransportManager().loadSessionTransports(transports);
	}*/	
	
	// Purpose: system-wide settings.
	public static void configureTransports(String setups, String transports) throws SJIOException 
	{
		SJTransportManager sjtm = SJRuntime.getTransportManager();	
	
	  sjtm.loadNegotiationTransports(parseTransportFlags(setups));
	  sjtm.loadSessionTransports(parseTransportFlags(transports));
	}
	
	public static List<Class<? extends SJTransport>> parseTransportFlags(String flags) throws SJIOException
	{
		List<Class<? extends SJTransport>> cs = new LinkedList<Class<? extends SJTransport>>();
		
		for (char c : flags.toCharArray()) 
		{
			if (c == 'd')
			{
				//cs.addAll(parseTransportFlags("fs"));
				cs.add(parseTransportFlag('f'));
				cs.add(parseTransportFlag('s'));				
			}
			else
			{
				cs.add(parseTransportFlag(c));
			}
		}
			
		return cs;
	}
	
  // The original intention is that these "letter codes" are not fundamental enough to be directly defined by the SJTransportManager; we define and process their use here. 
  private static Class<? extends SJTransport> parseTransportFlag(char code) throws SJIOException
  { 
    switch (code) 
    {
    	//case 'd': return 
	    case 'f': return SJFifoPair.class;	    
	    case 's': return SJStreamTCP.class;
	    case 'm': return SJManualTCP.class;
	    case 'a': return SJAsyncManualTCP.class;
	    case 'h': return SJHTTPServlet.class;
	    case 'b': return SJBoundedFifoPair.class;	        
    }

    throw new SJIOException("[SJTransportUtils] Unsupported transport flag: " + code);
  } 	
}
