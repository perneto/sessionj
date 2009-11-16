package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelectorInternal;

/**
 * @author Raymond, Fred van den Driessche
 * This interface defines low level communication operations used by Session Java sockets
 * A transport is used by a socket to read/write bytes to/from a peer.
 */
public interface SJTransport 
{
	SJConnectionAcceptor openAcceptor(int port) throws SJIOException; // Transport-level port.
	//SJConnection connect(SJServerIdentifier si) throws SJIOException;
    SJConnection connect(String hostName, int port) throws SJIOException;
    // May be useful to pass additional higher-level information to these operations,
    // such as the session type (e.g. for bounded-size buffers).

    SJSelectorInternal transportSelector();

    boolean portInUse(int port);
	int getFreePort() throws SJIOException;
	
	String getTransportName();
	
	String sessionHostToNegociationHost(String hostName);
	int sessionPortToSetupPort(int port);
	
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
