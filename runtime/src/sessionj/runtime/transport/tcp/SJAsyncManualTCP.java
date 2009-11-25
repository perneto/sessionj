package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelectorInternal;
import sessionj.runtime.transport.*;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOT SUITABLE to use as a negotiation transport (as the negotiation protocol does not use select).
 */
public final class SJAsyncManualTCP extends AbstractSJTransport
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.tcp.SJAsyncManualTCP";

	public static final int TCP_PORT_MAP_ADJUST = 200;
	
	static final boolean TCP_NO_DELAY = true;
	
    private final AsyncManualTCPSelector selector;
    private final SelectingThread thread;
    private static final Logger logger = Logger.getLogger(SJAsyncManualTCP.class.getName());

    public SJAsyncManualTCP() throws IOException {
        thread = new SelectingThread();
        selector = new AsyncManualTCPSelector(thread, TRANSPORT_NAME);
        Thread t = new Thread(thread, "AsyncManualTCP Selecting Thread");
        t.setDaemon(true);
        t.start();
    }

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException {
        try {
            return new AsyncTCPAcceptor(thread, port);
        } catch (IOException e) {
            throw new SJIOException("Could not open acceptor on (physical) port: " + port, e);
        }
    }
	
	public SJConnection connect(String hostName, int port) throws SJIOException // Transport-level values.
	{
        Socket s = null;
        try {
            //noinspection SocketOpenedButNotSafelyClosed
            s = new Socket(hostName, port);
            s.setTcpNoDelay(TCP_NO_DELAY);

            // TODO: Allow async operation on the client too.
            return new SJManualTCPConnection(s, s.getInputStream(), s.getOutputStream()) {
                @Override
                public String getTransportName() {
                    return TRANSPORT_NAME;
                }
            };
        }
        catch (IOException ioe)
        {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not close socket after initial exception:" + ioe, e);
            }
            throw new SJIOException(ioe);
        }
	}

    public SJSelectorInternal transportSelector() {
        return selector;
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
