package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

import java.nio.channels.SelectionKey;

public interface SJSelectorBase {
    int ACCEPT = SelectionKey.OP_ACCEPT; // To be used with bitwise-or, so choose appropriate values.
    int OUTPUT = SelectionKey.OP_WRITE;
    int INPUT = SelectionKey.OP_READ;

    SJSocket select(int mask) throws SJIOException;
    void close();
}
