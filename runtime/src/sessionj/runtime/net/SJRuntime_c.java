package sessionj.runtime.net;

import java.util.*;

import polyglot.types.SemanticException;

import sessionj.ExtensionInfo;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.util.SJTypeEncoder;

import sessionj.runtime.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.*;

public class SJRuntime_c extends SJRuntime
{	
	/*private static final SJTransportManager sjtm = new SJTransportManager_c();
	
	public void openServerSocket(SJServerSocket ss) throws SJIOException
	{		
		ss.setAcceptorGroup(sjtm.openAcceptorGroup(ss.getPort()));
	}
	
	public void closeServerSocket(SJServerSocket ss) 
	{
		sjtm.closeAcceptorGroup(ss.getPort());
	}
	
	public void connectSocket(SJRequestingSocket s) throws SJIOException
	{		
		s.setConnection(sjtm.openConnection(s.getService().getServerIdentifier())); 
	}
	
	public void closeSocket(SJAbstractSocket s) 
	{
		sjtm.closeConnection(s.getConnection());
	}
	
	public void accept(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException
	{
		s.accept();
	}
	
	public void request(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException
	{
		s.request();
	}
	
	public void close(SJAbstractSocket[] sockets)
	{
		for (SJAbstractSocket s : sockets)
		{
			if (s != null) 
			{
				s.close();
			}
		}
	}	
	
	public void send(SJAbstractSocket[] sockets, Object obj) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.send(obj);
		}
	}
	
	public void send(SJAbstractSocket[] sockets, int i) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.sendInt(i);
		}
	}
	
	public void pass(SJAbstractSocket[] sockets, Object obj) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.send(obj);
		}
	}
	
	public void copy(SJAbstractSocket[] sockets, Object obj) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.send(obj);
		}
	}
	
	public void copy(SJAbstractSocket[] sockets, int i) throws SJIOException	
	{
		for (SJAbstractSocket s : sockets)
		{
			s.sendInt(i);
		}
	}
	
	public Object receive(SJAbstractSocket[] sockets) throws SJIOException, ClassNotFoundException // Remove array in a compiler pass.
	{
		return sockets[0].receive();
	}

	public int receiveInt(SJAbstractSocket[] sockets) throws SJIOException
	{
		return sockets[0].receiveInt();
	}
	
	public Object receive(SJAbstractSocket[] sockets, String encoded) throws SJIOException, ClassNotFoundException
	{
		return sockets[0].receiveSession(encoded);
	}
	
	public boolean recurse(SJAbstractSocket[] sockets) throws SJIOException
	{
		return true;
	}
	
	public boolean outsync(SJAbstractSocket[] sockets, boolean cond) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.outsync(cond);
		}		
		
		return cond;
	}
	
	public boolean insync(SJAbstractSocket[] sockets) throws SJIOException
	{
		return sockets[0].insync();
	}
	
	public String outlabel(SJAbstractSocket[] sockets, String lab) throws SJIOException
	{
		for (SJAbstractSocket s : sockets)
		{
			s.outlabel(lab);
		}			
		
		return lab;
	}
	
	public String inlabel(SJAbstractSocket[] sockets) throws SJIOException
	{	
		return sockets[0].inlabel();
	}
	
	public boolean recursionEnter(SJAbstractSocket[] sockets)
	{
		return false;
	}
	
	public void recursionExit(SJAbstractSocket[] sockets)
	{

	}
	
	public void sendChannel(SJAbstractSocket[] sockets, SJService c) throws SJIOException // Channel objects should be immutable, so can be passed. // Remove array in a compiler pass
	{
		sockets[0].sendChannel(c);
	}
	
	public void delegateSession(SJAbstractSocket[] sockets, SJAbstractSocket s, String encoded) throws SJIOException
	{
		sockets[0].delegateSession(s, encoded);
	}
	
	public SJService receiveChannel(SJAbstractSocket[] sockets, String encoded) throws SJIOException, ClassNotFoundException
	{
		return sockets[0].receiveChannel(encoded);
	}
	
	public SJAbstractSocket receiveSession(SJAbstractSocket[] sockets, String encoded) throws SJIOException, ClassNotFoundException
	{
		return sockets[0].receiveSession(encoded);
	}	*/
}
