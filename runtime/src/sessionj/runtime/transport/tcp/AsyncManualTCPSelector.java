package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.util.Pair;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

/**
 */
class AsyncManualTCPSelector implements SJSelectorInternal {
    private static final Logger log = SJRuntimeUtils.getLogger(AsyncManualTCPSelector.class);
    
    private final SelectingThread thread;
    private final SJTransport transport;
    private final Map<SocketChannel, InputState> registeredInputs;
    private final Map<ServerSocketChannel, SJServerSocket> registeredAccepts;


    AsyncManualTCPSelector(SelectingThread thread, SJTransport transport) {
        this.thread = thread;
        this.transport = transport;
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

    public boolean registerInput(SJSocket s) throws SJIOException {
        SocketChannel sc = retrieveSocketChannel(s);
        if (sc != null) {
            log.finer("Registering: " + sc);
            thread.registerInput(sc, s.getParameters().createDeserializer(), this);
            registeredInputs.put(sc, new DirectlyToUser(s));
            return true;
        }
        return false;
    }

    public SJSocket select(boolean considerSessionType) throws SJIOException, SJIncompatibleSessionException {
        while (true) {
            Object chan;
            try {
                log.fine("Blocking dequeue...");
                chan = thread.dequeueChannelForSelect(registeredChannels()); // blocking dequeue
                log.fine("Channel selected: " + chan);
            } catch (InterruptedException e) {
                throw new SJIOException(e);
            }
            if (chan instanceof SocketChannel) {
                SocketChannel sc = (SocketChannel) chan;
                assert registeredInputs.containsKey(sc);

                InputState state = registeredInputs.get(sc);
                InputState newState = state.receivedInput();
                registeredInputs.put(sc, newState);
                SJSocket s = newState.sjSocket();
                if (s == null) {
                    log.finest("Read: InputState not complete: looping in select");
                } else if (considerSessionType && s.remainingSessionType() == null) {
                    // User-level inputs all done - this must be from the close protocol
                    log.finer("remainingSessionType is null: looping in select and deregistering socket " + s);
                    deregister(sc);
                } else {
                    return s;
                }
            } else {
                Pair<ServerSocketChannel, SocketChannel> p = (Pair<ServerSocketChannel, SocketChannel>) chan;
                ServerSocketChannel ssc = p.first;
                assert registeredAccepts.containsKey(ssc);

                SJServerSocket sjss = registeredAccepts.get(ssc);
                registerOngoingAccept(sjss, p.second);

                InputState initialState = registeredInputs.get(p.second);
                SJSocket s = initialState.sjSocket();
                if (s == null) {
                    log.finest("Accept: InputState not complete: looping in select");
                } else {
                    return s;
                }
            }
        }
    }

    private Collection<SelectableChannel> registeredChannels() {
        Collection<SelectableChannel> chans = new 
            LinkedList<SelectableChannel>(registeredInputs.keySet());
        chans.addAll(registeredAccepts.keySet());
        return chans;
    }

    private void deregister(SocketChannel sc) {
        // Does not really cancel the selection key, unless no
        // other instance of this class is interested.
        thread.deregisterInput(sc, this); 
        
        registeredInputs.remove(sc);
    }

    private void registerOngoingAccept(SJServerSocket sjss, SocketChannel sc) throws SJIOException, SJIncompatibleSessionException {
        // OK even if we only do outputs: enqueing write requests will change the interest
        // set for the channel. Input is the default interest that we go back to after 
        // everything is written.
        log.finest("sjss: " + sjss + ", sc: " + sc);
        thread.registerInput(sc, sjss.getParameters().createDeserializer(), this);
        registeredInputs.put(sc, sjss.getParameters().getAcceptProtocol().initialAcceptState(sjss, sc));
    }

    public void close() throws SJIOException {
        log.finer("Closing selector");
        Collection<SelectableChannel> channels = new LinkedList<SelectableChannel>();
        channels.addAll(registeredAccepts.keySet());
        channels.addAll(registeredInputs.keySet());
        for (SelectableChannel chan : channels) {
            thread.close(chan);
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
        AsyncTCPAcceptor acceptor = (AsyncTCPAcceptor) 
            ss.getAcceptorFor(transport.getTransportName());
        if (acceptor == null) {
            return null;
        } else {
            return acceptor.ssc;
        }
    }

    private boolean isOurSocket(SJSocket s) {
        return transport.equals(s.getConnection().getTransport());
    }
}
