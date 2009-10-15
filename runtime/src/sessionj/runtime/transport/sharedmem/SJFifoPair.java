package sessionj.runtime.transport.sharedmem;

import java.net.*;
import java.util.*;
import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.io.IOException;

import sessionj.runtime.*;
import sessionj.runtime.net.SJSelector;
import sessionj.runtime.transport.*;

class SJFifoPairAcceptor implements SJConnectionAcceptor
{	
	private static final HashMap<Integer, LinkedList<SJFifoPairConnection>> servers = new HashMap<Integer, LinkedList<SJFifoPairConnection>>();

	private final int port;
	private boolean isClosed = false;
	
	SJFifoPairAcceptor(int port) {
		this.port = port;
		
		servers.put(port, new LinkedList<SJFifoPairConnection>());
	}
	
	public SJFifoPairConnection accept() throws SJIOException
  {
		LinkedList<SJFifoPairConnection> requests = servers.get(port);
		
		SJFifoPairConnection theirConn;
		
		synchronized (requests) // FIXME: requests can sometimes be null at this point (quite rarely). 
    {
			try
			{
				while (requests.isEmpty())
				{
					requests.wait();
				}
			}
			catch (InterruptedException ie)
			{
				throw new SJIOException('[' + getTransportName() + "] 1: " + ie, ie);
			}
			
			theirConn = requests.remove(0);	
    }		
		
		List<Object> ours = new LinkedList<Object>();
		
		int localPort = SJFifoPair.findFreePort();
		
		SJFifoPair.bindPort(localPort); // Can the connection establishment after this fail? Would need to free the port.
		
		SJFifoPairConnection ourConn = new SJFifoPairConnection(null, theirConn.getLocalPort(), localPort, ours); // FIXME: need peer hostname.
		//SJFifoPairConnection ourConn = new SJFifoPairConnection(null, theirConn.getLocalPort(), port, ours); // FIXME: need peer hostname. // Reusing server port value for local port, as in TCP. // Problem: hard to tell when need to free port after a connection is closed (don't know if server is using that port still).
		
		theirConn.setPeerFifo(ours);
		ourConn.setPeerFifo(theirConn.getOurFifo());
				
		boolean[] b = theirConn.hasBeenAccepted;
		
		synchronized (b)
    {			
			b[0] = true;
			b.notifyAll(); 
    }	
		
	  return ourConn;				
  }

	public void close()
	{
		isClosed = true;
		
		synchronized (servers)
		{
			SJFifoPair.freePort(port);
			
			servers.remove(port);						
		}			
	}
	
	public boolean interruptToClose()
	{
		return true;
	}
	
	public boolean isClosed()
	{
		return isClosed;
	}
	
	public String getTransportName()
	{
		return SJFifoPair.TRANSPORT_NAME;
	}
	
	protected static void addRequest(int port, SJFifoPairConnection conn)
	{

        synchronized (servers)
		{
			List<SJFifoPairConnection> foo = servers.get(port);
		
			synchronized (foo)
      {
				foo.add(conn);
				foo.notify();	      
      }
		}
	}
}

class SJFifoPairConnection implements SJLocalConnection
{
	private static Object EOF = new Object() {}; 
	
	private String hostName;	
	private int port;
	
	private int localPort;
	
	protected List<Object> ours;
	protected List<Object> theirs;
	
	protected boolean[] hasBeenAccepted = { false };
	
	protected SJFifoPairConnection(String hostName, int port, int localPort, List<Object> ours) {
		this.hostName = hostName;
		this.port = port;
		this.localPort = localPort;
		
		this.ours = ours;
	}
	
	public void disconnect() 
	{		
		if (theirs != null) // FIXME: need an isClosed, e.g. delegation protocol closes early.
		{
			boolean closed = false;			
			
			synchronized (theirs)
			{
				if (!theirs.isEmpty() && theirs.get(theirs.size() - 1) == EOF)
				{
					ours = theirs = null;
					
					closed = true;
				}
			}
			
			if (!closed)
			{		
				synchronized (ours) // FIXME: probably can get bad interleavings with the above synchronization between the two peers. Need a common lock to synchronize on for close.
				{			
					ours.add(ours.size(), EOF);
				}
			}
		}
		
		SJFifoPair.freePort(localPort); // This is a problem if we try to reuse server port for accepted connections a la TCP. 
	}

