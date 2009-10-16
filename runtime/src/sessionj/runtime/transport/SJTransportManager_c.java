/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import sessionj.runtime.SJRuntimeException;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import static sessionj.runtime.util.SJRuntimeUtils.*;

import java.util.*;

/**
 * @author Raymond
 *
 */
public class SJTransportManager_c extends SJTransportManager
{	
	private static final String DEFAULT_SETUPS_PROPERTY = "sessionj.default.negociationTrans";
    private static final String DEFAULT_TRANSPORTS_PROPERTY = "sessionj.default.transports";
    //private static final boolean DEBUG = true;
	private static final boolean DEBUG = false;
	
	private final List<SJTransport> negociationTrans = new LinkedList<SJTransport>(); // These may need some synchronisation (getters and setters are currently public and non-defensive).
	private final List<SJTransport> sessionTrans = new LinkedList<SJTransport>();
	
	private final List<String> snames = new LinkedList<String>(); // Used in negotiation protocol.
	private final List<String> tnames = new LinkedList<String>();
	
	//private List<SJSetupNegotiator> clientNegotiators = new LinkedList<SJSetupNegotiator>();
	
	private final Map<Integer, SJAcceptorThreadGroup> acceptorGroups = new HashMap<Integer, SJAcceptorThreadGroup>();

    /*private HashMap<String, SJConnection> connections = new HashMap<String, SJConnection>();
     private HashMap<SJConnection, Integer> sessions = new HashMap<SJConnection, Integer>();*/
	
	public SJTransportManager_c() 
	{ 
		defaultSetups();
		defaultTransports();
	}	
	
	private void defaultSetups()
	{
        List<SJTransport> ss = TransportUtils.parseTransportFlags(
            System.getProperty(DEFAULT_SETUPS_PROPERTY, "d")
        );
        //ss.add(new SJFifoPair());
        // FIXME: need to prevent conflicting use of (shared memory) ports by multiple Runtimes on the same host.
        // Currently relying on SJStreamTCP to be a mandatory setup.
		//ss.add(new SJStreamTCP());
		//ss.add(new SJManualTCP());
		//ss.add(new SJHTTP());
		//ss.add(new SJUDP());
		
		configureNegociationTransports(ss);
	}
	
	private void defaultTransports()
	{
        List<SJTransport> ts = TransportUtils.parseTransportFlags(
            System.getProperty(DEFAULT_TRANSPORTS_PROPERTY, "d")
        );
		
		configureSessionTransports(ts);
	}
	
	public SJAcceptorThreadGroup openAcceptorGroup(int port, SJSessionParameters params) throws SJIOException 
	{		
		if (params.useDefault()) 
		{
			//synchronized (this)
			synchronized (negociationTrans) // Worth taking defensive copies instead?
			{
				synchronized (sessionTrans)
				{
					synchronized (snames)
					{
						return openAcceptorGroup(port, negociationTrans, sessionTrans, snames, params.getBoundedBufferSize());
					}
				}
			}
		}
		else
		{
			List<SJTransport> ss = params.getSetups(); // Has an implicit defensive copy. Maybe find a way to avoid that from here.
			List<SJTransport> ts = params.getTransports();				
			
			List<String> sn = new LinkedList<String>();
			
			for (SJTransport t : ss)
			{
				sn.add(t.getTransportName());
			}
			
			return openAcceptorGroup(port, ss, ts, sn, params.getBoundedBufferSize());
		}
	}
		
