package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

import java.nio.channels.SelectionKey;

public interface SJSelectorBase {
    int ACCEPT = SelectionKey.OP_ACCEPT; // To be used with bitwise-or, so choose appropriate values.
    int INPUT = SelectionKey.OP_READ;

    SJSocket select(int mask) throws SJIOException, SJIncompatibleSessionException; // Select may throw SJIncompatibleSessionException when finishing accept actions. But this is a bit inconvenient when we are not using a selector in this way.  
    void close() throws SJIOException;
}
