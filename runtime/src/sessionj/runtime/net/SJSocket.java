package sessionj.runtime.net;

import sessionj.runtime.*;

public interface SJSocket 
{	
	public void close();
	
	// Sending.
	public void send(Object o) throws SJIOException;
	public void sendInt(int i) throws SJIOException;
	public void sendBoolean(boolean b) throws SJIOException;
	public void sendDouble(double d) throws SJIOException;	
	
	public void pass(Object o) throws SJIOException;
	
	public void copy(Object o) throws SJIOException;
	/*public void copyInt(int i) throws SJIOException;
	public void copyDouble(double d) throws SJIOException;*/
	
	// Receiving.
	public Object receive() throws SJIOException, ClassNotFoundException;
	public int receiveInt() throws SJIOException;
	public boolean receiveBoolean() throws SJIOException;
	public double receiveDouble() throws SJIOException;
	
	/*public Object receive(int timeout) throws SJIOException, ClassNotFoundException;
	public int receiveInt(int timeout) throws SJIOException;
	public boolean receiveBoolean(int timeout) throws SJIOException;
	public double receiveDouble(int timeout) throws SJIOException;*/
	
	// Session handling.
	public void outlabel(String lab) throws SJIOException;
	public String inlabel() throws SJIOException;
	public boolean outsync(boolean bool) throws SJIOException;
	public boolean insync() throws SJIOException;	
	
	// Higher-order.
	//public void sendChannel(SJService c) throws SJIOException;
	public void sendChannel(SJService c, String encoded) throws SJIOException;
	public SJService receiveChannel(String encoded) throws SJIOException;
	
	public void delegateSession(SJAbstractSocket s, String encoded) throws SJIOException;	
	//public SJAbstractSocket receiveSession(String encoded) throws SJIOException;
	public SJAbstractSocket receiveSession(String encoded, SJSessionParameters params) throws SJIOException;
	
	public SJProtocol getProtocol();
	
	public String getHostName();
	public int getPort();
	
	public String getLocalHostName();
	public int getLocalPort();	
	
	public SJSessionParameters getParameters();
	//public void setParameters(SJSocketParameters params);
	
	//Hacks for bounded-buffer communication.
	
	//public boolean recurseBB(String lab) throws SJIOException;
}
