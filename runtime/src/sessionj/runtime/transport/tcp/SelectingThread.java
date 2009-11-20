package sessionj.runtime.transport.tcp;

import sessionj.runtime.session.OngoingRead;
import sessionj.runtime.session.SJDeserializer;
import static sessionj.runtime.transport.tcp.SelectingThread.ChangeAction.*;
import sessionj.util.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import static java.nio.channels.SelectionKey.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Queue;
import java.util.Iterator;
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

    private final ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>> readyInputs;
    private final BlockingQueue<Object> readyForSelect;
    private final ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>> accepted;
    private final ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>> requestedOutputs;
    private static final Logger logger = Logger.getLogger(SelectingThread.class.getName());
    private static final boolean DEBUG = false;

    SelectingThread(SJDeserializer deserializer) throws IOException {
        this.deserializer = deserializer;
        selector = SelectorProvider.provider().openSelector();
        pendingChangeRequests = new ConcurrentLinkedQueue<ChangeRequest>();
        readyInputs = new ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>>();
        requestedOutputs = new ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>>();
        readyForSelect = new LinkedBlockingQueue<Object>();
        accepted = new ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>>();
        readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    synchronized void registerAccept(ServerSocketChannel ssc) {
        assert !accepted.containsKey(ssc) : "The channel " + ssc + " has already been registered";
        accepted.put(ssc, new LinkedBlockingQueue<SocketChannel>());
        pendingChangeRequests.add(new ChangeRequest(ssc, REGISTER, OP_ACCEPT));
        // Don't touch selector registrations here, do all
        // selector handling in the selecting thread (as selector keys are not thread-safe)
        selector.wakeup(); // Essential for the first selector registration:
        // before that, the thread is blocked on select with no channel registered,
        // hence sleeping forever.
    }

    synchronized void registerInput(SocketChannel sc) {
        readyInputs.put(sc, new LinkedBlockingQueue<ByteBuffer>());
        // Don't change the order of these 2 statements: it's safe if we're interrupted after the first
        // one, as the channel is not registered with the selector yet. But if the registration requst is
        // added first, could have a NullPointerException
        pendingChangeRequests.add(new ChangeRequest(sc, REGISTER, OP_READ));
        // Don't touch selector registrations here, do all
        // selector handling in the selecting thread (as selector keys are not thread-safe)
        selector.wakeup();
    }

    /**
     * Non-blocking, as it should be used right after a select
     * call.
     * @param sc The channel for which to get an input message.
     * @return A complete message (according to the OngoingRead instance obtained from the serializer)
     * @throws java.util.NoSuchElementException if no message is ready for reading.
     */
    public ByteBuffer dequeueFromInputQueue(SocketChannel sc) {
        return readyInputs.get(sc).remove();
    }
    
    public ByteBuffer peekAtInputQueue(SocketChannel sc) {
        return readyInputs.get(sc).peek();
    }

    public synchronized void enqueueOutput(SocketChannel sc, byte[] bs) {
        Queue<ByteBuffer> outputsForChan = requestedOutputs.get(sc);
        if (outputsForChan == null) {
            outputsForChan = new ConcurrentLinkedQueue<ByteBuffer>();
            requestedOutputs.put(sc, outputsForChan);
        }
        outputsForChan.add(ByteBuffer.wrap(bs));
        debug("Enqueued write on: " + sc + " of: " + bs.length + " bytes");
        pendingChangeRequests.add(new ChangeRequest(sc, CHANGEOPS, OP_WRITE));
        selector.wakeup();
    }

    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    public void enqueueOutput(SocketChannel sc, byte b) {
        enqueueOutput(sc, new byte[]{b});
    }

    public void close(SelectableChannel sc) {
        // ConcurrentLinkedQueue - no synchronization needed
        pendingChangeRequests.add(new ChangeRequest(sc, CLOSE, -1));
    }

    public SocketChannel takeAccept(ServerSocketChannel ssc) throws InterruptedException {
        // ConcurrentHashMap and LinkedBlockingQueue, both thread-safe,
        // and no need for atomicity here
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        debug("Waiting for accept on server socket: " + ssc + " in queue: " + queue);
        return queue.take();
    }

    public void notifyAccepted(ServerSocketChannel ssc, SocketChannel socketChannel) {
        readyForSelect.add(new Pair<ServerSocketChannel, SocketChannel>(ssc, socketChannel));
    }

    public Object dequeueChannelForSelect() throws InterruptedException {
        return readyForSelect.take();
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                updateRegistrations();
                doSelect();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error in selecting loop", e);
            }
        }
    }

    private void updateRegistrations() throws IOException {
        while (!pendingChangeRequests.isEmpty()) {
            ChangeRequest req = pendingChangeRequests.remove();
            req.execute(selector);
        }
    }

    private void doSelect() throws IOException {
        selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            // This seems important: without it, we get notified several times
            // for the same event, resulting in eg. NPEs on accept.
            it.remove();

            if (key.isValid()) {
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                } else { // isConnectable
                    assert false : "Should not get here: readyOps = " + key.readyOps();
                }
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = requestedOutputs.get(socketChannel);

        boolean writtenInFull = true;
        debug("Writing data on: " + socketChannel);
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
            key.interestOps(OP_READ);
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
            logger.log(Level.INFO, 
                "Remote peer forcibly closed connection, closing channel and cancelling key", e);
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

        // socketChannel.read() advances the position, so need to set it back to 0.
        // also set the limit to the previous position, so that the next
        // method will not try to read past what was read from the socket.
        readBuffer.flip(); 
        
        decideIfMoreToRead(key, (SocketChannel) key.channel(),
                readBuffer.asReadOnlyBuffer() // just to be safe (shouldn't hurt speed)
        );
    }

    private void decideIfMoreToRead(SelectionKey key, SocketChannel sc, ByteBuffer bytes) {
        OngoingRead read = (OngoingRead) key.attachment();
        while (bytes.remaining() != 0) {
            if (read == null) read = attachNewOngoingRead(key);
            
            read.updatePendingInput(bytes);
            
            if (read.finished()) {
                readyInputs.get(sc).add(read.getCompleteInput());
                // order is important here: adding to readyForSelect makes the read visible to select
                readyForSelect.add(sc);
                
                read = attachNewOngoingRead(key);
            }
        }
    }

    private OngoingRead attachNewOngoingRead(SelectionKey key) {
        OngoingRead read = deserializer.newOngoingRead();
        key.attach(read);
        return read;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        debug("Enqueuing accepted socket for server socket: " + ssc + " in queue: " + queue);
        queue.add(socketChannel);
    }

    private static class ChangeRequest {
        private final SelectableChannel chan;
        private final ChangeAction changeAction;
        private final int interestOps;

        ChangeRequest(SelectableChannel chan, ChangeAction changeAction, int interestOps) {
            this.chan = chan;
            this.changeAction = changeAction;
            this.interestOps = interestOps;
        }

        void execute(Selector selector) throws IOException {
            changeAction.execute(selector, this);
        }
    }

    enum ChangeAction {
        REGISTER {
            void execute(Selector selector, ChangeRequest req) throws ClosedChannelException {
                req.chan.register(selector, req.interestOps);
            }
        }, CHANGEOPS {
            void execute(Selector selector, ChangeRequest req) {
                debug("Changing ops for: " + req.chan + " to: " + req.interestOps);
                req.chan.keyFor(selector).interestOps(req.interestOps);
            }
        }, CLOSE {
            void execute(Selector selector, ChangeRequest req) throws IOException {
                // Implicitly cancels all existing selection keys for that channel (see javadoc).
                req.chan.close();
            }
        };
        abstract void execute(Selector selector, ChangeRequest changeRequest) throws IOException;
    }

}
