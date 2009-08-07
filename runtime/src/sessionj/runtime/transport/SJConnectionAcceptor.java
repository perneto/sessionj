/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;

/**
 * @author Raymond
 *
 */
public interface SJConnectionAcceptor
{
	public SJConnection accept() throws SJIOException;
	public void close();
	
	public boolean interruptToClose();
	
	public boolean isClosed();
	public String getTransportName(); 
}
