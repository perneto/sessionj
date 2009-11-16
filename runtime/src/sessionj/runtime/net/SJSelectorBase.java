package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelectorBase {
    SJSocket select() throws SJIOException, SJIncompatibleSessionException;
    // Select may throw SJIncompatibleSessionException when finishing accept actions.
    // But this is a bit inconvenient when we are not using a selector in this way.  
    void close() throws SJIOException;
}
