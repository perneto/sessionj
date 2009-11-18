package sessionj.runtime.transport.sharedmem;

import sessionj.runtime.SJIOException;
import sessionj.runtime.net.SJSelectorInternal;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJLocalConnection;
import sessionj.runtime.transport.SJTransport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * @author Raymond
 *
 */
public class SJFifoPairWithSelector implements SJTransport
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.sharedmem.SJFifoPair";

	private static final int LOWER_PORT_LIMIT = 1024;
	private static final int PORT_RANGE = 65535 - 1024;

	private static final Set<Integer> portsInUse = new HashSet<Integer>();

	public SJFifoPairWithSelector() { }

	public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		SJFifoPairAcceptor a = new SJFifoPairAcceptor(port);

		SJFifoPairWithSelector.bindPort(port);

		return a;
	}

	/*public SJFifoPairConnection connect(SJServerIdentifier si) throws SJIOException
	{
		return connect(si.getHostName(), si.getPort());
	}*/

	public SJFifoPairConnection connect(String hostName, int port) throws SJIOException
	{
		// FIXME: need to map session-level addresses to transport specific values.

		try
		{
			if (notLocalHost(hostName))
			{
				throw new SJIOException('[' + getTransportName() + "] Connection not valid: " + hostName + ':' + port);
			}
		}
        catch (UnknownHostException e) {
            throw new SJIOException(e);
        }

		if (!portInUse(port))
		{
			throw new SJIOException('[' + getTransportName() + "] Port not open: " + port);
		}

		int localPort = getFreePort();

		bindPort(localPort); // Can the connection establishment after this fail? Would need to free the port.

		SJFifoPairConnection ourConn = new SJFifoPairConnection(hostName, port, localPort, new LinkedList<Object>());

		SJFifoPairAcceptor.addRequest(port, ourConn);

		boolean[] b = ourConn.hasBeenAccepted; // FIXME: Object will do (only need a lock for synchronisation).

		synchronized (b)
		{
			try
			{
				while (!b[0])
				{
					b.wait();
				}
			}
			catch (InterruptedException ie)
			{
				throw new SJIOException('[' + getTransportName() + "] 2: " + ie);
			}
    }

		return ourConn;
	}

    public SJSelectorInternal transportSelector() {
        return null; // TODO
    }

    public boolean blockingModeSupported() {
        return true;
    }

    private boolean notLocalHost(String hostName) throws UnknownHostException {
        // FIXME: check properly. We're now using IP addresses rather than host names.
        return !(hostName.equals("127.0.0.1")
            || hostName.equals(InetAddress.getLocalHost().getHostAddress())
            || hostName.equals("localhost")
            || hostName.equals(InetAddress.getLocalHost().getHostName()));
    }

    public boolean portInUse(int port)
	{
		return !portFree(port);
	}

	public int getFreePort() throws SJIOException
	{
		return findFreePort();
	}

	public String getTransportName()
	{
		return TRANSPORT_NAME;
	}

	public static boolean portFree(int port)
	{
		synchronized (portsInUse)
		{
			return !portsInUse.contains(port);
		}
	}

	protected static int findFreePort() throws SJIOException
	{
		int start = new Random().nextInt(PORT_RANGE);
		int seed = start + 1;

		for (int port = seed % PORT_RANGE; port != start; port = seed++ % PORT_RANGE)
		{
			if (portFree(port + LOWER_PORT_LIMIT))
			{
				return port + LOWER_PORT_LIMIT;
			}
		}

		throw new SJIOException('[' + TRANSPORT_NAME + "] No free port available.");
		//throw new SJIOException("[SJ(Bounded)FifoPair] No free port available.");
	}

	protected static void bindPort(int port)
	{
		synchronized (portsInUse)
    {
	    portsInUse.add(port);
    }
	}

	protected static void freePort(int port)
	{
		synchronized (portsInUse)
    {
	    portsInUse.remove(port);
    }
	}

	public String sessionHostToNegociationHost(String hostName)
	{
		return hostName;
	}

	public int sessionPortToSetupPort(int port)
	{
		return port;
	}
}