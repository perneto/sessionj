/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import sessionj.runtime.SJRuntimeException;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.httpservlet.SJHTTPServlet;
import sessionj.runtime.transport.sharedmem.SJBoundedFifoPair;
import sessionj.runtime.transport.sharedmem.SJFifoPair;
import sessionj.runtime.transport.tcp.SJAsyncManualTCP;
import sessionj.runtime.transport.tcp.SJManualTCP;
import sessionj.runtime.transport.tcp.SJStreamTCP;
import static sessionj.runtime.util.SJRuntimeUtils.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Raymond
 *
 */
public class SJTransportManager_c extends SJTransportManager
{
    private static final Logger logger = Logger.getLogger(SJTransportManager_c.class.getName());

	private static final String DEFAULT_SETUPS_PROPERTY = "sessionj.transports.negociation";
    private static final String DEFAULT_TRANSPORTS_PROPERTY = "sessionj.transports.session";
	private static final boolean DEBUG = true;
	
	private final Map<Integer, SJAcceptorThreadGroup> acceptorGroups = new HashMap<Integer, SJAcceptorThreadGroup>();
    
    // LinkedHashMap to retain the insertion order, used to define transport preference.
    private final Map<Character, SJTransport> activeSessionTransports = new LinkedHashMap<Character, SJTransport>();
    private final Map<Character, SJTransport> activeNegotiationTransports = new LinkedHashMap<Character, SJTransport>();
    private final String defaultSessionTransports;
    private final String defaultNegotiationTransports;
    
	public SJTransportManager_c() throws SJIOException {
        defaultNegotiationTransports = getDefault(DEFAULT_SETUPS_PROPERTY);
        defaultSessionTransports = getDefault(DEFAULT_TRANSPORTS_PROPERTY);
        initDefaultNegotiationTransports();
		initDefaultSessionTransports();
	}

    private String getDefault(String key) {
        String s = System.getProperty(key, "d");
        if (s.equals("d")) s = "fsa";
        return s;
    }

    private void initDefaultNegotiationTransports() throws SJIOException {
        loadNegotiationTransports(defaultNegotiationTransports);
        debug("Default negotiation transports: " + defaultNegotiationTransports 
            + ": "+ activeNegotiationTransports);
	}

    private void initDefaultSessionTransports() throws SJIOException {
        loadSessionTransports(defaultSessionTransports);
        debug("Default session transports: " + defaultSessionTransports 
            + ": " + activeSessionTransports);
    }

    public List<SJTransport> defaultSessionTransports() {
        List<SJTransport> defaults = new LinkedList<SJTransport>();
        for (char c : defaultSessionTransports.toCharArray()) {
            defaults.add(activeSessionTransports.get(c));
        }
        return defaults;
    }

    public List<SJTransport> loadNegotiationTransports(String transportLetterCodes) throws SJIOException {
        List<SJTransport> ts = new LinkedList<SJTransport>();
        for (char c : transportLetterCodes.toCharArray()) {
            SJTransport t = activeNegotiationTransports.get(c);
            if (t == null) {
                t = createTransport(c);
                activeNegotiationTransports.put(c, t);
            }
            ts.add(t);
        }
        return ts;
    }
    
    public List<SJTransport> loadSessionTransports(String transportLetterCodes) throws SJIOException {
        List<SJTransport> ts = new LinkedList<SJTransport>();
        for (char c : transportLetterCodes.toCharArray()) {
            SJTransport t = activeSessionTransports.get(c);
            if (t == null) {
                t = createTransport(c);
                activeSessionTransports.put(c, t);
            }
            ts.add(t);
        }
        return ts;
    }

    private SJTransport createTransport(char code) throws SJIOException {
        switch (code) {
            case 'f':
                return new SJFifoPair();
            case 'b':
                return new SJBoundedFifoPair();
            case 's':
                return new SJStreamTCP();
            case 'a':
                try {
                    return new SJAsyncManualTCP();
                } catch (IOException e) {
                    throw new SJIOException(e);
                }
            case 'm':
                return new SJManualTCP();
            case 'h':
                return new SJHTTPServlet();
        }

        throw new SJIOException("Unsupported transport code: " + code);
    }

    public SJAcceptorThreadGroup openAcceptorGroup(int port, SJSessionParameters params) throws SJIOException 
	{
        List<SJTransport> ss = params.getNegotiationTransports();
        List<SJTransport> ts = params.getSessionTransports();
        Collection<String> sn = transportNames(ss);

        return openAcceptorGroup(port, ss, ts, sn, params.getBoundedBufferSize());
	}

