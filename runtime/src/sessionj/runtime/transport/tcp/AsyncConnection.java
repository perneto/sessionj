package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.AbstractSJConnection;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.SJRuntimeUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class AsyncConnection extends AbstractSJConnection
{
    private final SelectingThread thread;
    // HACK: to allow selection of the correct connection when more than one is available in SJServerSocket.nextConnection()
    public final SocketChannel sc;
    private static final Logger log = SJRuntimeUtils.getLogger(AsyncConnection.class);

    AsyncConnection(SelectingThread thread, SocketChannel sc, SJTransport transport) {
        super(transport);
        this.thread = thread;
        this.sc = sc;
    }

    public void disconnect() {
        log.fine("Closing channel: " + sc);
        thread.close(sc);
    }

    public void writeByte(byte b) throws SJIOException {
        thread.enqueueOutput(sc, b);
    }

    public void writeBytes(byte[] bs) throws SJIOException {
        thread.enqueueOutput(sc, bs);
    }

    /**
     * Non-blocking read from the connection.
     * @throws NullPointerException If called when no data is ready on this connection (ie. not after a select call).
     */
    public synchronized byte readByte() throws SJIOException {
        ByteBuffer input = checkAndDequeue(1);
        
        return input.get();
    }

    private ByteBuffer checkAndDequeue(int remaining) throws SJIOException {
        ByteBuffer input = thread.peekAtInputQueue(sc);
        if (input == null) {
            throw new SJIOException("No available inputs on connection: " + this);
        }
        
        log.finest("Returning input: " + input);

        if (input.remaining() == remaining)
            thread.dequeueFromInputQueue(sc);
        
        return input;
    }
    
    /**
     * Non-blocking read from the connection.
     * @throws NullPointerException If called when no data is ready on this connection (ie. not after a select call).
     */
    public synchronized void readBytes(byte[] bs) throws SJIOException {
        ByteBuffer input = checkAndDequeue(bs.length);
        input.get(bs, 0, bs.length);
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

    SocketChannel socketChannel() {
        return sc;
    }

    @Override
    public boolean supportsBlocking() {
        return false;
    }

    @Override
    public String toString() {
        return "AsyncConnection{" + sc + '}';
    }
}
