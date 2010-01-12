package sessionj.runtime.transport;

public abstract class AbstractSJConnection extends AbstractWithTransport implements SJConnection {
    protected AbstractSJConnection(SJTransport transport) {
	    super(transport);
    }
	
    public boolean supportsBlocking() {
        return true;
    }
}
