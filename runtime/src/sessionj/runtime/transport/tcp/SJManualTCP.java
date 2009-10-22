package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelector;
import sessionj.runtime.net.SJSelectorInternal;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;
import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.util.Random;

class SJManualTCPAcceptor implements SJConnectionAcceptor
{
	private final ServerSocket ss;
	
	SJManualTCPAcceptor(int port) throws SJIOException
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
	
	public SJManualTCPConnection accept() throws SJIOException
	{
		try
		{
			if (ss == null)
			{
				throw new SJIOException("[" + getTransportName() + "] Connection acceptor not open.");
			}
			
			Socket s = ss.accept();
			
			s.setTcpNoDelay(SJManualTCP.TCP_NO_DELAY);
			
			return new SJManualTCPConnection(s, s.getInputStream(), s.getOutputStream());
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
		return SJManualTCP.TRANSPORT_NAME;
	}
}

class SJManualTCPConnection implements SJConnection 
{
	private final Socket s;
	
	private final DataOutputStream dos;
	private final DataInputStream dis;
	
	SJManualTCPConnection(Socket s, OutputStream os, InputStream is) throws SJIOException
	{
		this.s = s;
		
		//try
		{
            dos = new DataOutputStream(os);
            dis = new DataInputStream(is);
		}
		/*catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}*/
	}

	SJManualTCPConnection(Socket s, InputStream is, OutputStream os) throws SJIOException
	{
		this.s = s;
		
		//try
		{
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
		}
		/*catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}*/
	}
	
	public void disconnect() //throws SJIOException 
	{
		try { closeStream(dos); } catch (IOException ioe) { }		
		try { closeStream(dis); } catch (IOException ioe) { }
		
		try 
		{ 
			if (s != null)
			{
				s.close(); 			
			}
		}
		catch (IOException ioe) { }
	}

	public void writeByte(byte b) throws SJIOException
	{
		try
		{
			dos.writeByte(b);
			dos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public void writeBytes(byte[] bs) throws SJIOException
	{
		try
		{
			dos.write(bs);
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}

	public byte readByte() throws SJIOException
	{
		try
		{
			return dis.readByte();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}		
	}
	
	public void readBytes(byte[] bs) throws SJIOException
	{
		try
		{
			dis.readFully(bs);
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}			
	}
	
	public void flush() throws SJIOException
	{
		try
		{
			dos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}			
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
		return SJManualTCP.TRANSPORT_NAME;
	}
}

/**
 * @author Raymond
 *
 */
public class SJManualTCP implements SJTransport 
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJManualTCP";

	public static final int TCP_PORT_MAP_ADJUST = 1;

	protected static final boolean TCP_NO_DELAY = true;
	
	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 65535 - 1024;

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		return new SJManualTCPAcceptor(port);
	}
	
	/*public SJManualTCPConnection connect(SJServerIdentifier si) throws SJIOException
	{
		return connect(si.getHostName(), si.getPort());
	}*/
	
	public SJManualTCPConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
        Socket s = null;

		try {
			s = new Socket(hostName, port);
			s.setTcpNoDelay(TCP_NO_DELAY);
			
			return new SJManualTCPConnection(s, s.getOutputStream(), s.getInputStream()); // Have to get I/O streams here for exception handling.

		} catch (IOException ioe) {
			throw new SJIOException(ioe);
		} finally {
            if (s != null) try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
        }
	}

    public SJSelectorInternal transportSelector() {
        return null;
    }

    public boolean blockingModeSupported() {
        return true;
    }

    public boolean portInUse(int port)
	{
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException ioe) {
			return true;
		} finally {
			if (ss != null) try {
                ss.close();
            } catch (IOException ioe) {
                // ignore
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
	
	public String sessionHostToNegociationHost(String hostName)
	{
		return hostName;
	}
	
	public int sessionPortToSetupPort(int port)
	{
		return port + TCP_PORT_MAP_ADJUST; // To be compatible with SJStreamTCP.
	}
}
