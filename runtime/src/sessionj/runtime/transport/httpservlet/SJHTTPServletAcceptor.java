package sessionj.runtime.transport.httpservlet;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.channels.SelectableChannel;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;

import static sessionj.runtime.util.SJRuntimeUtils.*;

public class SJHTTPServletAcceptor implements SJConnectionAcceptor
{	
	private ServerSocket ss;
	
	public SJHTTPServletAcceptor(int port) throws SJIOException
	{
		try
		{
			ss = new ServerSocket(port); // Didn't bother to explicitly check portInUse.
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public SJConnection accept() throws SJIOException{
		
		try
		{
			if (ss == null)
			{
				throw new SJIOException("[" + getTransportName() + "] Connection acceptor not open.");
			}
			
			Socket s = ss.accept(); 
			
			return new SJHTTPServletConnection(s, s.getInputStream(), s.getOutputStream());
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}

    public SelectableChannel acceptSelectableChannel() {
        throw new UnsupportedOperationException("TODO");
    }

    public void close()
	{	
		try 
		{ 
			if (ss != null)
			{
				ss.close(); 
			}
		}
		catch (IOException ioe) 
		{
						
		}
	}
	
	public boolean interruptToClose()
	{	
		return false;
	}
	
	public boolean isClosed()
	{		
		return ss.isClosed();
	}
	
	public String getTransportName()
	{	
		return SJHTTPServlet.TRANSPORT_NAME;
	}
}
