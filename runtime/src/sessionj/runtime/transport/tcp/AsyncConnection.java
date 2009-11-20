package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

class AsyncConnection implements SJConnection
{
    private final SelectingThread thread;
    private final SocketChannel sc;

    AsyncConnection(SelectingThread thread, SocketChannel sc) {
        this.thread = thread;
        this.sc = sc;
    }

    public void disconnect()
    {
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
    public byte readByte() throws SJIOException {
        ByteBuffer input = thread.peekAtInputQueue(sc);
        if (input.remaining() == 1) thread.dequeueFromInputQueue(sc);
        return input.get();
    }

    /**
     * Non-blocking read from the connection.
     * @throws NullPointerException If called when no data is ready on this connection (ie. not after a select call).
     */
    public void readBytes(byte[] bs) throws SJIOException {
        ByteBuffer input = thread.peekAtInputQueue(sc);
        input.get(bs, 0, bs.length);
        if (input.remaining() == 0) thread.dequeueFromInputQueue(sc);
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

    public SocketChannel socketChannel() {
        return sc;
    }
}
