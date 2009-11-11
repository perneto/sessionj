/**
 * 
 */
package sessionj.runtime.session;

import java.nio.ByteBuffer;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;

/**
 * @author Raymond
 *
 * Intended to be an easier to use and more convenient interface than the full SJSerializer. The serializer should wrap this formatter around the underlying I/O streams.
 *
 */
abstract public class SJCustomMessageFormatter 
{		
	private SJConnection conn;
	
	void bindConnection(SJConnection conn)
	{
		this.conn = conn;
	}
	
	protected final void writeMessage(Object o) throws SJIOException
	{
		conn.writeBytes(formatMessage(o));
	}

	ByteBuffer bb = ByteBuffer.allocate(1024); // Called by SJCustomSerializer in a "sequentialised" way, i.e. no race conditions.
	
	//public final SJMessage readNextMessage() throws SJIOException
	protected final Object readNextMessage() throws SJIOException 
	{
		
		//while (parseMessage(bb.g))
		{
				
		}
		
		return null;
	}
	
	abstract public byte[] formatMessage(Object o) throws SJIOException; // Maybe we should use e.g. SJCustomMessage (subclasses) rather than Object. SJCustomMessage could also offer message-specific formatting operations.
	abstract public Object parseMessage(byte[] bs) throws SJIOException;
}
