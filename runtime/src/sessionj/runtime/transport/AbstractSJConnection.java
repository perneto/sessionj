package sessionj.runtime.transport;

public abstract class AbstractSJConnection implements SJConnection {
    private final SJTransport transport;

    protected AbstractSJConnection(SJTransport transport) {
        this.transport = transport;
    }

    public SJTransport getTransport() {
        return transport;
    }

    public String getTransportName() {
        return transport.getTransportName();
    }

    public boolean supportsBlocking() {
        return true;
    }
}