    private List<String> transportNames(Iterable<SJTransport> ss) {
        // Retrieve negotiation transport names.
        List<String> sn = new LinkedList<String>();
        for (SJTransport t : ss) sn.add(t.getTransportName());
        return sn;
    }

    private SJAcceptorThreadGroup openAcceptorGroup(int port, Iterable<SJTransport> ss, Iterable<SJTransport> ts, Collection<String> sn, int boundedBufferSize) throws SJIOException // Synchronized where necessary from calling scope.
	{
        debug("[SJTransportManager_c] Openening acceptor group: " + port);

        synchronized (acceptorGroups)
		{								
			if (acceptorGroups.keySet().contains(port)) 
            // FIXME: checks this transport manager hasn't already used the session p, but 
            // the session p could be used by another transport manager on the same machine,
            // or the underlying transport p might not be available.
			{
				throw new SJIOException("[SJTransportManager_c] Port already in use: " + port);
			}
			
			String name = ((Integer) port).toString(); // FIXME: Factor out threadgroup name scheme.
			
			SJAcceptorThreadGroup atg = new SJAcceptorThreadGroup(this, port, name); 
			
			Collection<SJSetupThread> sts = new LinkedList<SJSetupThread>();			
			
			for (SJTransport t : ss) 
                sts.add(openAcceptorForNegotiation(port, boundedBufferSize, atg, sts, t));
			
			if (sts.isEmpty())
                throw new SJIOException("[SJTransportManager_c] No valid negociationTrans: " + port);
			
			Collection<SJAcceptorThread> ats = new LinkedList<SJAcceptorThread>(); 
			
			for (SJTransport t : ts)
			{
				if (sn.contains(t.getTransportName()))
				{
					atg.addTransport(t.getTransportName(), -1 * t.sessionPortToSetupPort(port)); // Marked as minus to show it's a setup port (transport connection needs to send NEGOTIATION_DONE). // Reuse the setup port for accepting (negotiated) transport connections.
				}
				else 
				{
                    SJAcceptorThread thread = openAcceptorForSession(boundedBufferSize, atg, t);
                    if (thread != null) ats.add(thread);
                }
			}
			
			if (ats.isEmpty() && atg.getTransports().isEmpty())
				throw new SJIOException("[SJTransportManager_c] No valid acceptors: " + port);
		
			acceptorGroups.put(port, atg);

            debug("[SJTransportManager_c] Opened acceptor group: " + port);
			return atg;
		}		
	}

    private void debug(String msg) {
        if (DEBUG) System.out.println(msg);
    }

    private SJAcceptorThread openAcceptorForSession(int boundedBufferSize, SJAcceptorThreadGroup atg, SJTransport t) {
        try {
            int freePort = t.getFreePort(); 
            // FIXME: need to lock the transport until after we have properly claimed the port. 
            // Either after we have opened the socket (or whatever, or need to manually manage 
            // which ports are free for each transport. But even this is not enough, another process
            // (e.g. any program opening TCP ports) may steal the port, so we may need to retry.

            SJAcceptorThread at;

            if (t instanceof SJBoundedFifoPair) 
            // FIXME: currently hacked. Should there be a SJBoundedBufferTransport?
            {
                at = new SJAcceptorThread(atg, ((SJBoundedFifoPair) t).openAcceptor(freePort, boundedBufferSize));
            } else {
                at = new SJAcceptorThread(atg, t.openAcceptor(freePort));
            }

            at.start();

            debug("[SJTransportManager_c] " + t.getTransportName() + " transport ready on: " + freePort);

            atg.addTransport(t.getTransportName(), freePort);
            return at;
        }
        catch (SJIOException ioe) // Need to close the failed acceptor?
        {
            debug("[SJTransportManager_c] " + ioe);
            return null;
        }
    }

    private SJSetupThread openAcceptorForNegotiation(int port, int boundedBufferSize, SJAcceptorThreadGroup atg, Iterable<SJSetupThread> sts, SJTransport t) throws SJSetupException {
        try {
            SJSetupThread st;

            if (t instanceof SJBoundedFifoPair) // FIXME: currently hacked. Should there be a SJBoundedBufferTransport?
            {
                st = new SJSetupThread(atg, ((SJBoundedFifoPair) t).openAcceptor(t.sessionPortToSetupPort(port), boundedBufferSize));
            } else {
                st = new SJSetupThread(atg, t.openAcceptor(t.sessionPortToSetupPort(port)));
            }

            st.start();

            debug("[SJTransportManager_c] " + t.getTransportName() + " setup ready on: " + port + "(" + t.sessionPortToSetupPort(port) + ")");

            return st;
        }
        catch (SJIOException ioe) // Need to close the failed acceptor?
        {
            debug("[SJTransportManager_c] " + ioe);

            for (SJSetupThread setupThread : sts) {
                setupThread.close();
            }

            throw new SJSetupException("[SJTransportManager_c] Setup \"" + t.getTransportName() + "\" could not be opened for session port: " + port, ioe);
        }
    }

