package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelector;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJSocket;
import sessionj.runtime.transport.*;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Random;

/**
 * @author Raymond
 *
 */
public class SJStreamTCPWithSelector implements SJTransport 
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJStreamTCPWithSelector";

	public static final int TCP_PORT_MAP_ADJUST = 0;
	
	protected static final boolean TCP_NO_DELAY = true;
	
	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 65535 - 1024;
    private final Selector selector = new Selector();

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		return new Acceptor(port);
	}
	
	public SJConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
		try 
		{
            SocketChannel sc = SocketChannel.open();
            sc.socket().setTcpNoDelay(TCP_NO_DELAY);
            sc.connect(new InetSocketAddress(hostName, port));
			
			return new Connection(sc); // Have to get I/O streams here for exception handling.
            
		} catch (IOException ioe) {
			throw new SJIOException(ioe);
		}
	}

    public SJSelector transportSelector() {
        return selector;
    }

    public boolean portInUse(int port)
	{
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException ignored) {
			return true;
		} finally {
			if (ss != null) try {
                ss.close();
            } catch (IOException ignored) { }
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
	
	public int sessionPortToSetupPort(int port) // Maybe can factor out to an abstract TCP-based parent class.
	{
		return port + TCP_PORT_MAP_ADJUST;
	}

    private class Selector implements SJSelector {
        public void registerAccept(SJServerSocket ss) {
        }

        public void registerSend(SJSocket s) {
            if (isOurSocket(s)) {
                
            }
        }

        private boolean isOurSocket(SJSocket s) {
            return s.getConnection().getTransportName().equals(getTransportName());
        }

        public void registerReceive(SJSocket s) {
            if (isOurSocket(s)) {

            }
        }

        public SJSocket select(int mask) throws SJIOException {
            return null;
        }
    }

    private class Acceptor implements SJConnectionAcceptor
    {
        private final ServerSocketChannel ssc;

        Acceptor(int port) throws SJIOException
        {
            try
            {
                ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(port)); // Didn't bother to explicitly check portInUse.
            }
            catch (IOException ioe)
            {
                throw new SJIOException(ioe);
            }
        }

        public SJConnection accept() throws SJIOException
        {
            if (!ssc.isBlocking()) throw new SJIOException("Server socket is in non-blocking mode, must use select");
            try {
                SocketChannel sc = ssc.accept();

                sc.socket().setTcpNoDelay(TCP_NO_DELAY);

                return new Connection(sc);
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
                ssc.close();
            }
            catch (IOException ignored) { }
        }

        public boolean interruptToClose()
        {
            return false;
        }

        public boolean isClosed()
        {
            return ssc.socket().isClosed(); // is this the same as !ssc.isOpen() ?
        }

        public String getTransportName()
        {
            return TRANSPORT_NAME;
        }
    }

    private class Connection extends SJStreamConnection
    {
        private final SocketChannel sc;

        Connection(SocketChannel sc) throws IOException {
            super(sc.socket().getInputStream(), sc.socket().getOutputStream());

            this.sc = sc;
        }

        public void disconnect() //throws SJIOException
        {
            super.disconnect();

            try {
                sc.close();
            }
            catch (IOException ignored) { }
        }

        public String getHostName()
        {
            return sc.socket().getInetAddress().getHostName();
        }

        public int getPort()
        {
            return sc.socket().getPort();
        }

        public int getLocalPort()
        {
            return sc.socket().getLocalPort();
        }

        public String getTransportName()
        {
            return TRANSPORT_NAME;
        }
    }

}
