package sessionj.runtime.session;

import sessionj.runtime.net.SJSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.SJIOException;

public interface AcceptState {
    boolean hasFinishedAccept();

    void receivedInput();

    SJSocket accept() throws SJIOException, SJIncompatibleSessionException;
}
