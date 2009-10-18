package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

// Ray: would be more uniform with the current design for SJSelector to be an abstract class so that the factory method for selectors can be in here. 
public interface SJSelector {
    public static final int ACCEPT = 1; // To be used with bitwise-or, so choose appropriate values. 
    public static final int SEND = 2;
    public static final int RECEIVE = 4;

    void registerAccept(SJServerSocket ss);

    void registerSend(SJSocket s); // Although it has "Send" in the name, it is for all output operations (e.g. outwhile, outbranch), not just send. Similarly for registerReceive.

    void registerReceive(SJSocket s); 

    SJSocket select(int mask) throws SJIOException;
}
