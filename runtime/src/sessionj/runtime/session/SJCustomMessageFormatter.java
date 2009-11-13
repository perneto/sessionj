/**
 * 
 */
package sessionj.runtime.session;

import java.nio.ByteBuffer;
import java.util.*;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJConnection;

/**
 * @author Raymond
 *
 * Intended to be an easier to use and more convenient interface than the full SJSerializer. The serializer should wrap this formatter around the underlying I/O streams.
 *
 * For "simple" protocols, this component may not need to be stateful; but for many protocols, we probably need to implement a state machine here. For that purpose, message formatters are like a localised version of the protocol: the messages received by writeMessage should follow the dual protocol to the messages returned by readNextMessage. But the formatter is an object: need object-based session types to control this.
 */
abstract public class SJCustomMessageFormatter 
{		
	private SJConnection conn;
	
	private ByteBuffer bb = ByteBuffer.allocate(1024); // FIXME: factor out constant; should be at least greater than 0. // Called by SJCustomSerializer in a "sequentialised" way, i.e. no race conditions. 
	
	abstract public byte[] formatMessage(Object o) throws SJIOException; // Maybe we should use e.g. SJCustomMessage (subclasses) rather than Object. SJCustomMessage could also offer message-specific formatting operations.
	abstract public Object parseMessage(ByteBuffer bb) throws SJIOException; // Pre: bb should already be flipped, i.e. ready for (relative) "getting". Also has to be non-blocking (for readNextMessage to work as intended). // FIXME: this is maybe not a good interface for the user.
		
	protected final void bindConnection(SJConnection conn) // Called by SJCustomSerializer.
	{
		this.conn = conn;
	}
	
	protected final void writeMessage(Object o) throws SJIOException
	{
		conn.writeBytes(formatMessage(o));
		conn.flush();
		
		//System.out.println("a: " + o + ", " + Arrays.toString(formatMessage(o)));
	}
	
	//public final SJMessage readNextMessage() throws SJIOException
	protected final Object readNextMessage() throws SJIOException // FIXME: would be better if could implement using arrived.
	{
		bb.put(conn.readByte()); // Need at least one byte for a message. This gives the guarantee that parseMessage will not be called with empty arrays.
						
		//byte[] bs = copyByteBufferContents(bb);		
		
		//System.out.println("b1: " + Arrays.toString(bs));
		
		Object o = null;
		
		//for (o = parseMessage(bs); o == null; o = parseMessage(bs)) // Assuming parseMessage returns null if parsing unsuccessful. (But what if we want to communicate a null?)
		for (o = parseMessage(getFlippedReadOnlyByteBuffer(bb)); o == null; o = parseMessage(getFlippedReadOnlyByteBuffer(bb))) // Assuming parseMessage returns null if parsing unsuccessful. (But what if we want to communicate a null?)
		{
			bb.put(conn.readByte()); // FIXME: reallocate bb if it gets full.
			
			//bs = copyByteBufferContents(bb);
			
			//System.out.println("b2: " + o + ", " + Arrays.toString(bs));
		}
		
		bb.clear();
		
		return o;
	}
	
	private static final ByteBuffer getFlippedReadOnlyByteBuffer(ByteBuffer bb)
	{
		ByteBuffer fbb = bb.asReadOnlyBuffer();
		
		fbb.flip();
		
		return fbb;
	}	
	
	/*private static final byte[] copyByteBufferContents(ByteBuffer bb)
	{
		bb.flip();
		
		byte[] bs = byteBufferToByteArray(bb);
		
		unflipByteBuffer(bb);
		
		return bs;
	}	
	
	// Pre: bb should already be flipped, i.e. ready for (relative) "getting".
	private static final byte[] byteBufferToByteArray(ByteBuffer bb)
	{
		byte[] bs = new byte[bb.limit()];
		
		bb.get(bs, 0, bs.length);		
		
		return bs;
	}

	private static final void unflipByteBuffer(ByteBuffer bb)
	{
		bb.position(bb.limit()); 
		bb.limit(bb.capacity());
	}*/
	
	/*class SJByteArray // Would like to avoid creating creating lots of new byte arrays all the time (as we do in byteBufferToByteArray), but also don't want to much work to be moved into parseMessage (e.g. we could give the ByteBuffer directly, but the user would have to handle it very carefully). 
	{
		public final int capacity;
		private int position = 0;
		
		private final byte[] bs; 
		
		public SJByteArray(int capacity)
		{
			this.capacity = capacity;
			this.bs = new byte[capacity];
		}
		
		public void read(ByteBuffer bb)
		{
			bb.get(bs, 0, bb.limit());
			
			position = bb.limit();
		}
		
		public byte[] ...
		
		public int capacity()
		{
			return capacity;
		}
		
		public int position()
		{
			return position;
		}
	}*/	
}
