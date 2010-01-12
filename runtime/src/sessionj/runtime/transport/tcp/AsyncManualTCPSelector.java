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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
class AsyncManualTCPSelector implements TransportSelector {
    private static final Logger log = SJRuntimeUtils.getLogger(AsyncManualTCPSelector.class);
    
    private final SelectingThread thread;
    private final SJTransport transport;
    private final ChannelRegistrations registrations;

    AsyncManualTCPSelector(SelectingThread thread, SJTransport transport) {
        this.thread = thread;
        this.transport = transport;
        registrations = new ChannelRegistrations();
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public boolean registerAccept(SJServerSocket ss) throws IOException {
        ServerSocketChannel ssc = retrieveServerSocketChannel(ss);
        
        if (ssc != null) {
            // Not doing the real registration with the selecting thread,
            // this is done by the acceptor, ahead of time.
            // When the real registration is done twice, a race condition (occasional deadlocks) happens.
            
            registrations.accept(ssc, ss);
            return true;
        }
        return false;
    }

    public boolean registerInput(SJSocket s) throws SJIOException {
        SocketChannel sc = retrieveSocketChannel(s);
        if (sc != null) {
            if (log.isLoggable(Level.FINER))
                log.finer("Registering: " + sc);
            thread.registerInput(sc, s.getParameters().createDeserializer(), this);
            registrations.input(sc, new DirectlyToUser(s));
            return true;
        }
        return false;
    }

    public SJSocket select(boolean considerSessionType) throws SJIOException, SJIncompatibleSessionException {
        while (true) {
            Object chan;
            try {
                log.finer("Blocking dequeue...");
                chan = thread.dequeueChannelForSelect(registrations.registeredChannels()); // blocking dequeue
                if (log.isLoggable(Level.FINE)) log.fine("Channel selected: " + chan);
            } catch (InterruptedException e) {
                throw new SJIOException(e);
            }
            if (chan instanceof SocketChannel) {
                SocketChannel sc = (SocketChannel) chan;

                InputState state = registrations.getInput(sc);
                InputState newState = state.receivedInput();
                registrations.input(sc, newState);
                SJSocket s = newState.sjSocket();
                if (s == null) {
                    log.finest("Read: InputState not complete: looping in select");
                } else if (considerSessionType && s.remainingSessionType() == null) {
                    // User-level inputs all done - this must be from the close protocol
                    if (log.isLoggable(Level.FINER))
                        log.finer("remainingSessionType is null: looping in select and deregistering socket " + s);
                    thread.enqueueChannelForSelect(sc);
                    deregister(sc);
                } else {
                    return s;
                }
            } else {
                Pair<ServerSocketChannel, SocketChannel> p = (Pair<ServerSocketChannel, SocketChannel>) chan;
                ServerSocketChannel ssc = p.first;

                SJServerSocket sjss = registrations.getAccept(ssc);
                registerOngoingAccept(sjss, p.second);

                InputState initialState = registrations.getInput(p.second);
                SJSocket s = initialState.sjSocket();
                if (s == null) {
                    log.finest("Accept: InputState not complete: looping in select");
                } else {
                    return s;
                }
            }
        }
    }

    private void deregister(SocketChannel sc) {
        // Does not really cancel the selection key, unless no
        // other instance of this class is interested.
        thread.deregisterInput(sc, this); 
        
        registrations.removeInput(sc);
    }

    private void registerOngoingAccept(SJServerSocket sjss, SocketChannel sc) throws SJIOException, SJIncompatibleSessionException {
        // OK even if we only do outputs: enqueing write requests will change the interest
        // set for the channel. Input is the default interest that we go back to after 
        // everything is written.
        log.finest("sjss: " + sjss + ", sc: " + sc);
        thread.registerInput(sc, sjss.getParameters().createDeserializer(), this);
        registrations.input(sc, sjss.getParameters().getAcceptProtocol().initialAcceptState(sjss, sc));
    }

    public void close() throws SJIOException {
        log.finer("Closing selector");
        for (SelectableChannel chan : registrations.registeredChannels()) {
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