	private SJAcceptorThreadGroup openAcceptorGroup(int port, List<SJTransport> ss, List<SJTransport> ts, List<String> sn, int boundedBufferSize) throws SJIOException // Synchronized where necessary from calling scope. 
	{

        if (DEBUG)
		{
			System.out.println("[SJTransportManager_c] Openening acceptor group: " + port);
		}
		
		synchronized (acceptorGroups)
		{								
			if (acceptorGroups.keySet().contains(port)) // FIXME: checks this transport manager hasn't already used the session p, but the session p could be used by another transport manager on the same machine, or the underlying transport p might not be available.
			{
				throw new SJIOException("[SJTransportManager_c] Port already in use: " + port);
			}
			
			String name = ((Integer) port).toString(); // Factor out threadgroup name scheme.
			
			SJAcceptorThreadGroup atg = new SJAcceptorThreadGroup(this, port, name); 
			
			List<SJSetupThread> sts = new LinkedList<SJSetupThread>();			
			
			for (SJTransport t : ss)
			{			
				try
				{
					SJSetupThread st;
					
					if (t instanceof SJBoundedFifoPair) // FIXME: currently hacked. Should there be a SJBoundedBufferTransport?
					{
						st = new SJSetupThread(atg, ((SJBoundedFifoPair) t).openAcceptor(t.sessionPortToSetupPort(port), boundedBufferSize));
					}
					else
					{
						st = new SJSetupThread(atg, t.openAcceptor(t.sessionPortToSetupPort(port)));
					}
					
					st.start();
					
					if (DEBUG)
					{
						System.out.println("[SJTransportManager_c] " + t.getTransportName() + " setup ready on: " + port + "(" + t.sessionPortToSetupPort(port) + ")");
					}
					
					sts.add(st);
				}
				catch (SJIOException ioe) // Need to close the failed acceptor?
				{
					if (DEBUG)
					{
						System.out.println("[SJTransportManager_c] " + ioe);
					}
					
					for (SJSetupThread foo : sts)
					{
						foo.close();
					}
					
					throw new SJSetupException("[SJTransportManager_c] Setup \"" + t.getTransportName() + "\" could not be opened for session port: " + port, ioe);
				}
			}
			
			if (sts.isEmpty()) 
			{
				throw new SJIOException("[SJTransportManager_c] No valid negociationTrans: " + port);
			}
			
			List<SJAcceptorThread> ats = new LinkedList<SJAcceptorThread>(); 
			
			for (SJTransport t : ts)
			{
				if (sn.contains(t.getTransportName()))
				{
					atg.addTransport(t.getTransportName(), -1 * t.sessionPortToSetupPort(port)); // Marked as minus to show it's a setup port (transport connection needs to send NEGOTIATION_DONE). // Reuse the setup port for accepting (negotiated) transport connections.
				}
				else
				{
					try
					{
						int foo = t.getFreePort(); // FIXME: need to lock the transport until after we have properly claimed the port. Either after we have opened the socket (or whatever, or need to manually manage which ports are free for each transport. But even this is not enough, another process (e.g. any program opening TCP ports) may steal the port, so we may need to retry. 				
						
						SJAcceptorThread at;
						
						if (t instanceof SJBoundedFifoPair) // FIXME: currently hacked. Should there be a SJBoundedBufferTransport?
						{
							at = new SJAcceptorThread(atg, ((SJBoundedFifoPair) t).openAcceptor(foo, boundedBufferSize));
						}
						else
						{
							at = new SJAcceptorThread(atg, t.openAcceptor(foo));
						}
						
						at.start();
						
						if (DEBUG)
						{
							System.out.println("[SJTransportManager_c] " + t.getTransportName() + " transport ready on: " + foo);
						}						
						
						ats.add(at);
						
						atg.addTransport(t.getTransportName(), foo);
					}
					catch (SJIOException ioe) // Need to close the failed acceptor?
					{
						if (DEBUG)
						{
							System.out.println("[SJTransportManager_c] " + ioe);
						}
					}
				}
			}
			
			if (ats.isEmpty() && atg.getTransports().isEmpty())
			{
				throw new SJIOException("[SJTransportManager_c] No valid acceptors: " + port);
			}
			
			acceptorGroups.put(port, atg);
			
			if (DEBUG)
			{
				System.out.println("[SJTransportManager_c] Opened acceptor group: " + port);
			}
			
			return atg;
		}		
	}
	
	public void closeAcceptorGroup(int port)
	{
		synchronized (acceptorGroups)
		{
			acceptorGroups.remove(port).close();
		}
	}
	
	//public SJConnection openConnection(SJServerIdentifier si, boolean multiplex) throws SJIOException
	/*public SJConnection openConnection(SJServerIdentifier si) throws SJIOException
	{		
		return openConnection(si.getHostName(), si.getPort());   
	}
	
	public SJConnection openConnection(String hostName, int port) throws SJIOException
	{
		return openConnection(hostName, port, SJSessionParameters.DEFAULT_PARAMETERS);
	}*/
	
