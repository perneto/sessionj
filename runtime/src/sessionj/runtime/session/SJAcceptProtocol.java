package sessionj.runtime.session;

import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.tcp.InputState;

public interface SJAcceptProtocol {
    InputState initialAcceptState(SJServerSocket extraInput) throws SJIOException;
}
