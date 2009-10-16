/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;

import java.nio.channels.SelectableChannel;

/**
 * @author Raymond
 *
 */
public interface SJConnectionAcceptor
{
	SJConnection accept() throws SJIOException;
	void close();
	
	boolean interruptToClose();
	
	boolean isClosed();
	String getTransportName(); 
}
