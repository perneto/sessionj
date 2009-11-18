package sessionj.runtime.session;

import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.SJIOException;

public interface SJAcceptProtocol {
    AcceptState initialAcceptState(SJServerSocket extraInput) throws SJIOException;
}
