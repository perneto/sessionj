/**
 * 
 */
package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;
import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

import java.io.*;

/**
 * @author Raymond
 *
 */
abstract public class SJStreamConnection implements SJConnection
{
	private final DataOutputStream dos;
	private final DataInputStream dis;
	
	protected SJStreamConnection(InputStream in, OutputStream out)
    // HACK: to avoid deadlock when setting up I/O streams.
	// ie. order of parameters, in before out
    {
        dis = new DataInputStream(in);
        dos = new DataOutputStream(out);
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
		try { closeStream(dos); } catch (IOException ignored) {}
		try { closeStream(dis); } catch (IOException ignored) {}
	}

	public void writeByte(byte b) throws SJIOException
	{
		try
		{
			dos.writeByte(b);
			//dos.flush(); // Let upper layers sort out flushing.
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
}
