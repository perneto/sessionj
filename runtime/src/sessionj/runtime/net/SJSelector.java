package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelector {
    int ACCEPT = 1; // To be used with bitwise-or, so choose appropriate values. 
    int SEND = 2;
    int RECEIVE = 4;

    void registerAccept(SJServerSocket ss);

    void registerSend(SJSocket s); // Although it has "Send" in the name, it is for all output operations (e.g. outwhile, outbranch), not just send. Similarly for registerReceive.

    void registerReceive(SJSocket s); 

    SJSocket select(int mask) throws SJIOException;
}
