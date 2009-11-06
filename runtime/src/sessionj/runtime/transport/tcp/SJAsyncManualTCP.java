package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelectorInternal;
import sessionj.runtime.session.SJManualDeserializer;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;

import java.io.IOException;
import java.net.Socket;

/**
 *
 */
public final class SJAsyncManualTCP implements SJTransport
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJAsyncManualTCP";

	public static final int TCP_PORT_MAP_ADJUST = 200;
	
	static final boolean TCP_NO_DELAY = true;
	
    private final AsyncManualTCPSelector selector;

    public SJAsyncManualTCP() throws IOException {
        SelectingThread thread = new SelectingThread(new SJManualDeserializer());
        selector = new AsyncManualTCPSelector(thread, TRANSPORT_NAME);
        Thread t = new Thread(thread);
        t.setDaemon(true);
        t.start();
    }

    public SJConnectionAcceptor openAcceptor(int port) {
        throw new UnsupportedOperationException("Blocking mode unsupported");
    }
	
	public SJConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
        try {
            Socket s = new Socket(hostName, port);
            s.setTcpNoDelay(TCP_NO_DELAY);

            return new SJManualTCPConnection(s, s.getInputStream(), s.getOutputStream()); // Have to get I/O streams here for exception handling.
        }
        catch (IOException ioe)
        {
            throw new SJIOException(ioe);
        }
	}

    public SJSelectorInternal transportSelector() {
        return selector;
    }

    public boolean blockingModeSupported() {
        return false;
    }

    public boolean portInUse(int port)
	{
        return SJStreamTCP.isTCPPortInUse(port);
	}

    public int getFreePort() throws SJIOException
	{
        return SJStreamTCP.getFreeTCPPort(getTransportName());
	}
	
	public String getTransportName()
	{
		return TRANSPORT_NAME;
	}
	
	public String sessionHostToNegociationHost(String hostName)
	{
		return hostName;
	}
	
	public int sessionPortToSetupPort(int port) 
	{
		return port + TCP_PORT_MAP_ADJUST;
	}

}
