package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelector;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJStreamConnection;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Random;

class SJStreamTCPAcceptor implements SJConnectionAcceptor
{
	private final ServerSocket ss;
    private final ServerSocketChannel ssc;
	
	SJStreamTCPAcceptor(int port) throws SJIOException
	{
		try
		{
            ssc = ServerSocketChannel.open();
            ss = ssc.socket();
			ss.bind(new InetSocketAddress(port)); // Didn't bother to explicitly check portInUse.
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public SJStreamTCPConnection accept() throws SJIOException
	{
		try
		{
			if (ss == null)
			{
				throw new SJIOException('[' + getTransportName() + "] Connection acceptor not open.");
			}
			
			Socket s = ss.accept();
			
			s.setTcpNoDelay(SJStreamTCP.TCP_NO_DELAY);
			
			return new SJStreamTCPConnection(s, s.getInputStream(), s.getOutputStream());
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
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
		catch (IOException ioe) { }
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
		return SJStreamTCP.TRANSPORT_NAME;
	}
}

class SJStreamTCPConnection extends SJStreamConnection
{
	private final Socket s;
	
	protected SJStreamTCPConnection(Socket s, OutputStream os, InputStream is) throws SJIOException
	{
		super(os, is);
		
		this.s = s;
	}

	protected SJStreamTCPConnection(Socket s, InputStream is, OutputStream os) throws SJIOException
	{
		super(is, os);
		
		this.s = s;
	}
	
	public void disconnect() //throws SJIOException 
	{
		super.disconnect();				
		
		try 
		{ 
			if (s != null)
			{
				s.close(); 			
			}
		}
		catch (IOException ioe) { }
	}

	public String getHostName()
	{
		return s.getInetAddress().getHostName();
	}
	
	public int getPort()
	{
		return s.getPort();
	}
	
	public int getLocalPort()
	{
		return s.getLocalPort();
	}
	
	public String getTransportName()
	{
		return SJStreamTCP.TRANSPORT_NAME;
	}
}

/**
 * @author Raymond
 *
 */
public class SJStreamTCP implements SJTransport 
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJStreamTCP";

	public static final int TCP_PORT_MAP_ADJUST = 0;
	
	protected static final boolean TCP_NO_DELAY = true;
	
	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 65535 - 1024;

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		return new SJStreamTCPAcceptor(port);
	}
	
	/*public SJStreamTCPConnection connect(SJServerIdentifier si) throws SJIOException
	{
		return connect(si.getHostName(), si.getPort());
	}*/
	
	public SJStreamTCPConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
		try 
		{
			Socket s = new Socket(hostName, port);
			
			s.setTcpNoDelay(TCP_NO_DELAY);
			
			return new SJStreamTCPConnection(s, s.getOutputStream(), s.getInputStream()); // Have to get I/O streams here for exception handling.
		} 
		catch (IOException ioe) 
		{
			throw new SJIOException(ioe);
		}
	}

    public SJSelector transportSelector() {
        return null;
    }

    public boolean portInUse(int port)
	{
		ServerSocket ss = null;
		
		try
		{
			ss = new ServerSocket(port);
		}
		catch (IOException ignored)
		{
			return true;
		}
		finally
		{
			if (ss != null) 
			{
				try
				{
					ss.close();
				}
				catch (IOException ignored) { }					
			}
		}
		
		return false;
	}
	
	public int getFreePort() throws SJIOException
	{
		int start = new Random().nextInt(PORT_RANGE);
		int seed = start + 1;
		
		for (int port = seed % PORT_RANGE; port != start; port = seed++ % PORT_RANGE)  
		{
			if (!portInUse(port + LOWER_PORT_LIMIT))
			{
				return port + LOWER_PORT_LIMIT;
			}
		}
		
		throw new SJIOException('[' + getTransportName() + "] No free port available.");
	}
	
	public String getTransportName()
	{
		return TRANSPORT_NAME;
	}
	
	public String sessionHostToSetupHost(String hostName)
	{
		return hostName;
	}
	
	public int sessionPortToSetupPort(int port) // Maybe can factor out to an abstract TCP-based parent class.
	{
		return port + TCP_PORT_MAP_ADJUST;
	}	
}
