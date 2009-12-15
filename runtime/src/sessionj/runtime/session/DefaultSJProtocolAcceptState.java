package sessionj.runtime.session;

import sessionj.runtime.net.SJSocket;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.SJIOException;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.runtime.transport.tcp.InputState;
import sessionj.runtime.transport.tcp.WaitInitialInputIfNeeded;

import java.util.logging.Logger;
import java.nio.channels.SocketChannel;

/**
 * FIXME: Hardcodes the number of messages expected from clients in the accept protocol.
 * 
 * The messages are:
 * - client host name
 * - client port
 * - client serialized session type
 * 
 * See {@link sessionj.runtime.net.SJRuntime#accept( sessionj.runtime.net.SJAbstractSocket )}.
 * 
 * Meanwhile, the client is only waiting for the server's serialized session type,
 * so we can read all of this and only then write our session type.
 * 
 */
class DefaultSJProtocolAcceptState implements InputState {
    private int inputsReceived = 0;
    private final SJServerSocket serverSocket;
    private final SocketChannel sc;
    private static final Logger log = SJRuntimeUtils.getLogger(DefaultSJProtocolAcceptState.class);

    DefaultSJProtocolAcceptState(SJServerSocket serverSocket, SocketChannel sc) {
        this.serverSocket = serverSocket;
        this.sc = sc;
    }

    public InputState receivedInput() throws SJIOException, SJIncompatibleSessionException {
        inputsReceived++;
        if (inputsReceived == 3) {
            log.finest("Calling accept. sjss: " + serverSocket + ", sc: " + sc);
            serverSocket.getParameters().setSocketChannelHack(sc);
            return new WaitInitialInputIfNeeded(serverSocket.accept());
        } else return this;
    }

    public SJSocket sjSocket() {
        return null;
    }
}
