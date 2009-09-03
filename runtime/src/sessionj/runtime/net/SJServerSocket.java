/**
 * 
 */
package sessionj.runtime.net;

import java.util.LinkedList;
import java.util.List;

import sessionj.runtime.*;
import sessionj.runtime.transport.SJAcceptorThreadGroup;
import sessionj.runtime.transport.SJConnection;

/**
 * @author Raymond
 *
 */
abstract public class SJServerSocket
{
	private SJProtocol protocol;
	private int port; // The local port.

	private SJPort sjPort; // Later probably replace port by just sjPort.
	
	protected boolean isOpen = false;
	
	private SJSessionParameters params;
	
	protected SJServerSocket(SJProtocol protocol, int port, SJSessionParameters params) {
		this.protocol = protocol;
		this.port = port;
		this.params = params;
	}	
	
	public static SJServerSocket create(SJProtocol protocol, int port) throws SJIOException
	{	
		return create(protocol, port, SJSessionParameters.DEFAULT_PARAMETERS);
	}
	
	public static SJServerSocket create(SJProtocol protocol, int port, SJSessionParameters params) throws SJIOException
	{
		SJServerSocket ss = new SJServerSocketImpl(protocol, port, params);
		
		ss.init();
		
		return ss;
	}
	
	public static SJServerSocket create(SJProtocol protocol, SJPort sjPort) throws SJIOException
	{		
		SJServerSocket ss = new SJServerSocketImpl(protocol, sjPort.getValue(), sjPort.getParameters()); // No need to keep hold of the SJPort object?
		
		ss.sjPort = sjPort;
		
		ss.init(); // Need to call init after the SJPort has been recorded (cannot reuse above create routines).
		
		return ss;
	}
	
	/*public static SJServerSocket create(SJProtocol protocol, SJPort sjPort, SJSessionParameters params) throws SJIOException // No, SJPorts session parameters already configured. 
	{
		SJServerSocket ss = new SJServerSocketImpl(protocol, sjPort.getValue(), params); 
		
		ss.sjPort = sjPort;
		
		ss.init();
		
		return ss;
	}*/
	
	abstract public SJAbstractSocket accept() throws SJIOException, SJIncompatibleSessionException;
	abstract public void close();
	
	public SJProtocol getProtocol()
	{
		return protocol;
	}
	
	public int getLocalPort()
	{
		return port;
	}
	
	public boolean isClosed()
	{
		return !isOpen;
	}
	
	abstract protected void init() throws SJIOException;
	
	abstract protected SJAcceptorThreadGroup getAcceptorGroup();
	abstract protected void setAcceptorGroup(SJAcceptorThreadGroup acceptors);
	
	public SJServerSocketCloser getCloser()
	{
		return new SJServerSocketCloser(this);
	}
	
	public SJPort getLocalSJPort()
	{
		return sjPort; // FIXME: currently returns null if the server socket was initialised using an integer port value.
	}
	
	public SJSessionParameters getParameters()
	{
		return params;
	}
}
