package sessionj.runtime.transport;

import sessionj.runtime.*;
import sessionj.runtime.net.SJServerIdentifier;

/**
 * @author Raymond, Fred van den Driessche
 * This interface defines low level communication operations used by Session Java sockets
 * A transport is used by a socket to read/write bytes to/from a peer.
 */
public interface SJTransport 
{
	public SJConnectionAcceptor openAcceptor(int port) throws SJIOException; // Transport-level port.	
	//public SJConnection connect(SJServerIdentifier si) throws SJIOException;
	public SJConnection connect(String hostName, int port) throws SJIOException; // May be useful to pass additional higher-level information to these operations, such as the session type (e.g. for bounded-size buffers). 
	
	public boolean portInUse(int port);
	public int getFreePort() throws SJIOException;
	
	public String getTransportName();
	
	abstract public String sessionHostToSetupHost(String hostName);
	abstract public int sessionPortToSetupPort(int port);	
	
	/*
	 * TODO: Extras:
	 * 		method to get dual server transport - needed if a client needs
	 * 		to accept a delegation
	 * 		supports fast object writing? - returns yes/no if transport can pass
	 * 		(noalias) object by reference. Memory return true, file/net false
	 * 		writeObject(noalias Object) - method to send an object by reference.
	 * 		isConnected could be useful?
	 */
}
