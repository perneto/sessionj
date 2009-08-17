package sessionj.runtime.transport.http;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.net.ssl.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.SJConnection;
import sessionj.runtime.transport.SJConnectionAcceptor;

import static sessionj.runtime.util.SJRuntimeUtils.*;

public class SJHTTPSAcceptor implements SJConnectionAcceptor{
	
	private SSLServerSocketFactory ssf;
	
	private SSLServerSocket sss;
	
	public SJHTTPSAcceptor(int port) throws SJIOException
	{
		String DEFAULT_KEYSTORE = "C:\\cygwin\\home\\Raymond\\code\\java\\eclipse\\sessionj-cvs\\serverKeystore";
		
		try {
	    KeyManagerFactory mgrFact = KeyManagerFactory.getInstance("SunX509");
	    KeyStore serverStore = KeyStore.getInstance("JKS");
	
	    // Loads keystore
	    serverStore.load(new FileInputStream(DEFAULT_KEYSTORE), "password".toCharArray());
	    mgrFact.init(serverStore, "password".toCharArray());
	
	    // create a context and initialises it
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(mgrFact.getKeyManagers(), null, null);
		
		ssf = (SSLServerSocketFactory)sslContext.getServerSocketFactory();
	} catch (NoSuchAlgorithmException e) {
		throw new SJIOException(e);
	} catch (Exception e) {
		throw new SJIOException(e);
	}
		
		try
		{
			final SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			sss = (SSLServerSocket)sslSocketFactory.createServerSocket(port); // Didn't bother to explicitly check portInUse.
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public SJConnection accept() throws SJIOException{
		
		try
		{
			if (sss == null)
			{
				throw new SJIOException("[" + getTransportName() + "] Connection acceptor not open.");
			}
			
			SSLSocket s = (SSLSocket)sss.accept(); 
				
			return new SJHTTPSConnection(s, s.getInputStream(), s.getOutputStream());
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}

	}
	
	public void close(){
		
		try 
		{ 
			if (sss != null)
			{
				sss.close(); 
			}
		}
		catch (IOException ioe) { }

	}
	
	public boolean interruptToClose(){
		
		return false;
	}
	
	public boolean isClosed(){
			
		return sss.isClosed();
	}
	
	public String getTransportName(){
		
		return SJHTTPS.TRANSPORT_NAME;
	}

}
