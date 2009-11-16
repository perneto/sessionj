/**
 * 
 */
package sessionj.runtime.transport;

import java.util.*;

import sessionj.runtime.*;

/**
 * @author Raymond
 *
 */
public class SJAcceptorThreadGroup extends ThreadGroup
{
	private final SJTransportManager sjtm;
	
	private final int port; // The session port.
	
	private final Map<String, Integer> transports = new HashMap<String, Integer>();
	
	private final List<SJConnection> pending = new LinkedList<SJConnection>();
	
	private boolean isClosed = false;
	
	public SJAcceptorThreadGroup(SJTransportManager sjtm, int port, String name)
	{
		super(name);
		
		this.sjtm = sjtm;
		this.port = port;
	}
	
	/*public void start()
	{
		Thread[] ts = new Thread[activeCount()]; // Cannot use activeCount to start threads...
		
		enumerate(ts);
		
		System.out.println("1: " + Arrays.toString(ts));
		
		for (Thread t : ts) 
		{
			t.start();
		}
	}*/
	
	public void close()
	{
		isClosed = true;
		
		synchronized (pending)
		{
			pending.notifyAll();
		}
		
		Thread[] ts = new Thread[activeCount()];
		
		enumerate(ts); // A bit dodgy (see API docs). Maybe should use the recursive option?
		
		for (Thread t : ts) // Maybe some synchronization is needed.
		{
			if (t instanceof SJSetupThread)
			{
				((SJSetupThread) t).close();
			}
			else if (t instanceof SJAcceptorThread)
			{
				((SJAcceptorThread) t).close();
			}
			else // Other threads the transport implementations may have spawned themselves...
			{
				if (t != null) // Can this happen?? For some reason, the ThreadGroup API sometimes does put a null in.  
				{
					t.interrupt(); // Will close threads blocked on I/O as well as thread synchronization operations (wait, join, etc.).
				}
			}
		}				
		
		for (SJConnection conn : pending) // Synchronize?
		{
			conn.disconnect();
		}
	}
	
	void queueConnection(SJConnection c)
	{
		synchronized (pending)
		{
			pending.add(c);
			pending.notifyAll(); // notify should be enough.
		}
	}
	
	public SJConnection nextConnection() throws SJIOException
	{
		SJConnection next;
		
		synchronized (pending)
		{		
			while (pending.isEmpty())
			{
				try
				{
					pending.wait(); // Still possible for one thread to have called accept on server socket and another to close the server before the first has got to here -> deadlock.		
					
					if (isClosed) 
					{
						throw new SJIOException("[SJAcceptorThreadGroup] Group closed whilst waiting for next connection.");
					}
				}
				catch (InterruptedException ie)
				{
					//throw new SJRuntimeException(ie); // Runtime exceptions only terminate the current thread.
				}
			}
			
			next = pending.remove(0);
		}
		
		sjtm.registerConnection(next);
		
		return next;
	}
	
	public int getPort()
	{
		return port;
	}
	
	void addTransport(String name, int port)
	{
		transports.put(name, port);
	}
	
	protected Map<String, Integer> getTransports()
	{
		return transports;
	}
	
	public boolean isClosed()
	{
		return isClosed;
	}

    public SJConnectionAcceptor getAcceptorFor(String transportName) {
        Thread[] ts = new Thread[activeCount()];
        enumerate(ts);
        
        for (Thread t : ts) {
            if (t instanceof SJAcceptorThread){
                SJConnectionAcceptor acceptor = ((SJAcceptorThread) t).getAcceptorFor(transportName);
                if (acceptor != null) return acceptor;
            }
        }
        return null;
    }

}
