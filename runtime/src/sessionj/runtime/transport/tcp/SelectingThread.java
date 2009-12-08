package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.session.OngoingRead;
import sessionj.runtime.session.SJDeserializer;
import static sessionj.runtime.transport.tcp.SelectingThread.ChangeAction.*;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.util.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import static java.nio.channels.SelectionKey.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SelectingThread implements Runnable {
    private static final int BUFFER_SIZE = 16384;
    private final Selector selector;
    private final ByteBuffer readBuffer;

    private final Queue<ChangeRequest> pendingChangeRequests;

    private final ConcurrentHashMap<SocketChannel, BlockingQueue<ByteBuffer>> readyInputs;
    private final BlockingQueue<Object> readyForSelect;
    private final ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>> accepted;
    private final ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>> requestedOutputs;
    private static final Logger log = SJRuntimeUtils.getLogger(SelectingThread.class);
    private final Map<SocketChannel, SJDeserializer> deserializers;
    private final Map<SocketChannel, Collection<AsyncManualTCPSelector>> interestedSelectors;

    SelectingThread() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        pendingChangeRequests = new ConcurrentLinkedQueue<ChangeRequest>();


        // TODO: use a WeakHashMap
        readyInputs = new ConcurrentHashMap<SocketChannel, BlockingQueue<ByteBuffer>>();
        requestedOutputs = new ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>>();
        
        readyForSelect = new LinkedBlockingQueue<Object>();
        
        accepted = new ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>>();
        deserializers = new HashMap<SocketChannel, SJDeserializer>();
        
        // TODO: use a WeakHashMap
        interestedSelectors = new HashMap<SocketChannel, Collection<AsyncManualTCPSelector>>();
        
        readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    synchronized void registerAccept(ServerSocketChannel ssc) {
        assert !accepted.containsKey(ssc) : "The channel " + ssc + " has already been registered";
        accepted.put(ssc, new LinkedBlockingQueue<SocketChannel>());
        pendingChangeRequests.add(new ChangeRequest(ssc, REGISTER, OP_ACCEPT));
        // Don't touch selector registrations here, do all
        // selector handling in the selecting thread (as selector keys are not thread-safe)
        
        selector.wakeup(); 
        // Essential for the first selector registration:
        // before that, the thread is blocked on select with no channel registered,
        // hence sleeping forever.
    }

    synchronized void registerInput(SocketChannel sc, SJDeserializer deserializer, AsyncManualTCPSelector sel) {
        if (!readyInputs.containsKey(sc)) {
            log.finer("New registration for input: " + sc + ", deserializer: " + deserializer);
            readyInputs.put(sc, new LinkedBlockingQueue<ByteBuffer>());
            deserializers.put(sc, deserializer);
            Set<AsyncManualTCPSelector> set = new HashSet<AsyncManualTCPSelector>();
            set.add(sel);
            interestedSelectors.put(sc, set);
            // Don't change the order of these 2 statements: it's safe if we're interrupted after the first
            // one, as the channel is not registered with the selector yet. But if the registration requst is
            // added first, could have a NullPointerException
            pendingChangeRequests.add(new ChangeRequest(sc, REGISTER, OP_READ));
            // Don't touch selector registrations here, do all
            // selector handling in the selecting thread (as selector keys are not thread-safe)
            selector.wakeup();
        } else {
            // Not changing interest ops for now, to avoid losing/delaying writes. The interest ops
            // will be changed to OP_READ when everything has been written (see write() method).
            log.finer("Asked for input registration, but channel already registered. Channel: " + sc);
            interestedSelectors.get(sc).add(sel);
            selector.wakeup();
        }
    }
    
    synchronized void deregisterInput(SocketChannel sc, AsyncManualTCPSelector sel) {
        Collection<AsyncManualTCPSelector> coll = interestedSelectors.get(sc);
        coll.remove(sel);
        if (coll.isEmpty()) {
            log.finer("Deregistering channel: " + sc);
            readyInputs.remove(sc);
            pendingChangeRequests.add(new ChangeRequest(sc, CANCEL, -1));
            selector.wakeup();
        }
    }

    /**
     * Non-blocking, as it should be used right after a select
     * call.
     * @param sc The channel for which to get an input message.
     * @return A complete message (according to the OngoingRead instance obtained from the serializer)
     * @throws java.util.NoSuchElementException if no message is ready for reading.
     */
    public ByteBuffer dequeueFromInputQueue(SocketChannel sc) {
        log.finer("Dequeueing input from: " + sc);
        return readyInputs.get(sc).remove();
    }
    
    public ByteBuffer peekAtInputQueue(SocketChannel sc) {
        log.finest("Peeking at inputs for: " + sc);
        ByteBuffer b;
        try {
            b = readyInputs.get(sc).peek();
        } catch (RuntimeException e) {
            log.severe("foo" + e);
            e.printStackTrace();
            throw e;
        }
        log.finest("Found input for: " + sc + ", input: " + b);
        return b;
    }

    public synchronized void enqueueOutput(SocketChannel sc, byte[] bs) {
        Queue<ByteBuffer> outputsForChan = requestedOutputs.get(sc);
        if (outputsForChan == null) {
            outputsForChan = new ConcurrentLinkedQueue<ByteBuffer>();
            requestedOutputs.put(sc, outputsForChan);
        }
        outputsForChan.add(ByteBuffer.wrap(bs));
        log.finer("Enqueued write on: " + sc + " of: " + bs.length + " bytes");
        pendingChangeRequests.add(new ChangeRequest(sc, CHANGEOPS, OP_WRITE));
        selector.wakeup();
    }

    public void enqueueOutput(SocketChannel sc, byte b) {
        enqueueOutput(sc, new byte[]{b});
    }

    synchronized void close(SelectableChannel sc) {
        
        // ConcurrentLinkedQueue - no synchronization needed
        pendingChangeRequests.add(new ChangeRequest(sc, CLOSE, -1));
    }

    public SocketChannel takeAccept(ServerSocketChannel ssc) throws InterruptedException {
        // ConcurrentHashMap and LinkedBlockingQueue, both thread-safe,
        // and no need for atomicity here
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        log.finer("Waiting for accept on server socket: " + ssc + " in queue: " + queue);
        return queue.take();
    }

    public void notifyAccepted(ServerSocketChannel ssc, SocketChannel socketChannel) {
        readyForSelect.add(new Pair<ServerSocketChannel, SocketChannel>(ssc, socketChannel));
    }

    synchronized Object dequeueChannelForSelect
        (Collection<SelectableChannel> registeredChannels) throws InterruptedException {
        while (true) {
            Object o = readyForSelect.take();
            if (contains(registeredChannels, o)) {
                log.finest("Returning: " + o + ". readyForSelect queue: " + readyForSelect);
                return o;
            } else if (hasInterestedSelector(o)) {
                readyForSelect.add(o);
            } else {
                log.finer("Dropping from readyForSelect: " + o);
            }
        }
    }

    private boolean hasInterestedSelector(Object o) {
        if (!(o instanceof SocketChannel)) return true;
        else {
            SocketChannel chan = (SocketChannel) o;
            return !interestedSelectors.get(chan).isEmpty();
        }
    }

    private boolean contains(Collection<SelectableChannel> coll, Object o) {
        if (o instanceof SocketChannel) {
            return coll.contains(o);
        } else {
            Pair<ServerSocketChannel, SocketChannel> p = (Pair<ServerSocketChannel, SocketChannel>) o;
            return coll.contains(p.first);
        }
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                updateRegistrations();
                doSelect();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error in selecting loop", e);
            }
        }
    }

    private void updateRegistrations() throws IOException {
        Set<SelectableChannel> modified = new HashSet<SelectableChannel>();
        Collection<ChangeRequest> postponed = new LinkedList<ChangeRequest>();
        while (!pendingChangeRequests.isEmpty()) {
            ChangeRequest req = pendingChangeRequests.remove();
            boolean done = req.execute(selector, modified);
            if (!done) postponed.add(req);
        }
        pendingChangeRequests.addAll(postponed);
    }

    private void doSelect() throws IOException {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("NIO select, registered keys: " + dumpKeys(selector) 
                + " - remaining outputs: " + dumpOutputs() + "...");
        }
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

    private String dumpOutputs()
    {
        StringBuilder b = new StringBuilder();
        for (SocketChannel chan : requestedOutputs.keySet()) {
            Queue<ByteBuffer> q = requestedOutputs.get(chan);
            b.append(chan).append(':').append(q);
        }
        return b.toString();
    }

    private static String dumpKeys(Selector selector) {
        Set<SelectionKey> keys = selector.keys();
        StringBuilder b = new StringBuilder();
        for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
            SelectionKey key = it.next();
            b.append(key.channel())
                .append(" : ")
                .append(formatKey(key));
            if (it.hasNext()) b.append(", ");
        }
        return b.toString();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = requestedOutputs.get(socketChannel);

        log.finer("Writing data on: " + socketChannel);
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
            log.finest("Finished writing, changing interest back to OP_READ on: " + socketChannel); 
            key.interestOps(OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        log.finer("Reading on: " + socketChannel);
        // Clear out our read buffer so it's ready for new data
        readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            log.log(Level.WARNING, 
                "Remote peer forcibly closed connection, closing channel and cancelling key", e);
            key.cancel();
            socketChannel.close();
            return;
        }

        // socketChannel.read() advances the position, so need to set it back to 0.
        // also set the limit to the previous position, so that the next
        // method will not try to read past what was read from the socket.
        readBuffer.flip();

        try {
            consumeBytesRead(key, (SocketChannel) key.channel(),
                    //readBuffer.asReadOnlyBuffer(), // just to be safe (shouldn't hurt speed)
            				readBuffer,
                    numRead == -1 // -1 if and only if eof
            );
        } catch (SJIOException e) {
            log.log(Level.SEVERE, "Could not deserialize on channel: " + key.channel(), e);
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.cancel();
            socketChannel.close();
        }

    }

    private void consumeBytesRead(SelectionKey key, SocketChannel sc, ByteBuffer bytes, boolean eof) throws SJIOException
    {
    	OngoingRead or = (OngoingRead) key.attachment();
    		
    	if (or == null)
    	{
            // For custom message formatting mode, this does *not* create a new OngoingRead: it returns a singleton. 
    		or = attachNewOngoingRead(key); 
    	}
    	
    	while (bytes.remaining() > 0) 
    	{
            or.updatePendingInput(bytes, eof);

            if (or.finished())
            {
                ByteBuffer input = or.getCompleteInput();

                log.finer("Received complete input on channel " + sc + ": " + input);

                if (readyInputs.containsKey(sc))
                {
                    readyInputs.get(sc).add(input);
                    readyForSelect.add(sc); // order is important here: adding to readyForSelect makes the read visible to select
                }
                else
                {
                    log.finer("Dropping input received on deregistered channel: " + sc + ", input: " + input);
                }

                or = attachNewOngoingRead(key);
            }
    	}
    }
    
    private OngoingRead attachNewOngoingRead(SelectionKey key) throws SJIOException {
        OngoingRead read = deserializers.get(key.channel()).newOngoingRead();
        key.attach(read);
        return read;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        log.finer("Enqueuing accepted socket for server socket: " + ssc + " in queue: " + queue);
        queue.add(socketChannel);
    }

    private static class ChangeRequest {
        private final SelectableChannel chan;
        private final ChangeAction changeAction;
        private final int interestOps;

        ChangeRequest(SelectableChannel chan, ChangeAction changeAction, int interestOps) {
            assert chan != null;
            assert changeAction != null;
            this.chan = chan;
            this.changeAction = changeAction;
            this.interestOps = interestOps;
        }

        boolean execute(Selector selector, Set<SelectableChannel> modified) throws IOException {
            return changeAction.execute(selector, this, modified);
        }
    }

    enum ChangeAction {
        REGISTER {
            boolean execute(Selector selector, ChangeRequest req, Set<SelectableChannel> modified) throws ClosedChannelException {
                log.finer("Registering chan: " + req.chan + ", ops: " + formatOps(req.interestOps));
                req.chan.register(selector, req.interestOps);
                return true;
            }
        }, CHANGEOPS {
            boolean execute(Selector selector, ChangeRequest req, Set<SelectableChannel> modified) {
                SelectionKey key = req.chan.keyFor(selector);
                if (key != null && key.isValid() && key.interestOps() != req.interestOps) {
                    if (!modified.contains(req.chan)){
                        log.finer("Changing ops for: " + req.chan 
                            + " to: " + formatOps(req.interestOps));
                        key.interestOps(req.interestOps);
                        modified.add(req.chan);
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }, CLOSE {
            boolean execute(Selector selector, ChangeRequest req, Set<SelectableChannel> modified) throws IOException {
                log.finer("Closing channel: " + req.chan);
                // Implicitly cancels all existing selection keys for that channel (see javadoc).
                req.chan.close();
                return true;
            }
        }, CANCEL {
            boolean execute(Selector selector, ChangeRequest req, Set<SelectableChannel> modified) {
                SelectionKey key = req.chan.keyFor(selector);
                if (key != null) key.cancel();
                return true;
            }};

        abstract boolean execute(Selector selector, ChangeRequest req, Set<SelectableChannel> modified) throws IOException;
    }

    private static String formatKey(SelectionKey key) {
        if (!key.isValid()) return "[cancelled]";
        return formatOps(key.interestOps());
    }

    private static String formatOps(int interests) {
        switch (interests) {
            case 1: return "1 (OP_READ)";
            case 4: return "4 (OP_WRITE)";
            case 8: return "8 (OP_CONNECT)";
            case 16: return "16 (OP_ACCEPT)";
            default: return String.valueOf(interests);
        }
    }

}