	public /*synchronized */SJConnection openConnection(String hostName, int port, SJSessionParameters params) throws SJIOException
	{
		SJConnection conn;
	
		/*synchronized (connections)
		{		
			/*if (connections.keySet().contains(si))
			{
				conn = connections.get(si); 
			
				sessions.put(conn, new Integer(sessions.get(conn).intValue() + 1));
			} 
			else*
			{*/
				if (params.useDefault())
				{  							
					//synchronized (this)
					synchronized (negociationTrans) // Worth taking defensive copies instead?
					{ 
						synchronized (sessionTrans)
						{
							synchronized (tnames)
							{
								//conn = clientNegotiation(hostName, port, negociationTrans, sessionTrans, tnames);
								conn = clientNegotiation(hostName, port, negociationTrans, sessionTrans, tnames, params.getBoundedBufferSize()); // FIXME: make more general to allow additional parameters, such as the maximum bounded buffer size, to be passed (currently hacked for SJBoundedFifoPair). // Should be unbounded buffer by default.
							}
						}
					}
				}
				else
				{
					List<SJTransport> ss = params.getSetups();
					List<SJTransport> ts = params.getTransports();
					
					List<String> tn = new LinkedList<String>();
					
					for (SJTransport t : ts)
					{
						tn.add(t.getTransportName());
					}
					
					//conn = clientNegotiation(hostName, port, ss, ts, tn);
					conn = clientNegotiation(hostName, port, ss, ts, tn, params.getBoundedBufferSize()); // FIXME: make more general to allow additional parameters, such as the maximum bounded buffer size, to be passed (currently hacked for SJBoundedFifoPair).
				}				 
				
				if (conn == null)
				{
					throw new SJIOException("[SJTransportManager_c] Connection failed: " + hostName + ":" + port);
				}				
				
				if(DEBUG)
				{
					System.out.println("[SJTransportManager_c] Connected on " + conn.getLocalPort() + " to " + hostName + ":" + port + " (" + conn.getPort() + ") using: " + conn.getTransportName());
				}					
				
				registerConnection(conn);
			/*}
		}*/
		
		return conn;
	}
	
	public /*synchronized */void closeConnection(SJConnection conn) // FIXME: add in close delay to allow connection reuse.
	{
		/*synchronized (sessions)
		{
			int active = sessions.get(conn).intValue() - 1;
			
			if (active == 0)
			{
				sessions.remove(conn);*/			
				conn.disconnect();
			/*}
			else
			{
				sessions.put(conn, new Integer(active));
			}
		}*/
	}
	
	//public List<SJConnectionSetup> registeredNegociationTransports()
	public /*synchronized*/ List<SJTransport> registeredNegociationTransports()
	{
		synchronized (negociationTrans)
		{
			return new LinkedList<SJTransport>(negociationTrans); // Not sure if need to synchronize on negociationTrans here.
		}
	}
	
	//public void setRegisteredSetups(List<SJConnectionSetup> negociationTrans)
	public /*synchronized*/ void configureNegociationTransports(List<SJTransport> transports)
	{
		synchronized (negociationTrans)
		{
			synchronized (snames)
			{		
				negociationTrans.clear();
                negociationTrans.addAll(transports); // Not sure if need to synchronize on negociationTrans argument here.
				
				//synchronized (snames)
                snames.clear();

                for (SJTransport t : transports)
                {
                    snames.add(t.getTransportName());
                }
            }
		}
	}	
	
	public /*synchronized*/ List<SJTransport> registeredSessionTransports()
	{
		synchronized (sessionTrans)
		{
			return new LinkedList<SJTransport>(sessionTrans);
		}
	}
	
	public /*synchronized*/ void configureSessionTransports(List<SJTransport> transports)
	{
		synchronized (sessionTrans)
		{
			synchronized (tnames)
			{
                sessionTrans.clear();
                sessionTrans.addAll(transports);
				
				//synchronized (tnames)
                tnames.clear();

                for (SJTransport t : transports)
                {
                    tnames.add(t.getTransportName());
                }
            }
		}
	}			
	
	protected void registerConnection(SJConnection conn)
	{
		/*synchronized (connections)
		{
			connections.put(conn.getHostName(), conn);
			
			synchronized (sessions)
			{				
				sessions.put(conn, new Integer(1));
			}
		}*/
	}
	
