/**
 * 
 */
package sessionj.runtime.session;

import java.io.*;
import java.util.Arrays;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.SJRuntimeUtils;

import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

/**
 * @author Raymond
 *
 * FIXME: use SJRuntime as a factory to create an appropriate serializer (based on connection and user parameters). Also for session protocols object.
 *
 */
public class SJStreamSerializer extends SJSerializer
{		
	private ObjectOutputStream oos = null; // These and the stream operations should be moved into the stream connection object.
	private ObjectInputStream ois = null;
	
	public SJStreamSerializer(SJConnection conn) throws SJIOException
	{
		super(conn);
		
		if (conn instanceof SJStreamConnection)
		{
			SJStreamConnection sc = (SJStreamConnection) conn;

			try
			{
				//if (sc.getRole() == Role.ACCEPTOR)
				{
					oos = new ObjectOutputStream(sc.getOutputStream()); // FIXME: May need to adjust buffer size or wrap using a buffer stream for good performance?
					ois = new ObjectInputStream(sc.getInputStream());
				}
				/*else
				{					
					ois = new ObjectInputStream(sc.getInputStream());
					oos = new ObjectOutputStream(sc.getOutputStream());
				}*/
			}
			catch (IOException ioe)
			{
				throw new SJIOException(ioe);
			}
		}
		else
		{
			throw new SJIOException("[SJStreamSerializer] Expected SJStreamConnection, not: " + conn);
		}
	}
	
	protected boolean zeroCopySupported()
	{
		return conn instanceof SJLocalConnection; // False?
	}
	
	/*protected boolean hasBoundedBuffer()
	{
		return conn instanceof SJBoundedBufferConnection; // False?
	}*/
	
	public void close()
	{
		if (!isClosed) // Need synchro.?
		{
			isClosed = true;
			
			try { closeStream(oos); } catch (IOException ioe) { }
			try { closeStream(ois); } catch (IOException ioe) { }
		}
	}
	
