package sessionj.runtime.session;

import sessionj.runtime.net.SJSocket;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.tcp.InputState;
import sessionj.runtime.transport.tcp.WaitInitialInputIfNeeded;

/**
 * Hardcodes the number of messages expected from clients in the accept protocol.
 * 
 */
class DefaultSJProtocolAcceptState implements InputState {
    private int inputsReceived = 0;
    private final SJServerSocket serverSocket;

    DefaultSJProtocolAcceptState(SJServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public InputState receivedInput() throws SJIOException, SJIncompatibleSessionException {
        inputsReceived++;
        if (inputsReceived == 3) 
            return new WaitInitialInputIfNeeded(serverSocket.accept());
        else return this;
    }

    public SJSocket sjSocket() {
        return null;
    }
}
