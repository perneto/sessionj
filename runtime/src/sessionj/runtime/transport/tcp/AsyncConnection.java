package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJTransport;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

class AsyncConnection implements SJConnection
{
    private final SelectingThread thread;
    private final SocketChannel sc;
    private final SJTransport transport;
    private static final Logger log = Logger.getLogger(AsyncConnection.class.getName());

    AsyncConnection(SelectingThread thread, SocketChannel sc, SJTransport transport) {
        this.thread = thread;
        this.sc = sc;
        this.transport = transport;
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
        log.finest("Input: " + input);
        if (input == null) {
            throw new SJIOException("No available inputs on connection: " + this);
            // HACK to make the close protocol work, even if it doesn't call select
            // Update: the hack wreaks havoc for the clients connecting after the first one.
            // The message from the close protocol seems to arrive quickly enough in practice...
            
            /*
            try {
                input = thread.takeFromInputQueue(sc);
                log.finer("Unblocked after take, input: " + input);
            } catch (InterruptedException e) {
                throw new SJIOException(e);
            }
            */
        }
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

    public String getTransportName()
    {
        return SJAsyncManualTCP.TRANSPORT_NAME;
    }

    public SJTransport getTransport() {
        return transport;
    }

    SocketChannel socketChannel() {
        return sc;
    }
    
    @Override
    public String toString() {
        return "AsyncConnection{" + sc + '}';
    }
}
