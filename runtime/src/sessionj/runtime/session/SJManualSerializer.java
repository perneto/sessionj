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

import static sessionj.runtime.util.SJRuntimeUtils.*;

/**
 * @author Raymond
 *
 * FIXME: use SJRuntime as a factory to create an appropriate serializer (based on connection and user parameters). Also for session protocols object.
 *
 * FIXME: make clear that SJStreamSerializer and SJManualSerializer are just different implementations of the *same* protocol.  
 *
 */
public class SJManualSerializer extends SJSerializer
{	
	private static final byte FALSE = 0;
	private static final byte TRUE = 1;
	
	public SJManualSerializer(SJConnection conn) throws SJIOException
	{
		super(conn);
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
		if (!isClosed)
		{
			isClosed = true;
			
			try
			{
				if (conn != null) // Delegation case 2: no connection created between passive party and session acceptor.
				{
					conn.flush();				
				}
			}
			catch (SJIOException ioe)
			{
				
			}
		}			
	}
	
	public void writeObject(Object o) throws SJIOException
	{
		conn.writeByte(SJ_OBJECT);
		
		writeObjectToConn(o);
	}	
	
	public void writeByte(byte b) throws SJIOException
	{		
		conn.writeByte(SJ_BYTE);			
		conn.writeByte(b);
		conn.flush();
	}
	
	public void writeInt(int i) throws SJIOException
	{		
		conn.writeByte(SJ_INT);
		
		byte[] bs = serializeInt(i);		
		
		conn.writeBytes(bs);
		conn.flush();
	}
	
	public void writeBoolean(boolean b) throws SJIOException
	{
		conn.writeByte(SJ_BOOLEAN);			
		conn.writeByte(serializeBoolean(b));
		conn.flush();
	}

	public void writeDouble(double d) throws SJIOException
	{
		conn.writeByte(SJ_DOUBLE);		
		
		//byte[] bs = serializeDouble(d);
		
		//conn.writeBytes(bs);
		//conn.flush();
		
		writeObject(new Double(d)); // FIXME.
	}
	
	public Object readObject() throws SJIOException, ClassNotFoundException, SJControlSignal
	{
		Object o;
		
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
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return o;
	}
	
	public byte readByte() throws SJIOException, SJControlSignal
	{
		byte b;
		
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
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return b;
	}
	
	public int readInt() throws SJIOException, SJControlSignal
	{
		int i;
		
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
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return i;
	}
	
	public boolean readBoolean() throws SJIOException, SJControlSignal
	{
		boolean b;

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
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return b;
	}
	
	public double readDouble() throws SJIOException, SJControlSignal
	{
		double d;
		
		byte flag = conn.readByte();	
		
		if (flag == SJ_DOUBLE)
		{			
			d = ((Double) readObjectFromConnNoCNFE()).doubleValue(); // FIXME.
		}
		else if (flag == SJ_CONTROL)
		{								
			throw (SJControlSignal) readObjectFromConnNoCNFE();
		}					
		else
		{
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return d;
	}
	
	public void writeReference(Object o) throws SJIOException
	{		
		conn.writeByte(SJ_REFERENCE);
		
		((SJLocalConnection) conn).writeReference(o); // Ignoring possibly open oos, writing straight to conn (is this OK?).
	}
	
	public Object readReference() throws SJIOException, SJControlSignal
	{
		/*if (!(conn instanceof SJLocalConnection))
		{
			throw new SJIOException("[SJManualSerializer] Connection does not support reference read.");
		}*/	
		
		byte flag = conn.readByte();
					
		if (flag == SJ_REFERENCE)
		{
			return ((SJLocalConnection) conn).readReference();
		}
		else if (flag == SJ_CONTROL)
		{						
			throw (SJControlSignal) readObjectFromConnNoCNFE();
		}			
		else
		{
			throw new SJRuntimeException("[SJManualSerializer] Unexpected flag: " + flag);
		}					
	}

	public void writeControlSignal(SJControlSignal cs) throws SJIOException
	{
		byte[] bs = serializeObject(cs);
		
		conn.writeByte(SJ_CONTROL);
		conn.writeBytes(serializeInt(bs.length));
		conn.writeBytes(bs);
	}

	public SJControlSignal readControlSignal() throws SJIOException
	{
		SJControlSignal cs = null;
		
		byte flag = conn.readByte();
		
		if (flag == SJ_CONTROL)
		{
			cs = (SJControlSignal) readObjectFromConnNoCNFE();
		}
		else
		{
			throw new SJIOException("[SJManualSerializer] Unexpected flag: " + flag);
		}
		
		return cs;
	}
	
	protected SJMessage nextMessage() throws SJIOException, ClassNotFoundException//, SJControlSignal
	{
		byte type;
		Object o;
		
		byte flag = conn.readByte();
		
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
				throw new SJRuntimeException("[SJManualSerializer] Unsupported flag: " + flag);
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
		byte[] bs = new byte[SJ_SERIALIZED_INT_LENGTH];
		
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
	
	/*private static byte[] serializeDouble(double d) throws SJIOException
	{		
		return SJRuntimeUtils.serializeObject(new Double(d)); // FIXME.
	}*/	
	
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
	
	/*private static double deserializeDouble(byte[] bs) throws SJIOException
	{		
		return ((Double) SJRuntimeUtils.deserializeObject(bs)).doubleValue();		
	}*/		
}