	protected boolean serverNegotiation(SJAcceptorThreadGroup atg, SJConnection conn) throws SJIOException
	{
		Map<String, Integer> tn = atg.getTransports();
		
		boolean transportAgreed;  
		
		String sname = conn.getTransportName();
		
		if (tn.containsKey(sname)) // FIXME: extend negotiation protocol with a SJ_SERVER_TRANSPORT_FORCE? Means the Client must reuse the setup. If the Client doesn't want it, can we rely on an implicit connection fail? (Maybe need a complementary SJ_CLIENT_NO_FORCE which means the same as NEGOTIATION_START, but no force option permitted.) May be useful for e.g. SJHTTPProxyServlet.   
		{
			if (tn.size() == 1) // If Server doesn't have any other transports, or for any other reason. // FIXME: maybe integrate into the ATI. Currently only used by SJHTTPProxyServlet.
			{
				conn.writeByte(SJ_SERVER_TRANSPORT_FORCE); 
				conn.flush();
				
				if (DEBUG)
				{
					System.out.println("[SJTransportManager_c] SJ_SERVER_TRANSPORT_FORCE: " + sname);
				}
				
				if (conn.readByte() == SJ_CLIENT_TRANSPORT_NO_FORCE) // Negotiation has failed.
				{
					throw new SJIOException("[SJTransportManager_c] Server supports no other transports: " + sname); 
				}
				
				transportAgreed = true;
			}
			else
			{
				conn.writeByte(SJ_SERVER_TRANSPORT_SUPPORTED);
				conn.flush();
				
				if (DEBUG)
				{
					System.out.println("[SJTransportManager_c] SJ_SERVER_TRANSPORT_SUPPORTED: " + sname);
				}
				
				transportAgreed = conn.readByte() == SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED;
			}
		}
		else
		{
			conn.writeByte(SJ_SERVER_TRANSPORT_NOT_SUPPORTED); 
			conn.flush();

			if (DEBUG)
			{
				System.out.println("[SJTransportManager_c] SJ_SERVER_TRANSPORT_NOT_SUPPORTED: " + sname);
			}
			
			conn.readByte(); // Doesn't matter if Client wants this transport or not. 
			
			transportAgreed = false;
		}
		
		boolean reuse = transportAgreed;
		
		if (!transportAgreed) // Start main negotiations.
		{

            // First send our transports (Client is also sending theirs now).

            byte[] bs = serializeObject(tn);

            if (DEBUG)
			{
				System.out.println("[SJTransportManager_c] Sending server transport configuration to: " + conn.getHostName() + ": " + conn.getPort());
			}
			
			conn.writeBytes(serializeInt(bs.length)); 
			conn.writeBytes(bs);
			
			// Now receive Client's transports.
			
			bs = new byte[SJ_SERIALIZED_INT_LENGTH];
			
			conn.readBytes(bs);				
	
			int len = deserializeInt(bs);
			
			bs = new byte[len];
			conn.readBytes(bs); 
			
			List<String> desiredTransports = (List<String>) deserializeObject(bs); // Currently unused (see clientNegotiation).
		
			// Find out if Client wants to reuse the setup connection; otherwise the Client will connect to a different acceptor thread.
			
			reuse = conn.readByte() == REUSE_SETUP_CONNECTION;
		}
		
		return reuse; 		
	}
	
