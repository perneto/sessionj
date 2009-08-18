/**
 * 
 */
package sessionj.runtime.session;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJAbstractSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.net.SJService;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.SJBoundedBufferConnection;
import sessionj.runtime.transport.SJConnection;
import sessionj.types.sesstypes.SJSessionType;

/**
 * @author Raymond
 *
 * Currently the same as SJSocket, but doesn't need to be.
 *
 */
abstract public class SJSessionProtocols
{
	protected SJAbstractSocket s;
	protected SJSerializer ser;
	
	protected boolean zeroCopySupported;
	
	protected boolean boundedBufferSupported;
	
	public SJSessionProtocols(SJAbstractSocket s, SJSerializer ser)
	{
		this.s = s;
		this.ser = ser;
		this.zeroCopySupported = ser.zeroCopySupported();
		
		this.boundedBufferSupported = s.getConnection() instanceof SJBoundedBufferConnection; 
	}
	
	abstract public void accept() throws SJIOException, SJIncompatibleSessionException;
	abstract public void request() throws SJIOException, SJIncompatibleSessionException;	
	abstract public void close();

	// Sending.
	abstract public void send(Object o) throws SJIOException;
	abstract public void sendByte(byte b) throws SJIOException;
	abstract public void sendInt(int i) throws SJIOException;
	abstract public void sendBoolean(boolean b) throws SJIOException;
	abstract public void sendDouble(double d) throws SJIOException;
	
	abstract public void pass(Object o) throws SJIOException;
	
	abstract public void copy(Object o) throws SJIOException;
	/*abstract public void copyInt(int i) throws SJIOException;
	abstract public void copyDouble(double d) throws SJIOException;*/
	
	// Receiving.
	abstract public Object receive() throws SJIOException, ClassNotFoundException;
	abstract public byte receiveByte() throws SJIOException;
	abstract public int receiveInt() throws SJIOException;
	abstract public boolean receiveBoolean() throws SJIOException;
	abstract public double receiveDouble() throws SJIOException;
	
	// Session handling.
	abstract public void outlabel(String lab) throws SJIOException;
	abstract public String inlabel() throws SJIOException;
	abstract public void outsync(boolean bool) throws SJIOException; // Differs from SJSocket.
	abstract public boolean insync() throws SJIOException;
	
	// Higher-order.
	abstract public void sendChannel(SJService c, SJSessionType st) throws SJIOException;
	abstract public SJService receiveChannel(SJSessionType st) throws SJIOException;
	
	abstract public void delegateSession(SJAbstractSocket s, SJSessionType st) throws SJIOException;	
	//abstract public SJAbstractSocket receiveSession(SJSessionType st) throws SJIOException;
	abstract public SJAbstractSocket receiveSession(SJSessionType st, SJSessionParameters params) throws SJIOException;

	//abstract protected SJControlSignal receiveControlSignal() throws SJIOException;
	//abstract protected void sendControlSignal(SJControlSignal cs) throws SJIOException;
	abstract protected void handleControlSignal(SJControlSignal cs) throws SJIOException;
	
	protected SJSerializer getSerializer()
	{
		return ser;
	}
	
	public void setSerializer(SJSerializer ser)
	{
		this.ser = ser;
		this.zeroCopySupported = ser.zeroCopySupported();
	}
	
	/*protected boolean zeroCopySupported()
	{
		return zeroCopySupported;
	}*/
	
	//Hacks for bounded-buffer communication.
	
	//abstract public boolean recurseBB(String lab) throws SJIOException;
}