  public void writeByte(byte b) throws SJIOException
  {    
  	if (theirs == null) // e.g. forwarding protocol closes connections early. Remember: we are at transport level here, don't get confused by session-type level properties, e.g. writes can happen asynchronously (i.e. by both ends at the same time) and we may incorrectly try to use closed connections, etc. 
  	{
  		throw new SJIOException('[' + getTransportName() + "] Connection already closed.");
  	}
  	
  	synchronized(theirs)
  	{
  		if (!theirs.isEmpty() && theirs.get(theirs.size() - 1) == EOF)
  		{
  			throw new SJIOException('[' + getTransportName() + "] Connection closed by peer.");
  		}
  		
  		theirs.add(b);
  		theirs.notifyAll();
  	}   
  }

  public void writeBytes(byte[] bs) throws SJIOException
  {
  	if (theirs == null)  
  	{
  		throw new SJIOException('[' + getTransportName() + "] Connection already closed.");
  	}  	
  	
  	synchronized(theirs)
  	{
  		if (!theirs.isEmpty() && theirs.get(theirs.size() - 1) == EOF)
  		{
  			throw new SJIOException('[' + getTransportName() + "] Connection closed by peer.");
  		}  		
  		
  		theirs.add(bs); // FIXME: should copy-on-send. But should be OK, we're always writing the serialized messages (i.e. already copied and won't be modified)? 
  		theirs.notifyAll();
  	}
  }

  public byte readByte() throws SJIOException
  {
  	if (theirs == null)  
  	{
  		throw new SJIOException('[' + getTransportName() + "] Connection already closed.");
  	}  	
  	
  	synchronized(ours)
  	{
  		try
  		{
	  		while (ours.isEmpty())
	  		{
	  			ours.wait();
	  		}
  		}
  		catch (InterruptedException ie) 
  		{
				throw new SJIOException(ie);
			}
  		
  		Object o = ours.remove(0);
  		
  		if (o instanceof Byte) // Needed?
  		{
  			return (Byte) o;
  		}
  		else
  		{
  			throw new SJIOException('[' + getTransportName() + "] Connection closed by peer.");
  		}
  	}  	  	
  }

  public void readBytes(byte[] bs) throws SJIOException
  {  
  	if (theirs == null) 
  	{
  		throw new SJIOException('[' + getTransportName() + "] Connection already closed.");
  	}  	
  	
  	synchronized(ours)
  	{  	
	  	try
			{
				while (ours.isEmpty())
				{
					ours.wait();		
				}
			}
			catch (InterruptedException ie) 
			{
				throw new SJIOException(ie);
			}  		
		
  		//Object o = ours.remove(0);
			
  		//if (ours instanceof byte[])
  		{
  			byte[] foo = (byte[]) ours.remove(0);
  			
  			if (foo.length != bs.length) // FIXME: need to decide whether we should block until bs can be filled or what.
    		{
    			throw new SJIOException('[' + SJFifoPair.TRANSPORT_NAME + "] Bad buffer size: " + bs.length);
    		}
    		
    		System.arraycopy(foo, 0, bs, 0, bs.length); // FIXME: need to buffer any extra data that doesn't fit into bs. // FIXME: could optimise by returning foo;  			
  		}
  		/*else
  		{
  			throw new SJIOException("[" + getTransportName() + "] Connection closed by peer.");
  		}*/ 		  		
  	}
  }

  public void writeReference(Object o)
  {
  	synchronized (theirs)
    {
		  theirs.add(o);
		  theirs.notifyAll();
    }
  } 
  
  public Object readReference() throws SJIOException
  {
  	synchronized(ours)
  	{
	  	try
			{
				while (ours.isEmpty())
				{
					ours.wait();		
				}
			}
			catch (InterruptedException ie) 
			{
				throw new SJIOException(ie);
			}   		
			
  		return ours.remove(0);
  	}
  } 
  
	public void flush() throws SJIOException
  {
	  
  }
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getPort()
	{
		return port;
	}

	public int getLocalPort()
	{
		return localPort;
	}
	
	public String getTransportName()
	{
		return SJFifoPair.TRANSPORT_NAME;
	}
	
	protected List<Object> getOurFifo()
	{
		return ours;
	}
	
	protected void setPeerFifo(List<Object> theirs)
	{
		this.theirs = theirs;
	}	
}

/**
 * @author Raymond
 *
 */
public class SJFifoPair implements SJTransport 
{
	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.sharedmem.SJFifoPair";
	
	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 65535 - 1024;
	
	private static final Set<Integer> portsInUse = new HashSet<Integer>(); 
	
	public SJFifoPair() { }

	public SJConnectionAcceptor openAcceptor(int port) throws SJIOException
	{
		SJFifoPairAcceptor a = new SJFifoPairAcceptor(port);
		
		SJFifoPair.bindPort(port);
		
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

    public SJSelector transportSelector() {
        return null; // TODO
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
	
	public String sessionHostToSetupHost(String hostName)
	{
		return hostName;
	}
	
	public int sessionPortToSetupPort(int port)
	{
		return port;
	}
}
