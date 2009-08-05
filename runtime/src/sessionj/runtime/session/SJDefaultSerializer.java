/**
 * 
 */
package sessionj.runtime.session;

import java.io.*;
import java.util.Arrays;

import org.omg.CORBA.OBJ_ADAPTER;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.SJRuntimeUtils;

import static sessionj.runtime.util.SJRuntimeUtils.closeStream;

/**
 * @author Raymond
 * @deprecated
 *
 * FIXME: use SJRuntime as a factory to create an appropriate serializer (based on connection and user parameters). Also for session protocols object.
 *
 */
public class SJDefaultSerializer extends SJSerializer
{	
	private static final byte FALSE = 0;
	private static final byte TRUE = 1;
	
	private static final int SERIALIZED_INT_LENGTH = 4;
	
	private ObjectOutputStream oos = null; // These and the stream operations should be moved into the stream connection object.
	private ObjectInputStream ois = null;
	
	public SJDefaultSerializer(SJConnection conn) throws SJIOException
	{
		super(conn);
		
		if (conn instanceof SJStreamConnection)
		{
			SJStreamConnection sc = (SJStreamConnection) conn;

			try
			{
				//if (sc.getRole() == Role.ACCEPTOR)
				{
					oos = new ObjectOutputStream(sc.getOutputStream());
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
	}
	
	protected boolean zeroCopySupported()
	{
		return conn instanceof SJLocalConnection;
	}
	
	/*protected boolean hasBoundedBuffer()
	{
		return conn instanceof SJBoundedBufferConnection;
	}*/
	
	public void close()
	{
		try { closeStream(oos); } catch (IOException ioe) { }
		try { closeStream(ois); } catch (IOException ioe) { }
	}
	
	public void writeObject(Object o) throws SJIOException
	{
		if (oos != null)
		{
			try
			{
				oos.writeByte(SJ_OBJECT);
				oos.writeObject(o);
			}
			catch (IOException ioe)
			{
				throw new SJIOException(ioe);
			}
		}
		else
		{
			conn.writeByte(SJ_OBJECT);
			
			writeObjectToConn(o);
		}
	}	
	
	public void writeByte(byte b) throws SJIOException
	{
		if (oos != null)
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
		else
		{
			conn.writeByte(SJ_BYTE);			
			conn.writeByte(b);
			conn.flush();
		}
	}
	
	public void writeInt(int i) throws SJIOException
	{
		if (oos != null)
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
		else
		{
			conn.writeByte(SJ_INT);
			
			byte[] bs = serializeInt(i);		
			
			conn.writeBytes(bs);
			conn.flush();
		}
	}
	
	public void writeBoolean(boolean b) throws SJIOException
	{
		if (oos != null)
		{
			try
			{
				oos.writeByte(SJ_BOOLEAN);
				oos.writeByte(serializeBoolean(b));
				oos.flush();
			}
			catch (IOException ioe)
			{
				throw new SJIOException(ioe);
			}
		}
		else
		{
			conn.writeByte(SJ_BOOLEAN);			
			conn.writeByte(serializeBoolean(b));
			conn.flush();
		}
	}
	
	public void writeDouble(double d) throws SJIOException
	{
		if (oos != null)
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
		else
		{
			conn.writeByte(SJ_DOUBLE);
			
			//byte[] bs = serializeDouble(d); // FIXME.						
			
			//conn.writeBytes(bs);
			//conn.flush();
			
			throw new SJRuntimeException("[SJDefaultSerializer] writeDouble not done yet: " + d);
		}
	}
	
	public Object readObject() throws SJIOException, ClassNotFoundException, SJControlSignal
	{
		Object o;
		
		if (ois != null)
		{
			try
			{
				byte flag = ois.readByte();
				
				if (flag == SJ_OBJECT)
				{				
					o = ois.readObject();
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
		}
		else
		{						
			byte flag = conn.readByte();
			
			if (flag == SJ_OBJECT || flag == SJ_CONTROL)
			{
				o = readObjectFromConn();
				
				if (flag == SJ_CONTROL)
				{
					throw (SJControlSignal) o;
				}
			}
			else if (flag == SJ_REFERENCE)
			{
				o = ((SJLocalConnection) conn).readReference();
			}			
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return o;
	}
	
	public byte readByte() throws SJIOException, SJControlSignal
	{
		byte b;
		
		if (ois != null)
		{
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
		}
		else
		{
			byte flag = conn.readByte();	
			
			if (flag == SJ_BYTE)
			{			
				b = conn.readByte();
			}
			else if (flag == SJ_CONTROL)
			{								
				throw (SJControlSignal) readObjectFromConnNoCNFE();
			}			
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return b;
	}
	
	public int readInt() throws SJIOException, SJControlSignal
	{
		int i;
		
		if (ois != null)
		{
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
		}
		else
		{
			byte flag = conn.readByte();	
			
			if (flag == SJ_INT)
			{			
				i = readIntFromConn();
			}
			else if (flag == SJ_CONTROL)
			{								
				throw (SJControlSignal) readObjectFromConnNoCNFE();
			}					
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return i;
	}
	
	public boolean readBoolean() throws SJIOException, SJControlSignal
	{
		boolean b;
		
		if (ois != null)
		{
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
		}
		else
		{			
			byte flag = conn.readByte();			
			
			if (flag == SJ_BOOLEAN)
			{										
				b = deserializeBoolean(conn.readByte());
			}
			else if (flag == SJ_CONTROL)
			{								
				throw (SJControlSignal) readObjectFromConnNoCNFE();
			}				
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return b;
	}
	
	public double readDouble() throws SJIOException, SJControlSignal
	{
		double d;
		
		if (ois != null)
		{
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
		}
		else
		{
			byte flag = conn.readByte();	
			
			if (flag == SJ_DOUBLE)
			{			
				//d = ...; // FIXME.
				
				throw new SJRuntimeException("[SJDefaultSerializer] readDouble not done yet.");
			}
			else if (flag == SJ_CONTROL)
			{								
				throw (SJControlSignal) readObjectFromConnNoCNFE();
			}					
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return d;
	}
	
	public void writeReference(Object o) throws SJIOException
	{
		/*if (!(conn instanceof SJLocalConnection))
		{
			throw new SJIOException("[SJDefaultSerializer] Connection does not support reference write: " + o);			
		}*/
		
		if (oos != null)
		{
			try
			{
				oos.writeByte(SJ_REFERENCE);
			}
			catch (IOException ioe)
			{
				throw new SJIOException(ioe);				
			}			
		}
		else
		{
			conn.writeByte(SJ_REFERENCE);
		}
		
		((SJLocalConnection) conn).writeReference(o); // Ignoring possibly open oos, writing straight to conn (is this OK?).
	}
	
	public Object readReference() throws SJIOException, SJControlSignal
	{
		/*if (!(conn instanceof SJLocalConnection))
		{
			throw new SJIOException("[SJDefaultSerializer] Connection does not support reference read.");
		}*/	
		
		byte flag;
		
		if (ois != null)
		{
			try
			{
				flag = ois.readByte();
			}
			catch (IOException ioe)
			{
				throw new SJIOException(ioe);				
			}
		}
		else
		{
			flag = conn.readByte();
		}
					
		if (flag == SJ_REFERENCE)
		{
			return ((SJLocalConnection) conn).readReference();
		}
		else if (flag == SJ_CONTROL)
		{						
			if (ois != null)
			{
				throw (SJControlSignal) readObjectFromOOSNoCNFE();
			}
			else
			{
				throw (SJControlSignal) readObjectFromConnNoCNFE();
			}
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
		if (oos != null)
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
		else
		{
			byte[] bs = serializeObject(cs);
			
			conn.writeByte(SJ_CONTROL);
			conn.writeBytes(serializeInt(bs.length));
			conn.writeBytes(bs);
		}
	}

	public SJControlSignal readControlSignal() throws SJIOException
	{
		SJControlSignal cs = null;
		
		if (ois != null)
		{
			try
			{
				byte flag = ois.readByte();
				
				if (flag == SJ_CONTROL)
				{				
					cs = (SJControlSignal) ois.readObject();
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
			catch (ClassNotFoundException cnfe)
			{
				throw new SJIOException(cnfe);
			}
		}
		else
		{						
			byte flag = conn.readByte();
			
			if (flag == SJ_CONTROL)
			{
				cs = (SJControlSignal) readObjectFromConnNoCNFE();
			}
			else
			{
				throw new SJRuntimeException("[SJDefaultSerializer] Unexpected flag: " + flag);
			}
		}
		
		return cs;
	}
	
	protected SJMessage nextMessage() throws SJIOException, ClassNotFoundException//, SJControlSignal
	{
		byte flag;
		byte type;
		Object o;
		
		if (ois != null)
		{
			try
			{
				flag = ois.readByte();
				
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
		}
		else
		{
			flag = conn.readByte();
			
			switch (flag)
			{
				case SJ_CONTROL:
				{
					//throw (SJControlSignal) readObjectFromConn();
					
					type = SJMessage.SJ_CONTROL;
					o = readObjectFromConn(); 
					
					break;
				}			
				case SJ_OBJECT: 
				{
					type = SJMessage.SJ_OBJECT;
					o = readObjectFromConn(); 
					
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
					o = new Byte(conn.readByte()); 
					
					break;
				}
				case SJ_INT: 
				{
					type = SJMessage.SJ_INT;
					o = new Integer(readIntFromConn()); 
					
					break;
				}
				case SJ_BOOLEAN: 
				{
					type = SJMessage.SJ_BOOLEAN;
					o = new Boolean(deserializeBoolean(conn.readByte())); 
					
					break;
				}
				default: 
				{
					throw new SJRuntimeException("[SJDefaultSerializer] Unsupported flag: " + flag);
				}
			}			
		}			
		
		return new SJMessage(type, o);
	}
	
	private void writeObjectToConn(Object o) throws SJIOException
	{
		byte[] bs = serializeObject(o);
		
		conn.writeBytes(serializeInt(bs.length));
		conn.writeBytes(bs);
	}
	
	private Object readObjectFromConn() throws SJIOException, ClassNotFoundException
	{		
		byte[] bs = new byte[readIntFromConn()];
		
		conn.readBytes(bs);
		
		return deserializeObject(bs);		
	}
	
	private int readIntFromConn() throws SJIOException
	{
		byte[] bs = new byte[SERIALIZED_INT_LENGTH];
		
		conn.readBytes(bs);			
		
		return deserializeInt(bs);
	}
	
	private Object readObjectFromConnNoCNFE() throws SJIOException
	{
		try
		{
			return readObjectFromConn();
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SJRuntimeException(cnfe);
		}		
	}
	
	private static byte[] serializeObject(Object o) throws SJIOException
	{
		return SJRuntimeUtils.serializeObject(o);	
	}
	
	private static byte[] serializeInt(int i) throws SJIOException
	{		
		return SJRuntimeUtils.serializeInt(i);
	}	
	
	private static byte serializeBoolean(boolean b) throws SJIOException
	{
		return b ? TRUE : FALSE;
	}
	
	private static Object deserializeObject(byte[] bs) throws SJIOException
	{
		return SJRuntimeUtils.deserializeObject(bs);
	}
	
	private static int deserializeInt(byte[] bs) throws SJIOException
	{		
		return SJRuntimeUtils.deserializeInt(bs);		
	}		
	
	private static boolean deserializeBoolean(byte b) throws SJIOException
	{
		return (b == TRUE);
	}
}
