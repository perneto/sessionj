package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

public interface SJSelectorBase {
    void close() throws SJIOException;
}
