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
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINE;

final class SelectingThread implements Runnable {
    private static final int BUFFER_SIZE = 16384;
    private final Selector selector;
    private final ByteBuffer readBuffer;

    private final Queue<ChangeRequest> pendingChangeRequests;

    private final Map<SocketChannel, BlockingQueue<ByteBuffer>> readyInputs;
    private final BlockingQueue<Object> readyForSelect;
    private final ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>> accepted;
    private final ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>> requestedOutputs;
    private static final Logger log = SJRuntimeUtils.getLogger(SelectingThread.class);
    private final Map<SocketChannel, SJDeserializer> deserializers;
    private final Map<SocketChannel, Collection<AsyncManualTCPSelector>> interestedSelectors;

    SelectingThread() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        pendingChangeRequests = new ConcurrentLinkedQueue<ChangeRequest>();
        readyInputs = Collections.synchronizedMap(new WeakHashMap<SocketChannel, BlockingQueue<ByteBuffer>>());
        requestedOutputs = new ConcurrentHashMap<SocketChannel, Queue<ByteBuffer>>();
        readyForSelect = new LinkedBlockingQueue<Object>();
        accepted = new ConcurrentHashMap<ServerSocketChannel, BlockingQueue<SocketChannel>>();
        deserializers = new ConcurrentHashMap<SocketChannel, SJDeserializer>();
        interestedSelectors = new ConcurrentHashMap<SocketChannel, Collection<AsyncManualTCPSelector>>();
        
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
        // Could test on other maps as well but not readyInputs - we don't delete keys from there
        if (!deserializers.containsKey(sc)) {
            if (log.isLoggable(FINER))
                log.finer("New registration for input: " + sc + ", deserializer: " + deserializer);
            readyInputs.put(sc, new LinkedBlockingQueue<ByteBuffer>());
            deserializers.put(sc, deserializer);
            Collection<AsyncManualTCPSelector> set = new HashSet<AsyncManualTCPSelector>();
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
            if (log.isLoggable(FINER))
                log.finer("Asked for input registration, but channel already registered. Channel: " + sc);
            interestedSelectors.get(sc).add(sel);
            selector.wakeup();
        }
    }
    
    synchronized void deregisterInput(SocketChannel sc, AsyncManualTCPSelector sel) {
        Collection<AsyncManualTCPSelector> coll = interestedSelectors.get(sc);
        if (coll != null) {
            coll.remove(sel);
            if (coll.isEmpty()) {
                pendingChangeRequests.add(new ChangeRequest(sc, CANCEL, -1));
                selector.wakeup();
            }
        }
    }

    /**
     * Non-blocking, as it should be used right after a select
     * call.
     * @param sc The channel for which to get an input message.
     * @return A complete message (according to the OngoingRead instance obtained from the serializer)
     * @throws java.util.NoSuchElementException if no message is ready for reading.
     */
    ByteBuffer dequeueFromInputQueue(SocketChannel sc) {
        if (log.isLoggable(FINEST))
            log.finest("Dequeueing input from: " + sc);
        return readyInputs.get(sc).remove();
    }
    
    ByteBuffer peekAtInputQueue(SocketChannel sc) {
        ByteBuffer b = readyInputs.get(sc).peek();
        if (log.isLoggable(FINEST))
            log.finest("Peeked input for: " + sc + ", input: " + b);
        return b;
    }

    synchronized void enqueueOutput(SocketChannel sc, byte[] bs) {
        Queue<ByteBuffer> outputsForChan = requestedOutputs.get(sc);
        if (outputsForChan == null) {
            outputsForChan = new ConcurrentLinkedQueue<ByteBuffer>();
            requestedOutputs.put(sc, outputsForChan);
        }
        outputsForChan.add(ByteBuffer.wrap(bs));
        if (log.isLoggable(FINER))
            log.finer("Enqueued write on: " + sc + " of: " + bs.length + " bytes");
        pendingChangeRequests.add(new ChangeRequest(sc, CHANGEOPS, OP_WRITE));
        selector.wakeup();
    }

    void enqueueOutput(SocketChannel sc, byte b) {
        enqueueOutput(sc, new byte[]{b});
    }

    void close(SelectableChannel sc) {
        // ConcurrentLinkedQueue - no synchronization needed
        pendingChangeRequests.add(new ChangeRequest(sc, CLOSE, -1));
    }

    SocketChannel takeAccept(ServerSocketChannel ssc) throws InterruptedException {
        // ConcurrentHashMap and LinkedBlockingQueue, both thread-safe,
        // and no need for atomicity here
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        if (log.isLoggable(FINER))
            log.finer("Waiting for accept on server socket: " + ssc + " in queue: " + queue);
        return queue.take();
    }

    void notifyAccepted(ServerSocketChannel ssc, SocketChannel socketChannel) {
        readyForSelect.add(new Pair<ServerSocketChannel, SocketChannel>(ssc, socketChannel));
    }

    // Do not synchronize: readyForSelect is thread-safe, and no need for the whole method to
    // be atomic. Moreover, if synchronization becomes desirable when this method is modified,
    // make sure not to synchronize on this: it causes a deadlock when we block in take()
    // while another thread tries to aquire the "this" monitor, in registerInput. 
    Object dequeueChannelForSelect
        (Collection<SelectableChannel> registeredChannels) throws InterruptedException {
        while (true) {
            Object o = readyForSelect.take();
            if (contains(registeredChannels, o)) {
                if (log.isLoggable(FINEST))
                    log.finest("Returning: " + o + ". readyForSelect queue: " + readyForSelect);
                return o;
            } else if (hasInterestedSelector(o)) {
                readyForSelect.add(o);
            } else {
                // FIXME: This might lose some channel-ready messages. But we can't keep putting
                // them back in the queue, otherwise the SJ-level selectors would keep being
                // woken up for nothing. (This loop would busy-wait for an interesting channel).
                log.info("Dropping from readyForSelect: " + o);
            }
        }
    }
    
    void enqueueChannelForSelect(Object sc) {
        readyForSelect.add(sc);
    }


    // interestedSelectors is a ConcurrentHashMap, because registerInput happens in parallel.
    private boolean hasInterestedSelector(Object o) {
        if (!(o instanceof SocketChannel)) return true;
        else {
            SocketChannel chan = (SocketChannel) o;
            Collection<AsyncManualTCPSelector> interested = interestedSelectors.get(chan);
            if (log.isLoggable(FINEST)) 
                log.finest("interestedSelectors["+chan+"]: "+ interested);
            return interested != null && !interested.isEmpty();
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
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error in selecting loop", e);
            }
        }
    }

    private void updateRegistrations() throws IOException {
        Set<SelectableChannel> modified = new HashSet<SelectableChannel>();
        Collection<ChangeRequest> postponed = new LinkedList<ChangeRequest>();
        while (!pendingChangeRequests.isEmpty()) {
            ChangeRequest req = pendingChangeRequests.remove();
            boolean done = req.execute(this, modified);
            if (!done) postponed.add(req);
        }
        pendingChangeRequests.addAll(postponed);
    }

    private void doSelect() throws IOException {
        if (log.isLoggable(FINEST)) {
            log.finest("NIO select, registered keys: \n" + dumpKeys(selector) 
                + "\nRemaining outputs:\n" + dumpOutputs());
        }
        selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            // This seems important: without it, we get notified several times
            // for the same event, resulting in eg. NPEs on accept.
            it.remove();

            if (key.isValid()) {
                // TODO: To support large numbers of clients (200+), we should only do the accepts
                // in this thread, and do the reads and writes in a separate thread, with its own
                // selector. See http://stackoverflow.com/questions/843283/asynchronous-channel-close-in-java-nio
                // This is probably the cause of the SocketException: Connection reset that can be
                // seen on the client side with large number of concurrent clients.
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
        for (Iterator<SocketChannel> it = requestedOutputs.keySet().iterator(); it.hasNext();) {
            SocketChannel chan = it.next();
            Queue<ByteBuffer> q = requestedOutputs.get(chan);
            b.append(chan).append(':').append(q);
            if (it.hasNext()) b.append(", ");
            b.append('\n');
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
            b.append('\n');
        }
        return b.toString();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = requestedOutputs.get(socketChannel);

        if (log.isLoggable(FINER))
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
            if (log.isLoggable(FINEST))
                log.finest("Finished writing, changing interest back to OP_READ on: " + socketChannel); 
            key.interestOps(OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if(log.isLoggable(FINEST))
            log.finest("Reading on: " + socketChannel);
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
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not deserialize on channel: " + key.channel(), e);
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.cancel();
            socketChannel.close();
            removeChannelKey(socketChannel);
        }

    }

    private void consumeBytesRead(SelectionKey key, SocketChannel sc, ByteBuffer bytes, boolean eof) 
        throws SJIOException
    {
    	OngoingRead or = (OngoingRead) key.attachment();
    		
    	if (or == null) {
            // For custom message formatting mode, this does *not* create a new OngoingRead: it returns a singleton. 
    		or = attachNewOngoingRead(key); 
    	}
    	
    	while (bytes.remaining() > 0)  {
            or.updatePendingInput(bytes, eof);

            if (or.finished()) {
                ByteBuffer input = or.getCompleteInput();

                if (log.isLoggable(FINER))
                    log.finer("Received complete input on channel " + sc + ": " + input);

                if (readyInputs.containsKey(sc)) {
                    readyInputs.get(sc).add(input);
                    readyForSelect.add(sc); // order is important here: adding to readyForSelect makes the read visible to select
                    or = attachNewOngoingRead(key);
                } else {
                    throw new SJIOException("Dropping input received on deregistered channel: " + sc + ", input: " + input);
                }

            }
    	}
    }
    
    private OngoingRead attachNewOngoingRead(SelectionKey key) throws SJIOException {
        SJDeserializer deserializer = deserializers.get(key.channel());
        OngoingRead read = deserializer.newOngoingRead();
        key.attach(read);
        return read;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setTcpNoDelay(SJStreamTCP.TCP_NO_DELAY);
        BlockingQueue<SocketChannel> queue = accepted.get(ssc);
        if (log.isLoggable(FINER))
            log.finer("Enqueuing accepted socket for server socket: " + ssc + " in queue: " + queue);
        queue.add(socketChannel);
    }
    
    // This is called on the selecting thread, before the select() call
    private void removeChannelKey(SelectableChannel sc) {
        // Not removing from readyInputs, as some AsyncConnection instance might still want
        // to read from it later. readyInputs is a WeakHashMap, so this should not be a memory leak.
        interestedSelectors.remove(sc);
        // Ideally, we'd only do interestedSelectors.get(sc).clear(), but that would be a memory leak.
        // A WeakHashMap would do the trick, but the code might not be as easy to understand then.
        requestedOutputs.remove(sc);
        deserializers.remove(sc);
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

        boolean execute(SelectingThread thread, Set<SelectableChannel> modified) throws IOException {
            return changeAction.execute(thread, this, modified);
        }
    }

    enum ChangeAction {
        REGISTER {
            boolean execute(SelectingThread thread, ChangeRequest req, Set<SelectableChannel> modified) throws ClosedChannelException {
                try {
                    if (log.isLoggable(FINER))
                        log.finer("Registering chan: " + req.chan + ", ops: " + formatOps(req.interestOps));
                    req.chan.register(thread.selector, req.interestOps);
                } catch (CancelledKeyException e) {
                    if (log.isLoggable(FINE))
                        log.fine("Tried to register but key cancelled: " + req.chan);
                } catch (ClosedChannelException e) {
                    // This can happen with the close-protocol selector. In this case,
                    // we're fine: it just means the reads are in the inputs queue already.
                    if (log.isLoggable(FINE))
                        log.fine("Tried to register but channel already closed: " + req.chan);
                }
                return true;
            }
        }, CHANGEOPS {
            boolean execute(SelectingThread thread, ChangeRequest req, Set<SelectableChannel> modified) {
                SelectionKey key = req.chan.keyFor(thread.selector);
                if (key != null && key.isValid() && key.interestOps() != req.interestOps) {
                    if (!modified.contains(req.chan)){
                        if (log.isLoggable(FINER))
                            log.finer("Changing ops for: " + req.chan + " to: " + formatOps(req.interestOps));
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
            boolean execute(SelectingThread thread, ChangeRequest req, Set<SelectableChannel> modified) throws IOException {
                if (log.isLoggable(FINER))
                    log.finer("Closing channel: " + req.chan);
                // Implicitly cancels all existing selection keys for that channel (see javadoc).
                req.chan.close();
                thread.removeChannelKey(req.chan);
                return true;
            }
        }, CANCEL {
            boolean execute(SelectingThread thread, ChangeRequest req, Set<SelectableChannel> modified) {
                if (log.isLoggable(FINER))
                    log.finer("Deregistering channel: " + req.chan);
                SelectionKey key = req.chan.keyFor(thread.selector);
                if (key != null) key.cancel();
                thread.removeChannelKey(req.chan);
                return true;
            }};

        abstract boolean execute(SelectingThread thread, ChangeRequest req, Set<SelectableChannel> modified) throws IOException;
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
