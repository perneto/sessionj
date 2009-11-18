package sessionj.runtime.transport.tcp;

import sessionj.runtime.session.OngoingRead;
import sessionj.runtime.session.SJDeserializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
final class SelectingThread implements Runnable {
    private static final int BUFFER_SIZE = 16384;
    private final Selector selector;
    private final ByteBuffer readBuffer;
    private final SJDeserializer deserializer;

    private final Queue<ChangeRequest> pendingChangeRequests;

    private final ConcurrentHashMap<SocketChannel, Queue<byte[]>> readyInputs;
    /** Invariant: readyInputs.keySet() contains the same elements as readyForInput.
     *  The latter is there for implementation convenience */
    private final BlockingQueue<SocketChannel> readyForInput;
    private final BlockingQueue<ServerSocketChannel> readyForAccept;
    private final ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>> requestedOutputs;
    private static final Logger logger = Logger.getLogger(SelectingThread.class.getName());

    SelectingThread(SJDeserializer deserializer) throws IOException {
        this.deserializer = deserializer;
        selector = SelectorProvider.provider().openSelector();
        pendingChangeRequests = new ConcurrentLinkedQueue<ChangeRequest>();
        readyInputs = new ConcurrentHashMap<SocketChannel, Queue<byte[]>>();
        requestedOutputs = new ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>>();
        readyForInput = new LinkedBlockingQueue<SocketChannel>();
        readyForAccept = new LinkedBlockingQueue<ServerSocketChannel>();
        readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    void registerAccept(ServerSocketChannel ssc) {
        pendingChangeRequests.add(new ChangeRequest(ssc, ChangeAction.REGISTER, SelectionKey.OP_ACCEPT));
        // Don't touch selector registrations here, do all
        // selector handling in the selecting thread (as selector keys are not thread-safe)
    }

    void registerInput(SocketChannel sc) {
        readyInputs.put(sc, new LinkedBlockingQueue<byte[]>());
        // Don't change the order of these 2 statements: it's safe if we're interrupted after the first
        // one, as the channel is not registered with the selector yet. But if the registration requst is
        // added first, could have a NullPointerException
        pendingChangeRequests.add(new ChangeRequest(sc, ChangeAction.REGISTER, SelectionKey.OP_READ));
        // Don't touch selector registrations here, do all
        // selector handling in the selecting thread (as selector keys are not thread-safe)
    }

    public byte[] dequeueInput(SocketChannel sc) {
        return readyInputs.get(sc).remove();
    }

    public void enqueueOutput(SocketChannel sc, byte[] bs) {
        Queue<ByteBuffer> outputsForChan = requestedOutputs.get(sc);
        if (outputsForChan == null) {
            requestedOutputs.put(sc, new ConcurrentLinkedQueue<ByteBuffer>());
        }
        requestedOutputs.get(sc).add(ByteBuffer.wrap(bs));
        pendingChangeRequests.add(new ChangeRequest(sc, ChangeAction.CHANGEOPS, SelectionKey.OP_WRITE));
    }

    public void enqueueOutput(SocketChannel sc, byte b) {
        enqueueOutput(sc, new byte[]{b});
    }

    public void close(SelectableChannel sc) throws IOException {
        sc.keyFor(selector).cancel();
        sc.close();
    }

    public SocketChannel selectForInput() {
        return readyForInput.poll();
    }

    public ServerSocketChannel selectForAccept() {
        return readyForAccept.poll();
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                updateRegistrations();
                doSelect();
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    private void updateRegistrations() throws ClosedChannelException {
        while (!pendingChangeRequests.isEmpty()) {
            ChangeRequest reg = pendingChangeRequests.remove();
            switch (reg.changeAction) {
                case CHANGEOPS:
                    reg.chan.keyFor(selector).interestOps(reg.interestOps);
                    break;
                case REGISTER:
                    reg.chan.register(selector, reg.interestOps);
                    break;
            }

        }
    }

    private void doSelect() throws IOException {
        selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();

            if (key.isValid()) {
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                } else {
                    assert false : "Should not get here";
                }
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = requestedOutputs.get(socketChannel);

        boolean writtenInFull = true;

        // Write until there's no more data, or the socket's buffer fills up
        while (!queue.isEmpty() && writtenInFull) {
            ByteBuffer buf = queue.peek();
            socketChannel.write(buf);
            writtenInFull = buf.remaining() == 0;
            if (writtenInFull) queue.remove();
        }

        if (writtenInFull) {
            // We wrote away all data, so we're no longer interested
            // in writing on this socket. Switch back to waiting for data.
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            logger.log(Level.INFO, "Remote peer forcibly closed connection, closing channel and cancelling key", e);
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.cancel();
            socketChannel.close();            
            return;
        }

        readBuffer.position(0); // read() advances the position, so need to set it back to 0.
        decideIfMoreToRead(key, (SocketChannel) key.channel(),
                readBuffer.asReadOnlyBuffer() // just to be safe (shouldn't hurt speed)
        );
    }

    private void decideIfMoreToRead(SelectionKey key, SocketChannel sc, ByteBuffer bytes) {
        OngoingRead read = (OngoingRead) key.attachment();
        if (read == null) {
            read = deserializer.newOngoingRead();
            key.attach(read);
        }
        read.updatePendingInput(bytes);
        if (read.finished()) {
            readyInputs.get(sc).add(read.getCompleteInput());
            // order is important here: adding to readyForInput makes the read visible to select
            readyForInput.add(sc);
        }
    }

    private void accept(SelectionKey key) {
        try {
            readyForAccept.put((ServerSocketChannel) key.channel());
        } catch (InterruptedException e) {
            assert false : "Cannot happen, LinkedBlockingQueue is unbounded";
        }
    }

    private static class ChangeRequest {
        final SelectableChannel chan;
        final ChangeAction changeAction;
        final int interestOps;

        ChangeRequest(SelectableChannel chan, ChangeAction changeAction, int interestOps) {
            this.chan = chan;
            this.changeAction = changeAction;
            this.interestOps = interestOps;
        }
    }

    private enum ChangeAction { REGISTER, CHANGEOPS }

}
