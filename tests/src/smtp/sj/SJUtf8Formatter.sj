//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/SJUtf8Formatter.sj -d tests/classes/ 

package smtp.sj;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;
import sessionj.runtime.session.*;

public class SJUtf8Formatter extends SJCustomMessageFormatter
{			
	private static final Charset cs = Charset.forName("UTF8");
	private static final CharsetEncoder ce = cs.newEncoder();
	private static final CharsetDecoder cd = cs.newDecoder();
	
	public byte[] formatMessage(Object o) throws SJIOException 
	{
		try
		{
			return ce.encode(CharBuffer.wrap(o.toString())).array();
		}
		catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}			
	}
	
	public Object parseMessage(byte[] bs) throws SJIOException 
	{
		if (bs.length == 0)
		{
			return null;
		}
		
		try
		{
			return cd.decode(ByteBuffer.wrap(bs)).toString();
		}
		catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}
	}
}
