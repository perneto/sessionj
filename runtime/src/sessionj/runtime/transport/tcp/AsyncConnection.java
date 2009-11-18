package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;

import java.nio.channels.SocketChannel;

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

    public byte readByte() throws SJIOException {
        byte[] input = thread.dequeueInput(sc);
        assert input.length == 1;
        return input[0]; // TODO put back in queue if not consumed completely
    }

    public void readBytes(byte[] bs) throws SJIOException {
        byte[] input = thread.dequeueInput(sc);
        assert input.length == bs.length; // TODO put back in queue if not consumed completely
        System.arraycopy(input, 0, bs, 0, input.length); // FIXME: change signature to return byte[]
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
