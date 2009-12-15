package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

class AsyncTCPAcceptor implements SJConnectionAcceptor {
    private final SelectingThread thread;
    // package-private so the selector can access it
    final ServerSocketChannel ssc;
    private static final Logger logger = SJRuntimeUtils.getLogger(AsyncTCPAcceptor.class);
    private final SJTransport transport;

    AsyncTCPAcceptor(SelectingThread thread, int port, SJTransport transport) throws IOException {
        this.thread = thread;
        this.transport = transport;
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));
        thread.registerAccept(ssc);
    }

    public SJConnection accept() throws SJIOException {
        try {
            SocketChannel sc = thread.takeAccept(ssc);
            logger.finer("Accepted: " + sc);
            return createSJConnection(sc);
        } catch (IOException e) {
            throw new SJIOException(e);
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        }
    }

    public void close() {
        thread.close(ssc);
    }

    public boolean interruptToClose() {
        return false;
    }

    public boolean isClosed() {
        return !ssc.isOpen();
    }

    public String getTransportName() {
        return SJAsyncManualTCP.TRANSPORT_NAME;
    }


    private SJConnection createSJConnection(SocketChannel socketChannel) throws IOException {
        socketChannel.socket().setTcpNoDelay(SJManualTCP.TCP_NO_DELAY);
        thread.notifyAccepted(ssc, socketChannel);
        return new AsyncConnection(thread, socketChannel, transport);
    }
}
