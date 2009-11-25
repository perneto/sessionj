package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.*;
import sessionj.util.Pair;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
class AsyncManualTCPSelector implements SJSelectorInternal {
    private static final Logger logger = Logger.getLogger(AsyncManualTCPSelector.class.getName());
    
    private final SelectingThread thread;
    private final String transportName;
    private final Map<SocketChannel, InputState> registeredInputs;
    private final Map<ServerSocketChannel, SJServerSocket> registeredAccepts;

    AsyncManualTCPSelector(SelectingThread thread, String transportName) {
        this.thread = thread;
        this.transportName = transportName;
        registeredInputs = new HashMap<SocketChannel, InputState>();
        registeredAccepts = new HashMap<ServerSocketChannel, SJServerSocket>();
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public boolean registerAccept(SJServerSocket ss) throws IOException {
        ServerSocketChannel ssc = retrieveServerSocketChannel(ss);
        if (ssc != null) {
            // No need to do the real registration with the selecting thread,
            // this is done by the acceptor, ahead of time.
            // If done twice, a race condition (occasional deadlocks) happens.
            
            registeredAccepts.put(ssc, ss);
            return true;
        }
        return false;
    }

    public boolean registerInput(SJSocket s) {
        SocketChannel sc = retrieveSocketChannel(s);
        if (sc != null) {
            thread.registerInput(sc, s.getParameters().getDeserializer());
            registeredInputs.put(sc, new DirectlyToUser(s));
            return true;
        }
        return false;
    }

    public SJSocket select() throws SJIOException, SJIncompatibleSessionException {
        Object chan;
        try {
            chan = thread.dequeueChannelForSelect(); // blocking dequeue
            logger.fine("Channel dequeued in select: " + chan);
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        }
        if (chan instanceof SocketChannel) {
            SocketChannel sc = (SocketChannel) chan;
            // If the channel was deregistered, but we didn't finish reading all events:
            // select another one, dropping this one
            if (!registeredInputs.containsKey(sc)) return select();
            
            InputState state = registeredInputs.get(sc);
            InputState newState = state.receivedInput();
            registeredInputs.put(sc, newState);
            SJSocket s = newState.sjSocket();
            if (s == null) 
                return select();
            else if (s.remainingSessionType() == null) {
                // User-level inputs all done - this must be from the close protocol
                deregister(sc);
                return select();
            }
            else return s;
        } else {
            Pair<ServerSocketChannel, SocketChannel> p = (Pair<ServerSocketChannel, SocketChannel>) chan;
            ServerSocketChannel ssc = p.first;
            assert registeredAccepts.containsKey(ssc);

            SJServerSocket sjss = registeredAccepts.get(ssc);
            registerOngoingAccept(sjss, p.second);

            InputState initialState = registeredInputs.get(p.second);
            SJSocket s = initialState.sjSocket();
            if (s == null) return select();
            else return s;
        }
    }

    private void deregister(SocketChannel sc) {
        // HACK: Not doing this at the moment, so we can receive
        // the extra message from the close protocol
        // thread.deregisterInput(sc);
        
        registeredInputs.remove(sc);
    }

    private void registerOngoingAccept(SJServerSocket sjss, SocketChannel sc) throws SJIOException, SJIncompatibleSessionException {
        // OK even if we only do outputs: enqueing write requests will change the interest
        // set for the channel. Input is the default interest that we go back to after 
        // everything is written.
        thread.registerInput(sc, sjss.getParameters().getDeserializer());
        registeredInputs.put(sc, sjss.getParameters().getAcceptProtocol().initialAcceptState(sjss));
    }

    public void close() throws SJIOException {
        Collection<SelectableChannel> channels = new LinkedList<SelectableChannel>();
        channels.addAll(registeredAccepts.keySet());
        channels.addAll(registeredInputs.keySet());
        for (SelectableChannel chan : channels) {
            try {
                chan.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not close channel", e);
            }
        }
    }

    private SocketChannel retrieveSocketChannel(SJSocket s) {
        SocketChannel sc = null;
        if (isOurSocket(s)) {
            sc = ((AsyncConnection) s.getConnection()).socketChannel();
            assert sc != null;
        }
        return sc;
    }
    
    private ServerSocketChannel retrieveServerSocketChannel(SJServerSocket ss) {
        AsyncTCPAcceptor acceptor = (AsyncTCPAcceptor) ss.getAcceptorFor(transportName);
        if (acceptor == null) {
            return null;
        } else {
            return acceptor.ssc;
        }
    }

    private boolean isOurSocket(SJSocket s) {
        return isOurName(s.getConnection().getTransportName());
    }

    private boolean isOurName(Object name) {
        return name.equals(transportName);
    }
}
