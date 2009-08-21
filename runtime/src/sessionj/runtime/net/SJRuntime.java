package sessionj.runtime.net;

import polyglot.types.SemanticException;
import sessionj.ExtensionInfo;
import sessionj.runtime.SJIOException;
import sessionj.runtime.SJRuntimeException;
import sessionj.runtime.session.SJManualSerializer;
import sessionj.runtime.session.SJSerializer;
import sessionj.runtime.session.SJStreamSerializer;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.SJClassResolver;
import sessionj.runtime.util.SJRuntimeTypeEncoder;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.SJSessionType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class SJRuntime 
{
	private	static final ExtensionInfo extInfo;
	private static final SJTypeSystem sjts;
	private static final SJClassResolver sjcr;
	private static final SJRuntimeTypeEncoder sjte;	
	
	private static final String localHostName;
	
	static
	{
		try
		{
			extInfo = new ExtensionInfo();
			sjts = (SJTypeSystem) extInfo.typeSystem();
			sjcr = new SJClassResolver(sjts, extInfo);
			sjts.initialize(sjcr, extInfo);
			sjte = new SJRuntimeTypeEncoder(sjts);
		}
		catch (SemanticException se)
		{
			throw new SJRuntimeException(se);
		}
		
		try
		{
			//localHostName = InetAddress.getLocalHost().getHostName(); // FIXME: problems for e.g. HZHL2 on IC-DoC. But we've starting using host names, so use them for now.
			localHostName = InetAddress.getLocalHost().getHostAddress(); // FIXME: we're actually using IP addresses now, not host names.
		}
		catch (UnknownHostException uhe)
		{
			throw new SJRuntimeException(uhe);
		}
	}	
	
	protected SJRuntime() { }

	/*public static final SJRuntime getSJRuntime()
	{
		return runtime;
	}*/
	
	/*public static final SJTransportManager getTransportManager()
	{
		return sjtm;
	}*/	
	
	public static SJTypeSystem getTypeSystem()
	{
		return sjts;
	}
	
	public static SJRuntimeTypeEncoder getTypeEncoder()
	{
		return sjte;
	}
	
	/*abstract public void openServerSocket(SJServerSocket ss) throws SJIOException;	
	abstract public void closeServerSocket(SJServerSocket ss);
	
	abstract public void connectSocket(SJRequestingSocket s) throws SJIOException;
	abstract public void closeSocket(SJAbstractSocket s); 

	abstract public void accept(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException;
	abstract public void request(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException;
	abstract public void close(SJAbstractSocket[] sockets);
	
	abstract public void send(SJAbstractSocket[] sockets, Object obj) throws SJIOException;
	abstract public void send(SJAbstractSocket[] sockets, int i) throws SJIOException;
	
	abstract public void pass(SJAbstractSocket[] sockets, Object obj) throws SJIOException; // Explicit noalias primitives not possible, and anyway cannot be "passed".
	
	abstract public void copy(SJAbstractSocket[] sockets, Object obj) throws SJIOException;
	abstract public void copy(SJAbstractSocket[] sockets, int i) throws SJIOException;
	
	abstract public Object receive(SJAbstractSocket[] sockets) throws SJIOException, ClassNotFoundException; // Remove array in a compiler pass.;	public static int receiveInt(SJAbstractSocket[] sockets) throws SJIOException;
	
	abstract public boolean recurse(SJAbstractSocket[] sockets) throws SJIOException;
	abstract public boolean outsync(SJAbstractSocket[] sockets, boolean cond) throws SJIOException;
	abstract public boolean insync(SJAbstractSocket[] sockets) throws SJIOException;
	abstract public String outlabel(SJAbstractSocket[] sockets, String lab) throws SJIOException;
	abstract public String inlabel(SJAbstractSocket[] sockets) throws SJIOException;
	abstract public boolean recursionEnter(SJAbstractSocket[] sockets);
	abstract public void recursionExit(SJAbstractSocket[] sockets);
	
	abstract public void sendChannel(SJAbstractSocket[] sockets, SJService c) throws SJIOException; // Channel objects should be immutable, so can be passed. // Remove array in a compiler pass.	
	abstract public void delegateSession(SJAbstractSocket[] sockets, SJAbstractSocket s, String encoded) throws SJIOException; 	
	abstract public SJService receiveChannel(SJAbstractSocket[] sockets, String encoded) throws SJIOException, ClassNotFoundException;
	abstract public SJAbstractSocket receiveSession(SJAbstractSocket[] sockets, String encoded) throws SJIOException, ClassNotFoundException;*/
	
	private static final int LOWER_PORT_LIMIT = 1024;
	private static final int UPPER_PORT_LIMIT = 65535;
	
	private static final SJTransportManager sjtm = new SJTransportManager_c();
	
	private static final Set<Integer> portsInUse = new HashSet<Integer>();
	
	private static final Map<SJPort, SJAcceptorThreadGroup> reservedPorts = new HashMap<SJPort, SJAcceptorThreadGroup>();
    // FIXME: need to free up unclaimed ports.
	
	//private static final Map<Integer, DatagramSocket> servers = new HashMap<Integer, DatagramSocket>();
	
	/*public static SJAcceptorThreadGroup getFreshAcceptorThreadGroup() throws SJIOException
	{
		return getFreshAcceptorThreadGroup(SJSessionParameters.DEFAULT_PARAMETERS);
	}*/
	
	//public static SJServerSocket getFreshServer(SJProtocol p) throws SJIOException
	public static SJAcceptorThreadGroup getFreshAcceptorThreadGroup(SJSessionParameters params) throws SJIOException // Used to receive sessions.
	{
		int attempts = 100;
		
		synchronized (portsInUse)
		{
			for (int i = 0; i < attempts; i++)
			{
				int port = takeFreePort(); // Makes a record for portsInUse.
				
				try
				{								
					return sjtm.openAcceptorGroup(port, params);
				}
				catch (SJSetupException se)
				{
					freePort(port);
				}							
			}			
		}
		
		throw new SJIOException("[SJRuntime] Could not get a fresh server socket.");
	}
	
	public static SJPort reserveFreeSJPort() throws SJIOException
	{
		return reserveFreeSJPort(SJSessionParameters.DEFAULT_PARAMETERS);
	}
	
	public static SJPort reserveFreeSJPort(SJSessionParameters params) throws SJIOException
    // Unlike takeFreeSJPort, this actually reserves the setups (guarantees port is open).
	{
		SJPort p = null;
		
		//SJAcceptorThreadGroup atg = getFreshAcceptorThreadGroup();
        // Doesn't work because getFreshAcceptorThreadGroup uses takeFreePort, which registers the port as in use,
        // and then when we get a conflict when we create the SJPort. 			

        SJAcceptorThreadGroup atg = null;
		
		int attempts = 100; // Following adapted from takeFreePort and getFreshAcceptorThreadGroup.
		
		Random rand = new Random();
		
		synchronized (reservedPorts) // Necessary? 
		{
			synchronized (portsInUse)
			{				
				for (int i = 0; i < attempts && atg == null; i++)
				{
					int port = rand.nextInt(UPPER_PORT_LIMIT - LOWER_PORT_LIMIT) + LOWER_PORT_LIMIT;
					
					try
					{								
						atg = sjtm.openAcceptorGroup(port, params); 
						
						p = new SJPort(port);
					}
					catch (SJSetupException se)
					{
						//freePort(port);
					}							
				}
				
				if (atg == null)
				{
					throw new SJIOException("[SJRuntime] Could not get a fresh server socket.");		
				}
			}					
			
			reservedPorts.put(p, atg); // To reserve the port.
		}
		
		return p; // A "ticket" to the reserved port. When this port is used, can "claim" the AcceptorThreadGroup for use.
	}
	
	/*public static void openFreshServerSocket(SJProtocol p) throws SJIOException
	{
		// This might be useful in some cases.
	}*/
	
	public static void openServerSocket(SJServerSocket ss) throws SJIOException
	{		
		SJPort sjPort = ss.getLocalSJPort();
		
		if (sjPort == null || !reservedPorts.containsKey(sjPort)) // Need synchronization?
		{
			int port = ss.getLocalPort();

			synchronized (portsInUse)
			{
				if (portsInUse.contains(port))
				{
					throw new SJIOException("[SJRuntime] Port already in use: " + port);
				}
				
				ss.setAcceptorGroup(sjtm.openAcceptorGroup(port, ss.getParameters()));		
				
				portsInUse.add(port);
			}
		}
		else
		{
			synchronized (reservedPorts) // Synchronization necessary? // It shouldn't be possible for a manually created SJPort to clash with a reserved one (synchronized on portsInUse). So only the reserver can claim the reserved port. 
			{
				ss.setAcceptorGroup(reservedPorts.remove(sjPort));
			}
		}
	}
	
	public static void closeServerSocket(SJServerSocket ss) 
	{
		int port = ss.getLocalPort();

        synchronized (portsInUse)
		{
			portsInUse.remove(port);
		}
		
		sjtm.closeAcceptorGroup(port);
	}
	
	public static void bindSocket(SJAbstractSocket s, SJConnection conn) throws SJIOException // Currently, conn can be null (delegation case 2 reconnection).
	{		
		s.init(conn);  
		
		s.setLocalHostName(localHostName); 
		s.setLocalPort(takeFreePort());
	}
	
	public static void connectSocket(SJRequestingSocket s) throws SJIOException
	{		
		SJServerIdentifier si = s.getService().getServerIdentifier();
				
		String targetHostAddress;
		
		try
		{
			targetHostAddress = InetAddress.getByName(si.getHostName()).getHostAddress();
		}
		catch (UnknownHostException uhe)
		{
			throw new SJIOException(uhe);
		}
		
		s.init(sjtm.openConnection(targetHostAddress, si.getPort(), s.getParameters()));

		//s.setHostName(si.getHostName());
		s.setHostName(targetHostAddress); // Host names are more fragile than IP addresses (e.g. HZHL2 on IC-DoC).  
		s.setPort(si.getPort());
		
		s.setLocalHostName(localHostName); // Actually, initialised to IP address (not host name), as we are doing for the target host "name".
		s.setLocalPort(takeFreePort());
	}

	public static void reconnectSocket(SJAbstractSocket s, String hostName, int port) throws SJIOException
	{		
		closeSocket(s);
		
		//s.reconnect(sjtm.openConnection(hostName, port));
		s.reconnect(sjtm.openConnection(hostName, port, s.getParameters()));
		
		try
		{
			//s.setHostName(hostName);
			s.setHostName(InetAddress.getByName(hostName).getHostAddress()); // Host names are more fragile than IP addresses (e.g. HZHL2 on IC-DoC).
			s.setPort(port);		
		}
		catch (UnknownHostException uhe)
		{
			throw new SJIOException(uhe);
		}
	}
	
	public static void closeSocket(SJAbstractSocket s) 
	{
		SJConnection conn = s.getConnection();
				
		if (conn != null) // FIXME: need a isClosed.
		{
			freePort(s.getLocalPort());
			
			sjtm.closeConnection(conn);
		}
	}
	
	protected static void accept(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException
	{
		try
		{
			s.setHostName((String) s.receive()); // Will be whatever requestor has set its localHostName to. // Maybe host name can be gotten from the underlying connection. Session port value needs to be sent though. 
			s.setPort(s.receiveInt());
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SJRuntimeException(cnfe);
		}				
		
		s.accept();
	}
	
	protected static void request(SJAbstractSocket s) throws SJIOException, SJIncompatibleSessionException
	{		
		s.send(s.getLocalHostName());		
		s.sendInt(s.getLocalPort());
		
		s.request();
	}

	public static void close(SJSocket s)
	{
		if (s != null) s.close();
	}	
	
	public static void close(SJSocket... sockets)
	{
		for (final SJSocket s : sockets)
		{
			// Need arbitrary interleaving of close() calls, as there
            // is a handshake with the other party in the close protocol.
            if (s != null) {
                Runnable closer = new Runnable() {
                    public void run() {
                        s.close();
                    }
                };
                new Thread(closer).start();
            }
		}
	}

	public static void send(Object obj, SJSocket s) throws SJIOException
	{
		s.send(obj);
	}

	public static void send(Object obj, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.send(obj);
		}
	}

	public static void send(int i, SJSocket s) throws SJIOException
	{
		s.sendInt(i);
	}

	public static void send(int i, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.sendInt(i);
		}
	}

	public static void send(boolean b, SJSocket s) throws SJIOException
	{
		s.sendBoolean(b);
	}

	public static void send(boolean b, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.sendBoolean(b);
		}
	}
	
	public static void send(double d, SJSocket s) throws SJIOException
	{
		s.sendDouble(d);
	}

	public static void send(double d, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.sendDouble(d);
		}
	}
	
	public static void pass(Object obj, SJSocket s) throws SJIOException
	{
		s.pass(obj);
	}

	public static void pass(Object obj, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.pass(obj);
		}
	}
	
	public static void copy(Object obj, SJSocket s) throws SJIOException
	{
		s.copy(obj);
	}

	public static void copy(Object obj, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.copy(obj);
		}
	}
	
	/*public static void copy(SJSocket s, int i) throws SJIOException	// Are these ever needed? 
	{
		s.copyInt(i);
	}
	
	public static void copy(SJSocket[] sockets, int i) throws SJIOException	
	{
		for (SJSocket s : sockets)
		{
			s.copyInt(i);
		}
	}
	
	public static void copy(SJSocket s, double d) throws SJIOException	// Primitives can also be declared na-final... 
	{
		s.copyDouble(d);
	}
	
	public static void copy(SJSocket[] sockets, double d) throws SJIOException	
	{
		for (SJSocket s : sockets)
		{
			s.copyDouble(d);
		}
	}*/

    private static boolean isValidResult(Object res, Class<?> expectedClass) {
        if (expectedClass == null) return !(res instanceof Throwable);
        else return res.getClass().isInstance(expectedClass);
    }

    private static Object timedReceive(int timeout, SJSocket s, int typeCode, String typeName, Class<?> expectedClass, Object[] args) throws SJIOException {
        Object[] res = new Object[1];

        new SJRuntimeReceiveTimeout(Thread.currentThread(), s, res, typeCode, args).start();

        try
		{
			Thread.sleep(timeout);
        }
        catch (InterruptedException ie)
        {
            // res is passed as a parameter to the SJRuntimeReceiveTimeout constructor,
            // hence is properly shared even though it's a local variable
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (res)
            {
                if (isValidResult(res[0], expectedClass))
                {
                    return res[0];
                }
                else if (res[0] instanceof SJIOException)
                {
                    throw (SJIOException) res[0];
                }
                else if (res[0] instanceof RuntimeException)
				{
					throw (RuntimeException) res[0];
				}
                else
                {
                    throw new RuntimeException("Unexpected type for received message: " + res[0]);
                }
            }
        }
        // We only want to close it if there was a timeout and we're aborting the session.
        ((SJAbstractSocket) s).getSerializer().close();
        // Is this a good idea? (Want to bypass FIN protocol).
        // FIXME: could try to send our FIN somehow, but maybe not possible (if it is though, should factor out a terminal early-close routine).

        throw new SJTimeoutException("[SJRuntime] receive" + typeName + " timed out: " + timeout);
    }

	public static Object receive(SJSocket s) throws SJIOException, ClassNotFoundException // Remove array in a compiler pass.
	{
		return s.receive();
	}
	
	//RAY
	// Copied from cvs version. Need dummy target with sockets array to satisfy initial base typing pass before SJUnicastOptimiser is run (assuming that pass is still needed to convert the singleton array).  
	public static Object receive(SJSocket[] sockets) throws SJIOException, ClassNotFoundException // Remove array in a compiler pass.
	{
		throw new SJRuntimeException("[SJRuntime] Shouldn't get into here: " + sockets);
	}
	//YAR
	
    // FIXME: only half done so far. Need to do for remaining ops.: accept, compound ops., etc.
    // Currently relies on implicit close to terminate the SJRuntimeReceiveTimeout thread that is still blocked on the receive operation -
    // need to check this works for all transports (check that an exception from early close is propagated up properly).
    // Also, need more work on making SJTimeout a terminal exception - don't do FIN protocol (on our side at least).
	public static Object receive(int timeout, SJSocket s) throws SJIOException
	{
		return timedReceive(timeout, s, SJRuntimeReceiveTimeout.OBJECT, "Object", Object.class, null);
	}
	
	public static Object receive(int timeout, SJSocket... sockets) throws SJIOException
	{
        // TODO multi-socket
		return receive(timeout, sockets[0]);
	}
	
	public static int receiveInt(SJSocket s) throws SJIOException
	{
		return s.receiveInt();
        // TODO multi-socket
	}
	
	public static int receiveInt(SJSocket... sockets) throws SJIOException
	{
		return sockets[0].receiveInt();
        // TODO multi-socket
	}
	
	public static int receiveInt(int timeout, SJSocket s) throws SJIOException
	{
        return (Integer) timedReceive(timeout, s, SJRuntimeReceiveTimeout.INT, "Int", Integer.class, null);
    }
	
	public static int receiveInt(int timeout, SJSocket... sockets) throws SJIOException
	{
		return receiveInt(timeout, sockets[0]);
        // TODO mulit-socket        
	}
	
	public static boolean receiveBoolean(SJSocket s) throws SJIOException
	{
		return s.receiveBoolean();
	}
	
	public static boolean receiveBoolean(SJSocket... sockets) throws SJIOException
	{
		return sockets[0].receiveBoolean();
        // TODO multi-socket
	}
	
	public static boolean receiveBoolean(int timeout, SJSocket s) throws SJIOException
	{
        return (Boolean) timedReceive(timeout, s, SJRuntimeReceiveTimeout.BOOLEAN, "Boolean", Boolean.class, null);
	}
	
	public static boolean receiveBoolean(int timeout, SJSocket... sockets) throws SJIOException
	{
		return receiveBoolean(timeout, sockets[0]);
        // TODO multi-socket        
	}

	public static double receiveDouble(SJSocket s) throws SJIOException
	{
		return s.receiveDouble();
	}

	public static double receiveDouble(SJSocket... sockets) throws SJIOException
	{
		return sockets[0].receiveDouble();
        // TODO multi-socket                
	}

	public static double receiveDouble(int timeout, SJSocket s) throws SJIOException
	{
        return (Double) timedReceive(timeout, s, SJRuntimeReceiveTimeout.DOUBLE, "Double", Double.class, null);
	}

	public static double receiveDouble(int timeout, SJSocket... sockets) throws SJIOException
	{
		return receiveDouble(timeout, sockets[0]);
        // TODO multi-socket
	}
	
	public static boolean recurse(String lab, SJSocket s) throws SJIOException
	{
        // Session-level recurse to a label is translated to a boolean value.
		return true;
	}
   
	public static boolean recurse(String lab, SJSocket... sockets) throws SJIOException
	{
		return true;
	}

	/*public static void spawn(SJSocket[] sockets, SJThread t) throws SJIOException
	{
		t.spawn(...);
	}*/

    public static boolean outsync(boolean cond, SJSocket s) throws SJIOException
	{
		s.outsync(cond);
		return cond;
	}

    public static boolean outsync(boolean cond, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.outsync(cond);
		}		
		return cond;
	}

    public static boolean insync(SJSocket s) throws SJIOException {
        return s.insync();
    }

	public static boolean insync(SJSocket... sockets) throws SJIOException
	{
		//Semantics: require all sockets to terminate at the same time, otherwise fail on all sockets.
        boolean[] hasMore = new boolean[sockets.length];
        Arrays.fill(hasMore, true);
        for (int i=0; i<sockets.length; ++i) {
            hasMore[i] = sockets[i].insync();
        }
        boolean oneFalse = false, allFalse = true;
        for (boolean b : hasMore) {
            if (!b) oneFalse = true;
            else allFalse = false;
        }
        if (oneFalse && !allFalse) throw new SJIOException("multi-party insync: some of the sockets signalled end of transmission but not all");
	    return !allFalse;
    }
	
	public static void outlabel(String lab, SJSocket s) throws SJIOException
    // FIXME: this should be automatically eligible for reference passing, need to check how it is
    // currently performed - labels cannot be user modified, and are immutable Strings anyway.
	{
		s.outlabel(lab);
	}

	public static void outlabel(String lab, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.outlabel(lab);
		}			
	}

	public static String inlabel(SJSocket s) throws SJIOException
	{	
		return s.inlabel();
	}

	public static String inlabel(SJSocket... sockets) throws SJIOException
	{
		return sockets[0].inlabel();
        // TODO multi-socket
	}

	public static boolean recursionEnter(SJSocket s)
	{
		return false;
	}

	public static boolean recursionEnter(SJSocket... sockets)
	{
		return false;
	}

	public static void recursionExit(SJSocket s)
	{
	}

	public static void recursionExit(SJSocket... sockets)
	{
	}
	
	/*public static void sendChannel(SJSocket[] sockets, SJService c) throws SJIOException // Channel objects should be immutable, so can be passed. // Remove array in a compiler pass
	{
		sockets[0].sendChannel(c);
	}*/

	public static void copy(Object obj, String encoded, SJSocket s) throws SJIOException
    // Shared-channel passing.
	{
		s.sendChannel((SJService) obj, encoded);
        // Could just extract the session type from the SJService.
	}

	public static void copy(Object obj, String encoded, SJSocket... sockets) throws SJIOException
	{
		for (SJSocket s : sockets)
		{
			s.sendChannel((SJService) obj, encoded);
		}
	}

	public static SJService receiveChannel(String encoded, SJSocket s) throws SJIOException, ClassNotFoundException
    // Needs a different name to session-receive - arguments are the same.
    // No ClassNotFoundException means runtime errors (from malicious peers) due to receiving unexpected
    // and unknown object classes must be converted to IO errors.
    // Actually, no ClassNotFoundException here doesn't seem to make much difference, the base type checker uses the "ordinary" receive.
    // Actually, no ClassNotFoundException here breaks the code generation javac pass...
	{
		return s.receiveChannel(encoded);
	}
	
	public static SJService receiveChannel(String encoded, SJSocket... sockets) throws SJIOException, ClassNotFoundException
    // Needs a different name to session-receive - arguments are the same.
	{
		return sockets[0].receiveChannel(encoded);
        // TODO multi-socket
	}
	
	public static SJService receiveChannel(String encoded, int timeout, SJSocket s) throws SJIOException, ClassNotFoundException
    // Session-receive.
	{
		return (SJService) timedReceive(timeout, s, SJRuntimeReceiveTimeout.CHANNEL, "Channel", null, new Object[] { encoded });
	}
	
	public static SJService receiveChannel(String encoded, int timeout, SJSocket... sockets) throws SJIOException, ClassNotFoundException
	{
		return receiveChannel(encoded, timeout, sockets[0]);
        // TODO multi-session
	}

	public static void pass(Object obj, String encoded, SJSocket s) throws SJIOException
    // Session delegation. Probably better to rename to something more obvious here as well.
	{
		s.delegateSession((SJAbstractSocket) obj, encoded);
	}

	public static void pass(Object obj, String encoded, SJSocket... sockets) throws SJIOException
	{
		sockets[0].delegateSession((SJAbstractSocket) obj, encoded);
        // TODO multi-session        
	}

	public static SJAbstractSocket receive(String encoded, SJSocket s) throws SJIOException, ClassNotFoundException
    // Session-receive.
	{
		return receive(encoded, SJSessionParameters.DEFAULT_PARAMETERS, s);
	}
	
	public static SJAbstractSocket receive(String encoded, SJSocket... sockets) throws SJIOException, ClassNotFoundException
	{
		return receive(encoded, SJSessionParameters.DEFAULT_PARAMETERS, sockets[0]);
        // TODO multi-session                
	}
	
	public static SJAbstractSocket receive(String encoded, SJSessionParameters params, SJSocket s) throws SJIOException, ClassNotFoundException // Session-receive.
	{
		return s.receiveSession(encoded, params);
	}

	public static SJAbstractSocket receive(String encoded, SJSessionParameters params, SJSocket... sockets) throws SJIOException, ClassNotFoundException
	{
		return sockets[0].receiveSession(encoded, params);
        // TODO multi-session                        
	}

	public static SJAbstractSocket receive(int timeout, SJSocket s, String encoded) throws SJIOException, ClassNotFoundException // Session-receive.
	{
		return receive(encoded, SJSessionParameters.DEFAULT_PARAMETERS, timeout, s);
	}

	public static SJAbstractSocket receive(String encoded, int timeout, SJSocket... sockets) throws SJIOException, ClassNotFoundException
	{
		return receive(encoded, SJSessionParameters.DEFAULT_PARAMETERS, timeout, sockets[0]);
        // TODO multi-session                                
	}

	public static SJAbstractSocket receive(String encoded, SJSessionParameters params, int timeout, SJSocket s) throws SJIOException, ClassNotFoundException // Session-receive.
	{
		return (SJAbstractSocket) timedReceive(timeout, s, SJRuntimeReceiveTimeout.SESSION, "Session", null, new Object[] {encoded, params});
	}
	
	public static SJAbstractSocket receive(String encoded, SJSessionParameters params, int timeout, SJSocket... sockets) throws SJIOException, ClassNotFoundException
	{
		return receive(encoded, params, timeout, sockets[0]);
        // TODO multi-session
	}
	
	public static Object receive(SJSessionParameters params, SJSocket... sockets) throws SJIOException // Dummy compiler targets for session-receive (pre-translation).
	{
		throw new SJRuntimeException("[SJRuntime] Shouldn't get in here.");
	}

	public static Object receive(SJSessionParameters params, SJSocket s) throws SJIOException
	{
		throw new SJRuntimeException("[SJRuntime] Shouldn't get in here.");
	}

	public static Object receive(SJSessionParameters params, int timeout, SJSocket... sockets) throws SJIOException // Dummy compiler targets for session-receive (pre-translation).
	{
		throw new SJRuntimeException("[SJRuntime] Shouldn't get in here.");
	}

	public static Object receive(SJSessionParameters params, int timeout, SJSocket s) throws SJIOException
	{
		throw new SJRuntimeException("[SJRuntime] Shouldn't get in here.");
	}

	public static SJTransportManager getTransportManager()
	{
		return sjtm;
	}	
	
	public static SJSerializer getSerializer(SJConnection conn) throws SJIOException
	{
		if (conn instanceof SJStreamConnection)
		{
			return new SJStreamSerializer(conn);
		}
		else //if (conn != null) // FIXME: Delegation case 2? 
		{
			return new SJManualSerializer(conn);
		}
		
		//return null; // Delegation case 2: no connection created between passive party and session acceptor. 
	}
	
	public static int findFreePort() throws SJIOException
	{
		Random rand = new Random();
		
		int retries = 100; // Factor out constant.
		
		synchronized (portsInUse)
		{		
			while (retries-- > 0)
			{
				int port = rand.nextInt(UPPER_PORT_LIMIT - LOWER_PORT_LIMIT) + LOWER_PORT_LIMIT;
				
				if (!portsInUse.contains(new Integer(port)))
				{
					return port;
				}
			}
		}
		
		throw new SJIOException("[SJRuntime] No free port found.");
	}	
	
	public static int takeFreePort() throws SJIOException
    // FIXME: what we need is a routine that finds the port and opens the server immediately
    // to reduce the chance that another process will steal e.g. the TCP port.
    // Should use something like getFreshAcceptorThreadGroup to ensure we have a fresh port,
    // return the port value, and cache the AcceptorThreadGroup somewhere until the port is actually used.
	{
		Random rand = new Random();
		
		int retries = 100;
		
		synchronized (portsInUse)
		{		
			while (retries-- > 0)
			{
				int port = rand.nextInt(UPPER_PORT_LIMIT - LOWER_PORT_LIMIT) + LOWER_PORT_LIMIT;

                if (!portsInUse.contains(port))
				{
					portsInUse.add(port);
					
					return port;
				}
			}
		}
		
		throw new SJIOException("[SJRuntime] No free port found.");
	}	
	
	public static SJPort takeFreeSJPort() throws SJIOException
    // Rename to getFreeSJPort, to better distinguish from reserveFreeSJPort.
	{
		Random rand = new Random();
		
		int retries = 100;
		
		synchronized (portsInUse)
		{		
			while (retries-- > 0)
			{
				int port = rand.nextInt(UPPER_PORT_LIMIT - LOWER_PORT_LIMIT) + LOWER_PORT_LIMIT;

                if (!portsInUse.contains(port))
				{
					return new SJPort(port); // Uses take port to record for portsInUse.
				}
			}
		}
		
		throw new SJIOException("[SJRuntime] No free port found.");
	}
	
	public static void takePort(int port) throws SJIOException
    // FIXME: a bit confusing with takeFreePort and takeFreshSessionPort.
    // Other routines in this class should be modified to use this operation.
	{
		synchronized (portsInUse)
		{
			if (portsInUse.contains(new Integer(port)))
			{
				throw new SJIOException("[SJRuntime] Port already taken: " + port);
			}
			
			portsInUse.add(port);
		}
	}
	
	public static void freePort(int port)
	{
		synchronized (portsInUse)
		{
			portsInUse.remove(new Integer(port));
		}
	}
	
	public static SJSessionType decodeSessionType(String encoded) throws SJIOException
	{
		synchronized (sjte)
		{
			return sjte.decode(encoded);
		}
	}
	
	// Hacks for bounded-buffer communication.
	
	/*public static void initBoundedBuffer(SJSocket s, int size) throws SJIOException
	{
		SJConnection conn = ((SJAbstractSocket) s).getConnection();
		
		if (!(conn instanceof SJBoundedBufferConnection))
		{
			throw new SJIOException("[SJRuntime] SJBoundedBufferConnection not found: " + conn.getTransportName());
		}
		
		((SJBoundedBufferConnection) conn).initBoundedBuffer(size);
	}*/

	/*public static void sendBB(SJSocket s, Object obj) throws SJIOException
	{
		s.sendBB(obj);
	}
	
	public static void passBB(SJSocket s, Object obj) throws SJIOException
	{
		s.passBB(obj);
	}
	
	public static Object receiveBB(SJSocket s) throws SJIOException, ClassNotFoundException 
	{
		return s.receiveBB();
	}
	
	public static void outlabelBB(SJSocket s, String lab) throws SJIOException // FIXME: this should be automatically eligible for reference passing, need to check how it is currently performed - labels cannot be user modified, and are immutable Strings anyway.
	{
		s.outlabelBB(lab);			
	}
	
	public static String inlabelBB(SJSocket s) throws SJIOException
	{	
		return s.inlabelBB();
	}*/
	
	/*public static boolean recurseBB(SJSocket s, String lab) throws SJIOException // Session-level recurse to a label is translated to a boolean value.
	{
		// return true;
		
		return s.recurseBB(lab);	// If we adopt this in the future, the standard recurse routine should be modified accordingly.	
	}*/	
}

