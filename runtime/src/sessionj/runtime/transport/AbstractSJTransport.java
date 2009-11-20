package sessionj.runtime.transport;

import sessionj.runtime.net.SJSelectorInternal;

public abstract class AbstractSJTransport implements SJTransport {
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SJTransport)) return false;
        return getTransportName().equals(((SJTransport) o).getTransportName());
    }

    /**
     * Default implementation if async mode is unsupported.
     * @return null, always
     */
    public SJSelectorInternal transportSelector() {
        return null;
    }
    
}
