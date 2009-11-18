package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import java.util.logging.Level;

class AsyncTCPAcceptor implements SJConnectionAcceptor {
    private final SelectingThread thread;
    // package-private so the selector can access it
    final ServerSocketChannel ssc;
    private static final Logger logger = Logger.getLogger(AsyncTCPAcceptor.class.getName());

    AsyncTCPAcceptor(SelectingThread thread, int port) throws IOException {
        this.thread = thread;
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));
    }

    public SJConnection accept() throws SJIOException {
        try {
            thread.waitReadyAccept(ssc);
            return finishAccept(ssc);
        } catch (IOException e) {
            throw new SJIOException(e);
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        }
    }

    public void close() {
        thread.cancelAccept(ssc);
        try {
            ssc.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, 
                "Could not close server socket channel listening on port: " 
                    + ssc.socket().getLocalPort(), e);
        }

    }

    public boolean interruptToClose() {
        return false;
    }

    public boolean isClosed() {
        return false;
    }

    public String getTransportName() {
        return SJAsyncManualTCP.TRANSPORT_NAME;
    }


    private SJConnection finishAccept(ServerSocketChannel ssc) throws IOException {
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setTcpNoDelay(SJStreamTCP.TCP_NO_DELAY);
        thread.notifyAccepted(ssc, socketChannel);
        return new AsyncConnection(thread, socketChannel);
    }
}