	public void writeObject(Object o) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_OBJECT);
			oos.writeObject(o);
			
			oos.reset(); // Does this affect performance? Would it be faster to make a new oos? (Probably not.) Should we manually keep track of which objects have been sent and reset only when necessary?
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}	
	
	public void writeByte(byte b) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_BYTE);
			oos.writeByte(b);
			oos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public void writeInt(int i) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_INT);
			oos.writeInt(i);
			oos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public void writeBoolean(boolean b) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_BOOLEAN);
			oos.writeBoolean(b);
			oos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public void writeDouble(double d) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_DOUBLE);
			oos.writeDouble(d);
			oos.flush();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}
	
	public Object readObject() throws SJIOException, ClassNotFoundException, SJControlSignal
	{
		Object o;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_OBJECT)
			{				
				o = ois.readObject(); // Should we do reset at some point? // No, the API says the in/out streams are synchronised. // In fact the ois reset is actually something different.  
			}
			else if (flag == SJ_REFERENCE)
			{
				o = ((SJLocalConnection) conn).readReference();
			}
			else if (flag == SJ_CONTROL)
			{
				throw (SJControlSignal) ois.readObject();
			}
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
		
		return o;
	}
	
	public byte readByte() throws SJIOException, SJControlSignal
	{
		byte b;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_BYTE)
			{					
				b = ois.readByte();
			}
			else if (flag == SJ_CONTROL)
			{
				try
				{
					throw ((SJControlSignal) ois.readObject());
				}
				catch (ClassNotFoundException cnfe)
				{
					throw new SJRuntimeException(cnfe);
				}
			}				
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
			
		return b;
	}
	
	public int readInt() throws SJIOException, SJControlSignal
	{
		int i;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_INT)
			{					
				i = ois.readInt();
			}
			else if (flag == SJ_CONTROL)
			{
				try
				{
					throw ((SJControlSignal) ois.readObject());
				}
				catch (ClassNotFoundException cnfe)
				{
					throw new SJRuntimeException(cnfe);
				}
			}				
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
		
		return i;
	}
	
	public boolean readBoolean() throws SJIOException, SJControlSignal
	{
		boolean b;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_BOOLEAN)
			{					
				b = ois.readBoolean();
			}
			else if (flag == SJ_CONTROL)
			{
				try
				{
					throw ((SJControlSignal) ois.readObject());
				}
				catch (ClassNotFoundException cnfe)
				{
					throw new SJRuntimeException(cnfe);
				}
			}				
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
			
		return b;
	}
	
	public double readDouble() throws SJIOException, SJControlSignal
	{
		double d;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_DOUBLE)
			{					
				d = ois.readDouble();
			}
			else if (flag == SJ_CONTROL)
			{
				try
				{
					throw ((SJControlSignal) ois.readObject());
				}
				catch (ClassNotFoundException cnfe)
				{
					throw new SJRuntimeException(cnfe);
				}
			}				
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
		
		return d;
	}
	
	public void writeReference(Object o) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_REFERENCE);
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);				
		}			
		
		((SJLocalConnection) conn).writeReference(o); // Ignoring possibly open oos, writing straight to conn (is this OK?).
	}
	
	public Object readReference() throws SJIOException, SJControlSignal
	{			
		byte flag;

		try
		{
			flag = ois.readByte();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);				
		}
					
		if (flag == SJ_REFERENCE)
		{
			return ((SJLocalConnection) conn).readReference();
		}
		else if (flag == SJ_CONTROL)
		{						
			throw (SJControlSignal) readObjectFromOOSNoCNFE();
		}			
		else
		{
			throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
		}					
	}

	private Object readObjectFromOOSNoCNFE() throws SJIOException
	{
		try
		{
			return ois.readObject();
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SJRuntimeException(cnfe);
		}
	}
	
	public void writeControlSignal(SJControlSignal cs) throws SJIOException
	{
		try
		{
			oos.writeByte(SJ_CONTROL);
			oos.writeObject(cs);
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
	}

	public SJControlSignal readControlSignal() throws SJIOException
	{
		SJControlSignal cs = null;
		
		try
		{
			byte flag = ois.readByte();
			
			if (flag == SJ_CONTROL)
			{				
				cs = (SJControlSignal) ois.readObject();
			}
			else
			{
				//throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
				throw new SJIOException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}			
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SJIOException(cnfe);
		}
			
		return cs;
	}
	
	protected SJMessage nextMessage() throws SJIOException, ClassNotFoundException//, SJControlSignal
	{
		byte type;
		Object o;
		
		try
		{
			byte flag = ois.readByte();
			
			switch (flag)
			{
				case SJ_CONTROL:
				{
					//throw (SJControlSignal) ois.readObject();
					
					type = SJMessage.SJ_CONTROL;
					o = ois.readObject(); 
					
					break;
				}
				case SJ_OBJECT: 
				{
					type = SJMessage.SJ_OBJECT;
					o = ois.readObject(); 
					
					break;
				}
				case SJ_REFERENCE: 
				{
					type = SJMessage.SJ_REFERENCE;
					o = ((SJLocalConnection) conn).readReference();
					
					break;
				}
				case SJ_BYTE: 
				{
					type = SJMessage.SJ_BYTE;
					o = new Byte(ois.readByte()); 
					
					break;
				}
				case SJ_INT: 
				{
					type = SJMessage.SJ_INT;
					o = new Integer(ois.readInt()); 
					
					break;
				}
				case SJ_BOOLEAN: 
				{
					type = SJMessage.SJ_BOOLEAN;
					o = new Boolean(ois.readBoolean()); 
					
					break;
				}
				default: 
				{
					throw new SJRuntimeException("[SJDefaultSerializer] Unsupported flag: " + flag);
				}
			}
		}
		catch (IOException ioe)
		{
			throw new SJIOException(ioe);
		}
			
		return new SJMessage(type, o);
	}
}
