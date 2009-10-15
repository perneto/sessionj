package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelector {
    int ACCEPT = 1;
    int SEND = 2;
    int RECEIVE = 4;

    void registerAccept(SJServerSocket ss);

    void registerSend(SJSocket s);

    void registerReceive(SJSocket s);

    SJSocket select(int mask) throws SJIOException;
}
