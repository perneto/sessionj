package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.ByteBuffer;
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
    private static final int BUFFER_SIZE = 16384;

    public SJStreamTCPWithSelector() throws IOException {
        sel = SelectorProvider.provider().openSelector();
        selector = new Selector();
    }

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
        throw new UnsupportedOperationException("Blocking mode unsupported");
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
        private Map<SelectionKey, SJServerSocket> serverSockets =
            new HashMap<SelectionKey, SJServerSocket>();
        private Map<SelectionKey, SJSocket> sockets =
            new HashMap<SelectionKey, SJSocket>();

        public boolean registerAccept(SJServerSocket ss) throws IOException {
            if (supportsUs(ss)) {
                // We don't support early listening for server sockets managed by the
                // transport manager, so we start listening here, later than other transports.
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(ss.getLocalPort()));
                ssc.configureBlocking(false);
                SelectionKey key = ssc.register(sel, SelectionKey.OP_ACCEPT);
                serverSockets.put(key, ss);
                return true;
            }
            return false;
        }

        public boolean registerInput(SJSocket s) throws ClosedChannelException {
            SocketChannel sc = retrieveSocketChannel(s);
            if (sc != null) {
                SelectionKey key = sc.register(sel, SelectionKey.OP_READ);
                sockets.put(key, s);
                return true;
            }
            return false;
        }

        public boolean registerOutput(SJSocket s) throws ClosedChannelException {
            SocketChannel sc = retrieveSocketChannel(s);
            if (sc != null) {
                SelectionKey key = sc.register(sel, SelectionKey.OP_WRITE);
                sockets.put(key, s);
                return true;
            }
            return false;
        }

        public SJSocket select(int mask) throws SJIOException, SJIncompatibleSessionException {
            SJSocket s = null;
            try {
                sel.select(mask);
                Iterator it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();

                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            s = finishAccept(key);
                        } else {
                            s = lookupSocket(key);
                        }
                    }
                }
            } catch (IOException e) {
                throw new SJIOException(e);
            }
            assert s != null;
            return s;
        }

        private SJSocket lookupSocket(SelectionKey key) {
            SJSocket s = sockets.get(key);
            assert s != null;
            return s;
        }

        private SJSocket finishAccept(SelectionKey key) throws IOException, SJIOException, SJIncompatibleSessionException {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            SJServerSocket ss = serverSockets.get(key);

            SJAbstractSocket s = new SJAcceptingSocket(ss.getProtocol(), ss.getParameters());
            SJConnection conn = new Connection(socketChannel);
            SJRuntime.bindSocket(s, conn);
            SJRuntime.accept(s);
            return s;
        }

        public void close() throws SJIOException {
            try {
                sel.close();
            } catch (IOException e) {
                throw new SJIOException(e);
            }
        }

        private boolean supportsUs(SJServerSocket ss) {
            for (SJTransport t : ss.getParameters().getSessionTransports()) {
                if (isUs(t)) return true;
            }
            return false;
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
    }

    private class Connection implements SJConnection
    {
        private final SocketChannel sc;
        private final ByteBuffer buffer;
        Connection(SocketChannel sc) {
            this.sc = sc;
            buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        }

        public void disconnect()
        {
            try {
                sc.close();
            } catch (IOException ignored) { }
        }

        public void writeByte(byte b) throws SJIOException {
            buffer.clear();
            buffer.put(b);
            writeBufferContents();
        }

        public void writeBytes(byte[] bs) throws SJIOException {
            buffer.clear();
            buffer.put(bs);
            writeBufferContents();
        }

        private void writeBufferContents() throws SJIOException {
            try {
                sc.write(buffer);
                if (buffer.remaining() > 0)// TODO
                    System.err.println("Could not write all data");
            } catch (IOException e) {
                throw new SJIOException(e);
            }
        }

        public byte readByte() throws SJIOException {
            buffer.clear();
            try {
                sc.read(buffer);
            } catch (IOException e) {
                throw new SJIOException(e);
            }
            return buffer.get();
        }

        public void readBytes(byte[] bs) throws SJIOException {
            //readBytes
        }

        public void flush() throws SJIOException {
            // Do nothing; to do anything meaningful we would need to make
            // this blocking, and all the writeXXX() methods in the serializers call
            // this method.
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
