/**
 * 
 */
package sessionj.runtime.transport;

import java.util.*;

import sessionj.runtime.SJIOException;

/**
 * @author Raymond
 * @deprecated
 *
 */
public interface SJSetupNegotiator
{
	public void send(String hostName, int port, List<String> serverTransports) throws SJIOException; // Transport-level values.
	public List<String> receive() throws SJIOException;
	
	public void close();
	
	//public boolean interruptToClose();
	
	public String getSetupName();
}
