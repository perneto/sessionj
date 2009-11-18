/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;

/**
 * @author Raymond
 *
 */
public class SJAcceptorThread extends Thread
{
	private SJAcceptorThreadGroup atg; //=(SJAcceptorThreadGroup) getThreadGroup()
	private SJConnectionAcceptor ca;
	
	private boolean isAccepting = false;
	
	private boolean run = true;
	
	public SJAcceptorThread(SJAcceptorThreadGroup atg, SJConnectionAcceptor ca)
	{
		super(atg, ca.getTransportName() + ":" + atg.getPort());
		
		this.atg = atg;
		this.ca = ca;				
	}
	
	public void run()
	{
		this.isAccepting = true;
		
		try
		{
			while (run)
			{
				try
				{
					atg.queueConnection(ca.accept());
				}
				//catch (InterruptedException ie) { } // Interrupt doesn't apply...
				catch (SJIOException ioe) { } // ...instead close the connection acceptor and catch the exception?			
			}
		}
		finally
		{
			this.isAccepting = false;
			
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
		}
	}
	
	public int getPort()
	{
		return atg.getPort();
	}
	
	public boolean isAccepting()
	{
		return isAccepting;
	}
	
	public String toString()
	{
		return "SJAcceptor" + super.toString();
	}
}
