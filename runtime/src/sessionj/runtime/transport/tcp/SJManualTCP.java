package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.*;
import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class SJManualTCPAcceptor implements SJConnectionAcceptor
{
	private final ServerSocket ss;
    private final SJTransport transport;

    SJManualTCPAcceptor(int port, SJTransport transport) throws SJIOException
	{
        this.transport = transport;
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
			
			return new SJManualTCPConnection(s, s.getInputStream(), s.getOutputStream(), transport);
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
		return SJManualTCP.TRANSPORT_NAME;
	}
}

class SJManualTCPConnection extends AbstractSJConnection 
{
	private final Socket s;
	
	private final DataOutputStream dos;
	private final DataInputStream dis;

    SJManualTCPConnection(Socket s, OutputStream os, InputStream is, SJTransport transport) {
        super(transport);
        this.s = s;
        dos = new DataOutputStream(os);
        dis = new DataInputStream(is);
	}

	SJManualTCPConnection(Socket s, InputStream is, OutputStream os, SJTransport transport) {
        super(transport);
        this.s = s;
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);		
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
}

/**
 * @author Raymond
 *
 */
public class SJManualTCP extends AbstractSJTransport
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJManualTCP";

	public static final int TCP_PORT_MAP_ADJUST = 20; // FIXME: sort out port potential mapping clashes better.

	protected static final boolean TCP_NO_DELAY = true;
	
    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		return new SJManualTCPAcceptor(port, this);
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
			
			return new SJManualTCPConnection(s, s.getOutputStream(), s.getInputStream(), this); // Have to get I/O streams here for exception handling.

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

    public boolean portInUse(int port)
	{
        return SJStreamTCP.isTCPPortInUse(port);
	}
	
	public int getFreePort() throws SJIOException
	{
        return SJStreamTCP.getFreeTCPPort(getTransportName());
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
