package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelector extends SJSelectorBase {

    void registerAccept(SJServerSocket ss) throws SJIOException;

    void registerInput(SJSocket s) throws SJIOException;

    // Select may throw SJIncompatibleSessionException when finishing accept actions.
    // But this is a bit inconvenient when we are not using a selector in this way.      
    SJSocket select() throws SJIOException, SJIncompatibleSessionException;
}
