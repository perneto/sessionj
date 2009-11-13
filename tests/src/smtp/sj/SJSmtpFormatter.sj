//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/SJUtf8Formatter.sj -d tests/classes/ 

package smtp.sj;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
import sessionj.runtime.session.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;

import smtp.sj.messages.*;

// Message formatters are like a localised version of the protocol: the messages received by writeMessage should follow the dual protocol to the messages returned by readNextMessage. But the formatter is an object: need object-based session types to control this.
public class SJSmtpFormatter extends SJUtf8Formatter
{			
	public Object parseMessage(ByteBuffer bb) throws SJIOException // bb is read-only and already flipped (from SJCustomeMessageFormatter).
	{
		try
		{
			String m = decodeFromUtf8(bb);
			
			if (m.equals("L") || m.equals("LA"))
			{
				return null;
			}
			
			return m;
		}
		catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}
	}
}
