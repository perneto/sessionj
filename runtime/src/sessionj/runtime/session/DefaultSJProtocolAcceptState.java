package sessionj.runtime.session;

import sessionj.runtime.net.SJSocket;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.SJIOException;

class DefaultSJProtocolAcceptState implements AcceptState {
    private int inputsReceived = 0;
    private final SJServerSocket serverSocket;
    private final boolean noExtraInput;

    DefaultSJProtocolAcceptState(SJServerSocket serverSocket) throws SJIOException {
        this.serverSocket = serverSocket;
        noExtraInput = serverSocket.typeStartsWithOutput();
    }

    public boolean hasFinishedAccept() {
        return inputsReceived == (noExtraInput ? 3 : 4);
    }

    public void receivedInput() {
        inputsReceived++;
    }

    public SJSocket accept() throws SJIOException, SJIncompatibleSessionException {
        return serverSocket.accept();
    }
}
