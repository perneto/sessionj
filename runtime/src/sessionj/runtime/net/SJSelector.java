package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelector extends SJSelectorBase {

    void registerAccept(SJServerSocket ss) throws SJIOException;

    void registerOutput(SJSocket s) throws SJIOException;

    void registerInput(SJSocket s) throws SJIOException;

}
