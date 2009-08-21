/**
 * 
 */
package sessionj.runtime.transport;

import java.io.*;
import java.net.Socket;

import sessionj.runtime.SJIOException;

import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

/**
 * @author Raymond
 *
 */
abstract public class SJStreamConnection implements SJConnection
{
	/*public static enum Role { ACCEPTOR, REQUESTOR }
	
	private Role role;*/
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	protected SJStreamConnection(OutputStream out, InputStream in) throws SJIOException
	{
		//this.role = Role.REQUESTOR;
		
		this.dos = new DataOutputStream(out);
		this.dis = new DataInputStream(in);
	}

	protected SJStreamConnection(InputStream in, OutputStream out) throws SJIOException // HACK: to avoid deadlock when setting up I/O streams.
	{
		//this.role = Role.ACCEPTOR;
		
		this.dis = new DataInputStream(in);		
		this.dos = new DataOutputStream(out);
	}
	
	public OutputStream getOutputStream()
	{
		return dos;
	}
	
	public InputStream getInputStream()
	{
		return dis;
	}
	
	public void disconnect() //throws SJIOException 
	{
		try { closeStream(dos); } catch (IOException ioe) { }
		try { closeStream(dis); } catch (IOException ioe) { }				
	}

	public void writeByte(byte b) throws SJIOException
	{
		try
		{
			dos.writeByte(b);
			//dos.flush(); // Needed? Should instead expose the flush method?
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public void writeBytes(byte[] bs) throws SJIOException
	{
		try
		{
			dos.write(bs, 0, bs.length);
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}

	public byte readByte() throws SJIOException
	{
		try
		{
			return dis.readByte();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}		
	}
	
	public void readBytes(byte[] bs) throws SJIOException
	{
		try
		{
			dis.readFully(bs); // Here, ATI different to standard TCP API. 
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}	
	}	
	
	public void flush() throws SJIOException
	{
		try
		{		
			dos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}				
	}
	
	/*public Role getRole()
	{
		return role;
	}*/
}
