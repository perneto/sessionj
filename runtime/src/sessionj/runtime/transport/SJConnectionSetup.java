package sessionj.runtime.transport;

import java.net.DatagramSocket;
import java.util.*;

import sessionj.runtime.SJIOException;

/**
 * 
 * @author Raymond
 * @deprecated
 *
 */
abstract public class SJConnectionSetup
{
	abstract public SJSetupNegotiator getServerNegotiator(int port) throws SJIOException; // Session port.  
	abstract public SJSetupNegotiator getClientNegotiator() throws SJIOException; 
		
	abstract public String sessionHostToSetupHost(String hostName);
	abstract public int sessionPortToSetupPort(int port);
	
	abstract public boolean portInUse(int port);
	
	abstract public String getSetupName();
}
