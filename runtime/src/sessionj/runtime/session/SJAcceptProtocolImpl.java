package sessionj.runtime.session;

import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.SJIOException;

public class SJAcceptProtocolImpl implements SJAcceptProtocol {
    public AcceptState initialAcceptState(SJServerSocket serverSocket) throws SJIOException {
        return new DefaultSJProtocolAcceptState(serverSocket);
    }
}