	protected SJConnection clientNegotiation(String hostName, int port, List<SJTransport> ss, List<SJTransport> ts, List<String> tn, int boundedBufferSize) throws SJIOException // Synchronized where necessary from calling scope.
	{
		SJConnection conn = null;
		
		for (SJTransport t : ss)
		{
			try
			{
				if (t instanceof SJBoundedFifoPair) // FIXME: currently hacked. Should there be a SJBoundedBufferTransport? 
				{
					conn = ((SJBoundedFifoPair) t).connect(t.sessionHostToNegociationHost(hostName), t.sessionPortToSetupPort(port), boundedBufferSize);
				}
				else 
				{
					conn = t.connect(t.sessionHostToNegociationHost(hostName), t.sessionPortToSetupPort(port));
				}
				
				if (DEBUG)
				{
					System.out.println("[SJTransportManager_c] Setting up on " + conn.getLocalPort() + " to " + hostName + ":" + port + " using: " + t.getTransportName());									
				}
				
				break;
			}
			catch (SJIOException ioe)
			{	
				if (DEBUG)
				{
					//ioe.printStackTrace();
					System.out.println("[SJTransportManager_c] " + t.getTransportName() + " setup failed: " + ioe.getMessage());
				}
			}		
		}						
		
		if (conn == null)
		{
			throw new SJIOException("[SJTransportManager_c] Setup failed: " + hostName + ':' + port);
		}			
				
		boolean transportAgreed;
		
		String sname = conn.getTransportName();
		
		if (sname.equals(ts.get(0).getTransportName()))  
		{
			conn.writeByte(SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED);
			conn.flush();

			if (DEBUG)
			{
				System.out.println("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED: " + sname);
			}
			
			byte b = conn.readByte();
			
			transportAgreed = b == SJ_SERVER_TRANSPORT_SUPPORTED || b == SJ_SERVER_TRANSPORT_FORCE;
		}
		else 
		{
			if (!tn.contains(sname))
			{
				conn.writeByte(SJ_CLIENT_TRANSPORT_NO_FORCE); // Should be sent if the setup isn't a Client transport. 
				conn.flush();				
				
				if (DEBUG)
				{
					System.out.println("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NO_FORCE: " + sname);
				}
				
				if (conn.readByte() == SJ_SERVER_TRANSPORT_FORCE) // Negotiation has failed.
				{
					throw new SJIOException("[SJTransportManager_c] Client does not support this transport: " + sname); 
				}
				
				transportAgreed = false;
			}
			else
			{
				conn.writeByte(SJ_CLIENT_TRANSPORT_NEGOTIATION_START);
				conn.flush();
				
				if (DEBUG)
				{
					System.out.println("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NEGOTIATION_START: " + sname);
				}
				
				byte b = conn.readByte();

				// FIXME: currently, only comes from SJHTTPProxyServlet, but need to prepare for it more generally.
                transportAgreed = b == SJ_SERVER_TRANSPORT_FORCE;
			}
		}
		
		if (!transportAgreed) // Start main negotiations.
		{
            // Send our transports.

            byte[] bs = serializeObject(tn);

            conn.writeBytes(serializeInt(bs.length));
			conn.writeBytes(bs); // This is currently pointless - Server has the same reply in any case.
			
			// Receive Server's transports.
			
			bs = new byte[SJ_SERIALIZED_INT_LENGTH];		
			conn.readBytes(bs);		
			
			int len = deserializeInt(bs);
			
			bs = new byte[len];		
			conn.readBytes(bs);
			
			Map<String, Integer> servers = (Map<String, Integer>) deserializeObject(bs);
			
			if (DEBUG)
			{
				System.out.println("[SJTransportManager_c] Server at " + hostName + ':' + port + " offers: " + servers);
			}		
			
			for (SJTransport t : ts)
			{
				try
				{
					String name = t.getTransportName();
									
					if (sname.equals(name) && servers.get(name) != null)
					{
						conn.writeByte(REUSE_SETUP_CONNECTION);
						conn.flush();
						
						transportAgreed = true;
						
						break;
					}
					
					if (servers.containsKey(name))
					{
						int p = servers.get(name);
						
						//SJConnection tmp = t.connect(t.sessionHostToNegociationHost(hostName), Math.abs(p));
						
						SJConnection tmp;
						
						if (t instanceof SJBoundedFifoPair) // FIXME: currently hacked. Should there be a SJBoundedBufferTransport? 
						{
							tmp = ((SJBoundedFifoPair) t).connect(t.sessionHostToNegociationHost(hostName), p < 0 ? -1 * p : p, boundedBufferSize);
						}
						else // The original case. 
						{
							tmp = t.connect(t.sessionHostToNegociationHost(hostName), p < 0 ? -1 * p : p);
						}
						
						if (p < 0) // Connected to a setup, so bypass the preliminary negotiation phase.
						{
							tmp.writeByte(SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED);
							tmp.flush();
						
							byte b = tmp.readByte();
							
							if (!(b == SJ_SERVER_TRANSPORT_SUPPORTED || b == SJ_SERVER_TRANSPORT_FORCE))
							{
								throw new SJRuntimeException("[SJTransportManager_c] Shouldn't get in here: " + b);
							}
						}
						
						conn.writeByte(CLOSE_SETUP_CONNECTION); // i.e. Not reusing it.
						conn.flush();
						
						conn.disconnect();														
						
						conn = tmp;																				
						
						transportAgreed = true;
						
						break;
					}
				}
				catch (SJIOException ioe)
				{
					if (DEBUG)
					{
						//ioe.printStackTrace();
						System.out.println("[SJTransportManager_c] Transport connection failed: " + ioe.getMessage());
					}
				}					
			}
		}
		
		return transportAgreed ? conn : null;
	}
}
