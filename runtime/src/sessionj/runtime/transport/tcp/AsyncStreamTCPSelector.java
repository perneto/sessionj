package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 */
class AsyncStreamTCPSelector implements SJSelectorInternal {
    private final SelectingThread thread;
    private final String transportName;
    private final Map<SocketChannel, SJSocket> registeredInputs;
    private final Map<ServerSocketChannel, SJServerSocket> registeredAccepts;

    AsyncStreamTCPSelector(SelectingThread thread, String transportName) {
        this.thread = thread;
        this.transportName = transportName;
        registeredInputs = new HashMap<SocketChannel, SJSocket>();
        registeredAccepts = new HashMap<ServerSocketChannel, SJServerSocket>();
    }

    public boolean registerAccept(SJServerSocket ss) throws IOException {
        if (supportsUs(ss)) {
            // Async transports don't support early listening for server sockets (as managed by
            // SJTransportManager_c), so we start listening here, later than other transports.
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(ss.getLocalPort()));
            ssc.configureBlocking(false);
            thread.registerAccept(ssc);
            registeredAccepts.put(ssc, ss);
            return true;
        }
        return false;
    }

    public boolean registerInput(SJSocket s) {
        SocketChannel sc = retrieveSocketChannel(s);
        if (sc != null) {
            thread.registerInput(sc);
            registeredInputs.put(sc, s);
            return true;
        }
        return false;
    }

    public SJSocket select(int mask) throws SJIOException, SJIncompatibleSessionException {
        SJSocket s = null;
        if ((mask & INPUT) != 0) {
            SocketChannel sc = thread.selectForInput();
            assert registeredInputs.containsKey(sc);
            s = registeredInputs.get(sc);
        }

        if ((mask & ACCEPT) != 0 && s == null) {
            ServerSocketChannel sc = thread.selectForAccept();
            assert sc != null;
            try {
                s = finishAccept(sc);
            } catch (IOException e) {
                throw new SJIOException(e);
            }
        }

        assert s != null;
        return s;
    }

    private SJSocket finishAccept(ServerSocketChannel ssc) throws IOException, SJIOException, SJIncompatibleSessionException {
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setTcpNoDelay(SJStreamTCP.TCP_NO_DELAY);
        SJServerSocket ss = registeredAccepts.get(ssc);

        SJAbstractSocket s = new SJAcceptingSocket(ss.getProtocol(), ss.getParameters());
        SJConnection conn = new AsyncConnection(thread, socketChannel);
        SJRuntime.bindSocket(s, conn);
        SJRuntime.accept(s);
        return s;
    }

    public void close() throws SJIOException {

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
            sc = ((AsyncConnection) s.getConnection()).socketChannel();
            assert sc != null;
        }
        return sc;
    }

    private boolean isUs(SJTransport t) {
        return isOurName(t.getTransportName());
    }

    private boolean isOurSocket(SJSocket s) {
        return isOurName(s.getConnection().getTransportName());
    }

    private boolean isOurName(String name) {
        return name.equals(transportName);
    }
}
