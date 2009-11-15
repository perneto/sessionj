package sessionj.runtime.session;

import java.nio.*;
import java.nio.charset.*;

import sessionj.runtime.*;

/*
 * "Converts" objects to UTF-8 via toString. Could instead make a specific SJUtf8Message, and specify a special method (e.g. toUtf8) for this purpose. 
 */
abstract public class SJUtf8Formatter extends SJCustomMessageFormatter
{			
	private static final Charset cs = Charset.forName("UTF8");
	private static final CharsetEncoder ce = cs.newEncoder();
	private static final CharsetDecoder cd = cs.newDecoder();
	
	public final byte[] encodeAsUtf8(String m) throws CharacterCodingException
	{
		//ce.reset(); // Not needed when using the convenience encode method below.		
		//ce = cs.newEncoder();
		
		return ce.encode(CharBuffer.wrap(m)).array(); // Rather than centralising the encoding and decoding routines in the formatter, we could use a "SJCustomMessage" and do it on a per-message basis.
	}
	
	/*public final String decodeFromUtf8(byte[] bs) throws CharacterCodingException
	{
		return cd.decode(ByteBuffer.wrap(bs)).toString();
	}*/
	
	// Pre: bb should already be flipped, i.e. ready for (relative) "getting".
	public final String decodeFromUtf8(ByteBuffer bb) throws CharacterCodingException 
	{
		return cd.decode(bb).toString();
	}
	
	public final byte[] formatMessage(Object o) throws SJIOException 
	{
		try
		{
			return encodeAsUtf8(o.toString());
		}
		catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}			
	}	
}
