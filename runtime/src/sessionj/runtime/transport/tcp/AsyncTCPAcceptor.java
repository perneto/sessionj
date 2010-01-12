package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.runtime.transport.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

class AsyncTCPAcceptor extends AbstractWithTransport implements SJConnectionAcceptor {
    private final SelectingThread thread;
    // package-private so the selector can access it
    final ServerSocketChannel ssc;
    private static final Logger logger = SJRuntimeUtils.getLogger(AsyncTCPAcceptor.class);

    AsyncTCPAcceptor(SelectingThread thread, int port, SJTransport transport) throws IOException {
	    super(transport);
        this.thread = thread;
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

	private SJConnection createSJConnection(SocketChannel socketChannel) throws IOException {
        socketChannel.socket().setTcpNoDelay(SJManualTCP.TCP_NO_DELAY);
        thread.notifyAccepted(ssc, socketChannel);
        return new AsyncConnection(thread, socketChannel, getTransport());
    }
}
