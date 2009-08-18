package sessionj.runtime.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import sessionj.types.sesstypes.SJSessionType;

import sessionj.runtime.*;
import sessionj.runtime.session.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.*;

abstract public class SJAbstractSocket implements SJSocket
{
	//protected static final SJTransportManager sjtm = SJRuntime.getTransportManager();
	
	private SJProtocol protocol;
	
	private String hostName;	
	private int port;
	
	private String localHostName; // Session-level values.
	private int localPort;
	
	private SJConnection conn;
	
	private SJSessionProtocols sp;
	private SJSerializer ser;
	
	private boolean isActive = false;

	private SJSessionParameters params;
	
	/*protected SJAbstractSocket(SJProtocol protocol) throws SJIOException
	{
		this(protocol, SJSessionParameters.DEFAULT_PARAMETERS); 
	}*/
	
	protected SJAbstractSocket(SJProtocol protocol, SJSessionParameters params) throws SJIOException
	{
		this.protocol = protocol; // Remainder of initialisation for client sockets performed when init is called.
		this.params = params;
		
		try
		{
			localHostName = InetAddress.getLocalHost().getHostName();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}	
	}
	
	protected void init(SJConnection conn) throws SJIOException // conn can be null (delegation case 2?).
	{
		this.conn = conn;
		//this.ser = new SJDefaultSerializer(conn); // FIXME: should be...
		this.ser = SJRuntime.getSerializer(conn);
		this.sp = new SJSessionProtocolsImpl(this, ser); // ... user configurable.		
	}	
	
	protected void reconnect(SJConnection conn) throws SJIOException
	{
		this.ser.close();
		
		this.conn = conn;
		//this.ser = new SJDefaultSerializer(conn);
		this.ser = SJRuntime.getSerializer(conn);
		this.sp.setSerializer(ser);
	}
	
	protected void accept() throws SJIOException, SJIncompatibleSessionException
	{		
		sp.accept();
	}
	
	protected void request() throws SJIOException, SJIncompatibleSessionException
	{		
		sp.request();
	}

	public void close() // FIXME: not compatible with delegation.
	{
		sp.close(); 
	}

	public void send(Object o) throws SJIOException
	{
		sp.send(o);
	}
	
	public void sendInt(int i) throws SJIOException
	{
		sp.sendInt(i);
	}

	public void sendBoolean(boolean b) throws SJIOException
	{
		sp.sendBoolean(b);
	}
	
	public void sendDouble(double d) throws SJIOException
	{
		sp.sendDouble(d);
	}
	
	public void pass(Object o) throws SJIOException
	{
		sp.pass(o);
	}
	
	public void copy(Object o) throws SJIOException
	{
		sp.copy(o);
	}
	
	/*public void copyInt(int i) throws SJIOException
	{
		sp.copyInt(i);
	}

	public void copyDouble(double d) throws SJIOException
	{
		sp.copyDouble(d);
	}*/
	
	public Object receive() throws SJIOException, ClassNotFoundException
	{
		return sp.receive();
	}
	
	public int receiveInt() throws SJIOException
	{
		return sp.receiveInt();
	}

	public boolean receiveBoolean() throws SJIOException
	{
		return sp.receiveBoolean();
	}
	
	public double receiveDouble() throws SJIOException
	{
		return sp.receiveDouble();
	}
	
	/*public Object receive(int timeout) throws SJIOException, ClassNotFoundException
	{
		//return sp.receive(timeout);
		
		return sp.receive();
	}
	
	public int receiveInt(int timeout) throws SJIOException
	{
		//return sp.receiveInt(timeout);
		
		return sp.receiveInt();
	}

	public boolean receiveBoolean(int timeout) throws SJIOException
	{
		//return sp.receiveBoolean(timeout);
		
		return sp.receiveBoolean();
	}
	
	public double receiveDouble(int timeout) throws SJIOException
	{
		//return sp.receiveDouble(tiimeout);
		
		return sp.receiveDouble();
	}*/
	
	public void outlabel(String lab) throws SJIOException
	{
		sp.outlabel(lab);
	}
	
	public String inlabel() throws SJIOException
	{
		return sp.inlabel();
	}
	
	public boolean outsync(boolean b) throws SJIOException
	{
		sp.outsync(b);
		
		return b;
	}
	
	public boolean insync() throws SJIOException
	{
		return sp.insync();
	}
		
	public void sendChannel(SJService c, String encoded) throws SJIOException
	{
		//sp.sendChannel(c, SJRuntime.getTypeEncoder().decode(c.getProtocol().encoded()));
		sp.sendChannel(c, SJRuntime.getTypeEncoder().decode(encoded));
	}
	
	public SJService receiveChannel(String encoded) throws SJIOException
	{
		return sp.receiveChannel(SJRuntime.getTypeEncoder().decode(encoded));
	}
	
	public void delegateSession(SJAbstractSocket s, String encoded) throws SJIOException
	{
		//throw new SJRuntimeException("[SJDelegateSession] Operation not yet supported.");
		
		sp.delegateSession(s, SJRuntime.getTypeEncoder().decode(encoded));
	}
	
	//public SJAbstractSocket receiveSession(String encoded) throws SJIOException
	public SJAbstractSocket receiveSession(String encoded, SJSessionParameters params) throws SJIOException
	{
		//throw new SJRuntimeException("[SJSessionProtocolsImpl] Operation not yet supported.");
		
		return sp.receiveSession(SJRuntime.getTypeEncoder().decode(encoded), params);
	}
	
	public boolean isActive()
	{
		return this.isActive;
	}

	protected void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}
		
	//protected SJConnection getConnection()
	public SJConnection getConnection()
	{
		return conn;
	}
	
	//protected SJSerializer getSerializer()
	public SJSerializer getSerializer()
	{
		return ser;
	}
	
	public SJSessionProtocols getSJSessionProtocols() // Access modifier too open.
	{
		return sp;
	}

	public SJProtocol getProtocol()
	{
		return protocol;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	/*protected*/ public void setHostName(String hostName) // Access by users disallowed by compiler.
	{
		this.hostName = hostName;
	}
	
	public int getPort()
	{
		return port;
	}
	
	/*protected*/ public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getLocalHostName()
	{
		return localHostName;
	}	
	
	public int getLocalPort()
	{
		return localPort;
	}
	
	protected void setLocalHostName(String localHostName)
	{
		this.localHostName = localHostName;
	}
	
	protected void setLocalPort(int localPort)
	{
		this.localPort = localPort;
	}
	
	public SJSessionParameters getParameters()
	{
		return params;		
	}
	
	/*protected void setParameters(SJSessionParameters params)
	{
		this.params = params;
	}*/
	
	// Hacks for bounded-buffer communication.

	/*public void sendBB(Object o) throws SJIOException
	{
		sp.sendBB(o);
	}
	
	public void passBB(Object o) throws SJIOException
	{
		sp.passBB(o);
	}
	
	public Object receiveBB() throws SJIOException, ClassNotFoundException
	{
		return sp.receiveBB();
	}
	
	public void outlabelBB(String lab) throws SJIOException
	{
		sp.outlabelBB(lab);
	}
	
	public String inlabelBB() throws SJIOException
	{
		return sp.inlabelBB();
	}*/
	
	/*public boolean recurseBB(String lab) throws SJIOException
	{
		return sp.recurseBB(lab);
	}*/
}