class SJRuntimeReceiveTimeout extends Thread
// This is a bit of a hack - maybe better to have timeout operations directly supported by each transport,
// by adding them to the ATI. This scheme currently relies on the implicit socket close to terminate this thread (if it's still blocking) after a timeout.
{
	public static final int OBJECT = 11;
	public static final int INT = 12;
	public static final int BOOLEAN = 13;
	public static final int DOUBLE = 14;
	
	public static final int CHANNEL = 21;
	public static final int SESSION = 22;	

	private Thread t;
	private SJSocket s;
	private final Object[] res;
	private int op;
	private Object[] args;
	
	public SJRuntimeReceiveTimeout(Thread t, SJSocket s, Object[] res, int op, Object[] args)
	{
		this.t = t;
		this.s = s;
		this.res = res;
		this.op = op;
		this.args = args;
	}
	
	public void run()
	{
		Object o;
		
		try
		{						
			switch (op)
			{
				case OBJECT:
				{
					o = s.receive(); break;
				}
				case INT:
				{
					o = s.receiveInt(); break;
				}
				case BOOLEAN:
				{
					o = s.receiveBoolean(); break;
				}
				case DOUBLE:
				{
					o = s.receiveDouble(); break;
				}
				case CHANNEL:
				{
					o = s.receiveChannel((String) args[0]); break;
				}
				case SESSION:
				{
					o = s.receiveSession((String) args[0], (SJSessionParameters) args[1]); break;
				}
				default:
				{
					o = new SJRuntimeException("[SJRuntimeReceiveTimeout] Shouldn't get in here: " + op);
				}
			}
		}
		catch (Exception x)
		{
			o = x;
		}
		
		synchronized (res)
		{
			res[0] = o;
		}
		
		t.interrupt();
	}
}
