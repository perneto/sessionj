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
		return new SJSessionParameters(mode, parseTransportFlags(setups), parseTransportFlags(transports), cmf);
	}
  
	// Purpose: for session-specific parameters. Don't want to change system-wide settings. 
	public static SJSessionParameters createSJSessionParameters(String setups, String transports) throws SJIOException, SJSessionParametersException 
	{
		return new SJSessionParameters(parseTransportFlags(setups), parseTransportFlags(transports));	
	}

	public static SJSessionParameters createSJSessionParameters(String setups, String transports, int boundedBufferSize) throws SJSessionParametersException, SJIOException 
	{		
		return new SJSessionParameters(parseTransportFlags(setups), parseTransportFlags(transports), boundedBufferSize);
	}

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
