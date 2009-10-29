/**
 * 
 */
package sessionj.runtime.transport;

import java.io.IOException;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.SJRuntime;

import static sessionj.runtime.util.SJRuntimeUtils.*;

/**
 * @author Raymond
 *
 */
public class SJSetupThread extends Thread // Should have a common base class with SJAcceptorThread.
{	
	private SJAcceptorThreadGroup atg;
	private SJConnectionAcceptor ca;
	
	private boolean run = true;
	
	public SJSetupThread(SJAcceptorThreadGroup atg, SJConnectionAcceptor ca)
	{
		super(atg, ca.getTransportName() + ":" + atg.getPort());
	
		this.atg = atg;
		this.ca = ca;
	}
	
	public void run()
	{		
		try
		{
			while (run)
			{
				SJConnection conn = null;
				
				boolean reuse = false;
				
				try
				{
					reuse = false;
					
					conn = ca.accept();
					
					reuse = SJRuntime.getTransportManager().serverNegotiation(atg, conn);
					
					if (reuse)
					{
						atg.queueConnection(conn);
					}					
				}			
				catch (SJIOException ioe)
				{
					
				}
				finally
				{
					if (!reuse)
					{
						if (conn != null)
						{
							try
							{
								conn.flush();
							}
							catch (SJIOException ioe)
							{
								
							}
							finally
							{
								conn.disconnect();
							}
						}						
					}
				}
			}
		}
		finally
		{
			if (!ca.isClosed())
			{
				ca.close(); 
			}
		}			
	}
	
	public void close()
	{		
		run = false;
		
		ca.close();
	
		if (ca.interruptToClose())
		{
			this.interrupt();
			//throw new RuntimeException("..."); // Maybe this would be better, with an appropriate exception catcher?
		}
	}
	
	public String toString()
	{
		return "SJSetup" + super.toString();
	}	
}
