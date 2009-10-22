package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelectorInternal;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJSocket;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * @author Raymond
 *
 */
public class SJStreamTCPWithSelector implements SJTransport 
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJStreamTCPWithSelector";

	public static final int TCP_PORT_MAP_ADJUST = 2;
	
	protected static final boolean TCP_NO_DELAY = true;
	
	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 65535 - 1024;
    private final Selector selector;
    private final java.nio.channels.Selector sel;

    public SJStreamTCPWithSelector() throws IOException {
        sel = SelectorProvider.provider().openSelector();
        selector = new Selector();
    }

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		Acceptor acceptor = new Acceptor(port);
	    selector.preregisterAcceptor(port, acceptor);
	    return acceptor;
    }
	
	public SJConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
        try
        {
            Socket s = new Socket(hostName, port);

            s.setTcpNoDelay(TCP_NO_DELAY);

            return new SJStreamTCPConnection(s, s.getInputStream(), s.getOutputStream()); // Have to get I/O streams here for exception handling.
        }
        catch (IOException ioe)
        {
            throw new SJIOException(ioe);
        }
	}

    public SJSelectorInternal transportSelector() {
        return selector;
    }

    public boolean blockingModeSupported() {
        return true;
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
	
	public int sessionPortToSetupPort(int port) 
	{
		return port + TCP_PORT_MAP_ADJUST;
	}

    private class Selector implements SJSelectorInternal {
        private final Map<Integer, Acceptor> acceptors = new HashMap<Integer, Acceptor>();

        public boolean registerAccept(SJServerSocket ss) throws ClosedChannelException {
            ServerSocketChannel ssc = retrieveServerChannel(ss);
            if (ssc != null) {
                ssc.register(sel, SelectionKey.OP_ACCEPT);
            }
            return false;
        }

        public boolean registerInput(SJSocket s) throws ClosedChannelException {
            SocketChannel sc = retrieveSocketChannel(s);
            if (sc != null) {
                sc.register(sel, SelectionKey.OP_READ);
                return true;
            }
            return false;
        }

        public boolean registerOutput(SJSocket s) throws ClosedChannelException {
            SocketChannel sc = retrieveSocketChannel(s);
            if (sc != null) {
                sc.register(sel, SelectionKey.OP_WRITE);
                return true;
            }
            return false;
        }

        public SJSocket select(int mask) throws SJIOException {
            SJSocket s = null;
            try {
                sel.select(mask);
                Iterator it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();

                    if (key.isValid()) {
                        // TODO: check if this works with the delegation protocol 
                        //s = new SJAcceptingSocket(
                        //    new SJProtocol(SJRuntime.encode()),

                        //);
                    }
                }
            } catch (IOException e) {
                throw new SJIOException(e);
            }
            assert s != null;
            return s;
        }

        private ServerSocketChannel retrieveServerChannel(SJServerSocket ss) {
            ServerSocketChannel ssc = null;
            for (SJTransport t : ss.getParameters().getSessionTransports()) {
                if (isUs(t)) {
                    Acceptor forSocket = acceptors.get(ss.getLocalPort());
                    ssc = forSocket.ssc;
                    assert ssc != null;
                }
            }
            return ssc;
        }

        private SocketChannel retrieveSocketChannel(SJSocket s) {
            SocketChannel sc = null;
            if (isOurSocket(s)) {
                sc = ((Connection) s.getConnection()).sc;
                assert sc != null;
            }
            return sc;
        }

        private boolean isUs(SJTransport t) { return isOurName(t.getTransportName()); }

        private boolean isOurSocket(SJSocket s) {
            return isOurName(s.getConnection().getTransportName());
        }

        private boolean isOurName(String name) { return name.equals(getTransportName()); }

        void preregisterAcceptor(int port, Acceptor acceptor) {
            acceptors.put(port, acceptor);
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
                ssc.configureBlocking(false);
                ssc.socket().bind(new InetSocketAddress(port)); // Didn't bother to explicitly check portInUse.
            }
            catch (IOException ioe)
            {
                throw new SJIOException(ioe);
            }
        }

        public SJConnection accept() throws SJIOException
        {
            throw new UnsupportedOperationException
                ("Transport "+getTransportName()+" does not support blocking mode");
        }

        public void close()
        {
            try
            {
                ssc.keyFor(sel).cancel(); // does close do this automatically?
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
            return !ssc.isOpen();
            //ssc.socket().isClosed(); is this the same as !ssc.isOpen() ?
        }

        public String getTransportName()
        {
            return TRANSPORT_NAME;
        }
    }

    private class Connection implements SJConnection
    {
        private final SocketChannel sc;

        Connection(SocketChannel sc) {
            this.sc = sc;
        }

        public void disconnect()
        {
            try {
                sc.close();
            } catch (IOException ignored) { }
        }

        public void writeByte(byte b) throws SJIOException {
        }

        public void writeBytes(byte[] bs) throws SJIOException {
        }

        public byte readByte() throws SJIOException {
            return 0;
        }

        public void readBytes(byte[] bs) throws SJIOException {
        }

        public void flush() throws SJIOException {
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
