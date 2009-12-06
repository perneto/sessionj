package sessionj.runtime.transport.tcp;

import sessionj.runtime.SJIOException;
import sessionj.runtime.util.SJRuntimeUtils;
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
	
    private final SelectingThread thread;
    private static final Logger logger = SJRuntimeUtils.getLogger(SJAsyncManualTCP.class);

    public SJAsyncManualTCP() throws IOException {
        thread = new SelectingThread();
        Thread t = new Thread(thread, "SelectingThread");
        t.setDaemon(true);
        t.start();
    }

    public SJConnectionAcceptor openAcceptor(int port) throws SJIOException {
        try {
            return new AsyncTCPAcceptor(thread, port, this);
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
            s.setTcpNoDelay(SJManualTCP.TCP_NO_DELAY);

            // TODO: Allow async operation on the client too.
            return new SJManualTCPConnection(s, s.getInputStream(), s.getOutputStream(), this) {
                @Override
                public String getTransportName() {
                    return TRANSPORT_NAME;
                }

                @Override
                public boolean supportsBlocking() {
                    return true;
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
        return new AsyncManualTCPSelector(thread, this);
    }

    @Override
    public boolean supportsBlocking() {
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
