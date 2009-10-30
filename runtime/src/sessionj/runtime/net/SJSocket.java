package sessionj.runtime.net;

import sessionj.runtime.*;
import sessionj.runtime.session.SJStateManager;
import sessionj.runtime.transport.SJConnection;
import sessionj.types.sesstypes.SJSessionType;

public interface SJSocket extends SJChannel
{	
	void close();
	
	// Sending.
    void send(Object o) throws SJIOException;
	void sendInt(int i) throws SJIOException;
	void sendBoolean(boolean b) throws SJIOException;
	void sendDouble(double d) throws SJIOException;
	
	void pass(Object o) throws SJIOException;
	
	void copy(Object o) throws SJIOException;
	/*public void copyInt(int i) throws SJIOException;
	public void copyDouble(double d) throws SJIOException;*/
	
	// Receiving.
	Object receive() throws SJIOException, ClassNotFoundException;
	int receiveInt() throws SJIOException;
	boolean receiveBoolean() throws SJIOException;
	double receiveDouble() throws SJIOException;
	
	/*public Object receive(int timeout) throws SJIOException, ClassNotFoundException;
	public int receiveInt(int timeout) throws SJIOException;
	public boolean receiveBoolean(int timeout) throws SJIOException;
	public double receiveDouble(int timeout) throws SJIOException;*/
	
	// Session handling.
	void outlabel(String lab) throws SJIOException;
	String inlabel() throws SJIOException;
	boolean outsync(boolean condition) throws SJIOException;
	boolean insync() throws SJIOException;
    boolean isPeerInterruptibleOut(boolean selfInterrupting) throws SJIOException;
    boolean isPeerInterruptingIn(boolean selfInterruptible) throws SJIOException;
    boolean interruptibleOutsync(boolean condition) throws SJIOException;
    boolean interruptingInsync(boolean condition, boolean peerInterruptible) throws SJIOException;

    // Higher-order.
	//public void sendChannel(SJService c) throws SJIOException;
    void sendChannel(SJService c, String encoded) throws SJIOException;
	SJService receiveChannel(String encoded) throws SJIOException;
	
	void delegateSession(SJAbstractSocket s, String encoded) throws SJIOException;
	//public SJAbstractSocket receiveSession(String encoded) throws SJIOException;
    SJAbstractSocket receiveSession(String encoded, SJSessionParameters params) throws SJIOException;
	
	SJProtocol getProtocol();
	
	String getHostName();
	int getPort();
	
	String getLocalHostName();
	int getLocalPort();
	
	SJSessionParameters getParameters();
	//public void setParameters(SJSocketParameters params);
	
	//Hacks for bounded-buffer communication.
	
	//public boolean recurseBB(String lab) throws SJIOException;

  SJConnection getConnection();

  void reconnect(SJConnection connection) throws SJIOException;

  void setHostName(String hostAddress);

  void setPort(int port);

  /** Used for the translation of the typecase construct */
  int typeLabel() throws SJIOException;
  
  public SJStateManager getStateManager();
  public void setStateManager(SJStateManager sm);
  
  public SJSessionType currentSessionType(); // Session actions performed so far (modulo loop types).
  public SJSessionType remainingSessionType();
}
