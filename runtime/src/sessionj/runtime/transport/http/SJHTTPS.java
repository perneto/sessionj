package sessionj.runtime.transport.http;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.ssl.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;
import sessionj.runtime.transport.SJTransport;

import static sessionj.runtime.util.SJRuntimeUtils.*;

public class SJHTTPS implements SJTransport{

	public static final String TRANSPORT_NAME = "sessionj.runtime.transport.https.SJHTTPS";

	private static final int LOWER_PORT_LIMIT = 1024; 
	private static final int PORT_RANGE = 10000;
	
	public SJConnectionAcceptor openAcceptor(int port) throws SJIOException{
		
		return new SJHTTPSAcceptor(port);
	}
	
	/*public SJConnection connect(SJServerIdentifier si) throws SJIOException{
		return connect(si.getHostName(), si.getPort());
	}*/
	
	public SJConnection connect(String hostName, int port) throws SJIOException
	{		
		String DEFAULT_KEYSTORE = "C:\\cygwin\\home\\Raymond\\code\\java\\eclipse\\sessionj-cvs\\serverKeystore";
		
		try 
		{
			SSLSocketFactory ssf;
			
			try {
		    KeyManagerFactory mgrFact = KeyManagerFactory.getInstance("SunX509");
		    KeyStore serverStore = KeyStore.getInstance("JKS");
		
		    // Loads keystore
		    serverStore.load(new FileInputStream(DEFAULT_KEYSTORE), "password".toCharArray());
		    mgrFact.init(serverStore, "password".toCharArray());
		
		    // create a context and initialises it
		    SSLContext sslContext = SSLContext.getInstance("TLS");
		    sslContext.init(mgrFact.getKeyManagers(), null, null);
			
		    
		    
			ssf = (SSLSocketFactory)sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			throw new SJIOException(e);
		} catch (Exception e) {
			throw new SJIOException(e);
		}			
			
			//final SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();			
			//SSLSocket conn = (SSLSocket)sslSocketFactory.createSocket(hostName, port);
		
			SSLSocket conn = (SSLSocket)ssf.createSocket(hostName, port);
			
			return new SJHTTPConnection(conn, conn.getOutputStream(), conn.getInputStream());
		} 
		catch (IOException ioe) 
		{
			throw new SJIOException(ioe);
		}

	}

    public SJSelector transportSelector() {
        return null;
    }

    public boolean portInUse(int port){
		
		//SSLServerSocket sss = null; 
		ServerSocket ss = null;
		
		try
		{
			/*final SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			sss = (SSLServerSocket)sslSocketFactory.createServerSocket(port);*/
			
			ss = new ServerSocket(port);

		}
		catch (IOException ioe)
		{
			return true;
		}
		finally
		{
			//if (sss != null) 
			if (ss != null)
			{
				try
				{
					//sss.close();
					ss.close();
				}
				catch (IOException ioe) { }					
			}
		}
		
		return false;

	}
	
	public int getFreePort() throws SJIOException{
		
		int start = new Random().nextInt(PORT_RANGE);
		int seed = start + 1;
		
		for (int port = seed % PORT_RANGE; port != start; port = seed++ % PORT_RANGE)  
		{
			if (!portInUse(port + LOWER_PORT_LIMIT))
			{
				return port + LOWER_PORT_LIMIT;
			}
		}
		
		throw new SJIOException("[" + getTransportName() + "] No free port available.");
	}
	
	public String getTransportName(){
		
		return TRANSPORT_NAME;
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
