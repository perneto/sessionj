package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelectorInternal extends SJSelectorBase {
    boolean registerAccept(SJServerSocket ss) throws Exception;
    boolean registerInput(SJSocket s) throws Exception;

    SJSocket select(boolean considerSessionType) throws SJIOException, SJIncompatibleSessionException;
}
