package sessionj.runtime.session;

import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.tcp.InputState;

import java.nio.channels.SocketChannel;

public class SJAcceptProtocolImpl implements SJAcceptProtocol {
    public InputState initialAcceptState(SJServerSocket serverSocket, SocketChannel sc) throws SJIOException {
        return new DefaultSJProtocolAcceptState(serverSocket, sc);
    }
}
