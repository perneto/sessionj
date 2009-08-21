package sessionj.runtime.session;

import sessionj.runtime.*;
import sessionj.runtime.transport.SJConnection;

/**
 * Defines the methods needed for a Serializer, so different serialization
 * protocols can be used if necessary
 * 
 * @see SJDefaultSerializer
 * @author Raymond, Fred van den Driessche
 *
 */
abstract public class SJSerializer 
{	
	protected static final byte SJ_CONTROL = 1;
	//private static final byte SJ_DELEGATION = 2;
	
	protected static final byte SJ_OBJECT = 12; // SJ_Message flags are 2, 3, etc.
	protected static final byte SJ_REFERENCE = 13;
	protected static final byte SJ_BYTE = 14;
	protected static final byte SJ_INT = 15;
	protected static final byte SJ_BOOLEAN = 16;
	protected static final byte SJ_DOUBLE = 17;
	
	protected SJConnection conn; // Could be moved into an abstract super class (with constructor), but would be tying it to Java serialization.
	
	protected boolean isClosed = false;
	
	public SJSerializer(SJConnection conn) 
	{
		this.conn = conn;
	}
	
	abstract protected boolean zeroCopySupported();
	//abstract protected boolean hasBoundedBuffer();
	
	abstract public void close();
	
	abstract public void writeObject(Object o) throws SJIOException;
	abstract public void writeByte(byte b) throws SJIOException;
	abstract public void writeInt(int i) throws SJIOException;
	abstract public void writeBoolean(boolean b) throws SJIOException;
	abstract public void writeDouble(double d) throws SJIOException;

	abstract public Object readObject() throws SJIOException, ClassNotFoundException, SJControlSignal;
	abstract public byte readByte() throws SJIOException, SJControlSignal;
	abstract public int readInt() throws SJIOException, SJControlSignal;
	abstract public boolean readBoolean() throws SJIOException, SJControlSignal;
	abstract public double readDouble() throws SJIOException, SJControlSignal;
	
	abstract public void writeReference(Object o) throws SJIOException; // FIXME: these shouldn't be part of a "serializer" component.
	abstract public Object readReference() throws SJIOException, SJControlSignal;
	
	abstract public void writeControlSignal(SJControlSignal cs) throws SJIOException;
	abstract public SJControlSignal readControlSignal() throws SJIOException;
	
	abstract protected SJMessage nextMessage() throws SJIOException, ClassNotFoundException;//, SJControlSignal;
	
	public boolean isClosed()
	{
		return isClosed;
	}
	
	protected SJConnection getConnection()
	{
		return conn;
	}
}