    public void closeAcceptorGroup(int port)
	{
		synchronized (acceptorGroups)
		{
			acceptorGroups.remove(port).close();
		}
	}
	
	public SJConnection openConnection(String hostName, int port, SJSessionParameters params) throws SJIOException
	{
        List<SJTransport> ss = params.getNegotiationTransports();
        List<SJTransport> ts = params.getSessionTransports();
        List<String> tn = transportNames(ts);

        SJConnection conn = clientNegotiation(hostName, port, ss, ts, tn, params.getBoundedBufferSize());

        if (conn == null)
            throw new SJIOException("[SJTransportManager_c] Connection failed: " + hostName + ":" + port);

        debug("[SJTransportManager_c] Connected on " + conn.getLocalPort() + " to " + hostName + ":" + port + " (" + conn.getPort() + ") using: " + conn.getTransportName());

        registerConnection(conn);
		
		return conn;
	}
	
	public void closeConnection(SJConnection conn) // FIXME: add in close delay to allow connection reuse.
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

    public Collection<SJTransport> activeNegotiationTransports() {
        synchronized (activeNegotiationTransports)
        {
            return Collections.unmodifiableCollection(activeNegotiationTransports.values());
        }
    }

    public Collection<SJTransport> activeSessionTransports()
	{
		synchronized (activeSessionTransports)
		{
			return Collections.unmodifiableCollection(activeSessionTransports.values());
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
				
				debug("[SJTransportManager_c] SJ_SERVER_TRANSPORT_FORCE: " + sname);
				
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
				
				debug("[SJTransportManager_c] SJ_SERVER_TRANSPORT_SUPPORTED: " + sname);
				
				transportAgreed = conn.readByte() == SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED;
			}
		}
		else
		{
			conn.writeByte(SJ_SERVER_TRANSPORT_NOT_SUPPORTED); 
			conn.flush();

			debug("[SJTransportManager_c] SJ_SERVER_TRANSPORT_NOT_SUPPORTED: " + sname);
			
			conn.readByte(); // Doesn't matter if Client wants this transport or not. 
			
			transportAgreed = false;
		}
		
		boolean reuse = transportAgreed;
		
		if (!transportAgreed) // Start main negotiations.
		{
            // First send our transports (Client is also sending theirs now).

            byte[] bs = serializeObject(tn);

            debug("[SJTransportManager_c] Sending server transport configuration to: " + conn.getHostName() + ": " + conn.getPort());
			
			conn.writeBytes(serializeInt(bs.length)); 
			conn.writeBytes(bs);
			
			// Now receive Client's transports.
			
			bs = new byte[SJ_SERIALIZED_INT_LENGTH];
			
			conn.readBytes(bs);				
	
			int len = deserializeInt(bs);
			
			bs = new byte[len];
			conn.readBytes(bs); 
			
			List<String> desiredTransports = (List<String>) deserializeObject(bs); 
            // Currently unused (see clientNegotiation).
		
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
                
                debug("[SJTransportManager_c] Setting up on " + conn.getLocalPort() + " to " + hostName + ":" + port + " using: " + t.getTransportName());									
				
				break;
			}
			catch (SJIOException ioe)
			{	
				debug("[SJTransportManager_c] " + t.getTransportName() + " setup failed: " + ioe.getMessage());
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

			debug("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NEGOTIATION_NOT_NEEDED: " + sname);
			
			byte b = conn.readByte();
			
			transportAgreed = b == SJ_SERVER_TRANSPORT_SUPPORTED || b == SJ_SERVER_TRANSPORT_FORCE;
		}
		else 
		{
			if (!tn.contains(sname))
			{
				conn.writeByte(SJ_CLIENT_TRANSPORT_NO_FORCE); // Should be sent if the setup isn't a Client transport. 
				conn.flush();				
				
				debug("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NO_FORCE: " + sname);
				
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
				
				debug("[SJTransportManager_c] SJ_CLIENT_TRANSPORT_NEGOTIATION_START: " + sname);
				
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
			
			debug("[SJTransportManager_c] Server at " + hostName + ':' + port + " offers: " + servers);
			
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
					debug("[SJTransportManager_c] Transport connection failed: " + ioe);
				}					
			}
		}
		
		return transportAgreed ? conn : null;
	}
}
