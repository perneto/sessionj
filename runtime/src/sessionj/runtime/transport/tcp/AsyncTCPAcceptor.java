package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class AsyncTCPAcceptor implements SJConnectionAcceptor {
    private final SelectingThread thread;
    // package-private so the selector can access it
    ServerSocketChannel ssc = null;

    AsyncTCPAcceptor(SelectingThread thread) {
        this.thread = thread;
    }

    public SJConnection accept() throws SJIOException {
        try {
            ssc = thread.dequeueReadyAccept();
            return finishAccept(ssc);
        } catch (IOException e) {
            throw new SJIOException(e);
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        }
    }

    public void close() {
        if (ssc != null)
            thread.cancelAccept(ssc);

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
