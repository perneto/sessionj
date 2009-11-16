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
    private final SelectingThread thread;
    private final String transportName;
    private final Map<SocketChannel, SJSocket> registeredInputs;
    private final Map<ServerSocketChannel, SJServerSocket> registeredAccepts;
    private static final Logger logger = Logger.getLogger(AsyncManualTCPSelector.class.getName());

    AsyncManualTCPSelector(SelectingThread thread, String transportName) {
        this.thread = thread;
        this.transportName = transportName;
        registeredInputs = new HashMap<SocketChannel, SJSocket>();
        registeredAccepts = new HashMap<ServerSocketChannel, SJServerSocket>();
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public boolean registerAccept(SJServerSocket ss) throws IOException {
        ServerSocketChannel ssc = retrieveServerSocketChannel(ss);
        if (ssc != null) {
            thread.registerAccept(ssc);
            registeredAccepts.put(ssc, ss);
            return true;
        }
        return false;
    }

    public boolean registerInput(SJSocket s) {
        SocketChannel sc = retrieveSocketChannel(s);
        if (sc != null) {
            thread.registerInput(sc);
            registeredInputs.put(sc, s);
            return true;
        }
        return false;
    }

    public SJSocket select() throws SJIOException, SJIncompatibleSessionException {
        SJSocket s = null;
        while (s == null) {
            Object chan;
            try {
                chan = thread.dequeueChannelForSelect(); // blocking dequeue
            } catch (InterruptedException e) {
                throw new SJIOException(e);
            }
            if (chan instanceof SocketChannel) {
                SocketChannel sc = (SocketChannel) chan;
                assert registeredInputs.containsKey(sc);
                s = registeredInputs.get(sc);
            }  else  {
                Pair<ServerSocketChannel, SocketChannel> p = (Pair<ServerSocketChannel, SocketChannel>) chan;
                ServerSocketChannel ssc = p.first;
                assert registeredAccepts.containsKey(ssc);

                // OK even if we only do outputs: enqueing write requests will change the interest
                // set for the channel. Input is the default interest that we go back to after everything
                // is written.
                thread.registerInput(p.second);

                SJServerSocket sjss = registeredAccepts.get(ssc);
                if (sjss.typeStartsWithOutput())
                    s = sjss.accept();
                else
                    return select();
                // The new SocketChannel is registered for reading just before,
                // so no need to do anything else.
            }
        }
        return s;
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

    private boolean isOurName(String name) {
        return name.equals(transportName);
    }
}
