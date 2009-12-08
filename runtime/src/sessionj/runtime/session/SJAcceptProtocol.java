package sessionj.runtime.session;

import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJIncompatibleSessionException;
import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.tcp.InputState;

import java.nio.channels.SocketChannel;

public interface SJAcceptProtocol {
    InputState initialAcceptState(SJServerSocket serverSocket, SocketChannel sc) throws SJIOException, SJIncompatibleSessionException;
}
