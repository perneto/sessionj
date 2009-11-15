//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/SJSmtpFormatter.sj -d tests/classes/ 

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
	private static final String LINE_FEED = "\n";
	
	// Rather than "manual" state management (enum, switch, update), would be nice to use a selector with typecase; then we'd get static typing. Afterall, objects (actors?) are essentially event-driven entities. 
	private static final int GREETING = 1;
	private static final int HELO_ACK = 2;
	private static final int MAIL_ACK = 3;
	private static final int RCPT_ACK = 4;
	private static final int DATA_ACK = 5;
	private static final int QUIT_ACK = 6;
	
	private int state = GREETING;
	
	private static final byte[] copyByteBufferContents(ByteBuffer bb)
	{
		byte[] bs = new byte[bb.limit()];

		bb.get(bs);
		
		return bs;
	}
	
	public Object parseMessage(ByteBuffer bb, boolean eof) throws SJIOException // bb is read-only and already flipped (from SJCustomeMessageFormatter).
	{
		byte[] bs = copyByteBufferContents(bb);
		
		int foo = 0;
		
		//try
		{
			if (eof && state != QUIT_ACK) 
			{
				//String m = decodeFromUtf8(bb);
				String m = decodeFromUtf8(bs);
				
				throw new SJIOException("[SJSmtpFormatter] Unexpected EOF: " + m);
			}				
			
			if (state == GREETING)
			{
				//String greeting = decodeFromUtf8(bb);
				String greeting = decodeFromUtf8(bs);
								
				if (greeting.endsWith(LINE_FEED))
				{
					state = HELO_ACK;
					
					return new ServerGreeting(greeting.substring(0, greeting.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else if (state == HELO_ACK)
			{
				//String ack = decodeFromUtf8(bb);
				String ack = decodeFromUtf8(bs);
				
				if (ack.endsWith(LINE_FEED))
				{
					state = MAIL_ACK;
					
					return new HeloAck(ack.substring(0, ack.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else if (state == MAIL_ACK)
			{
				String ack = decodeFromUtf8(bs);
				
				if (ack.endsWith(LINE_FEED))
				{
					state = RCPT_ACK;
					
					return new MailAck(ack.substring(0, ack.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else if (state == RCPT_ACK)
			{
				String ack = decodeFromUtf8(bs);
				
				if (ack.endsWith(LINE_FEED))
				{
					state = DATA_ACK;
					
					return new RcptAck(ack.substring(0, ack.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else if (state == DATA_ACK)
			{
				String ack = decodeFromUtf8(bs);
				
				if (ack.endsWith(LINE_FEED))
				{
					if (foo == 0)
					{
						foo++;
					}
					else
					{
						state = QUIT_ACK;
					}
					
					return new DataAck(ack.substring(0, ack.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else if (state == QUIT_ACK)
			{
				String ack = decodeFromUtf8(bs);
				
				if (ack.endsWith(LINE_FEED)) // Or is it just EOF directly?
				{
					return new QuitAck(ack.substring(0, ack.length() - LINE_FEED.length()));
				}
				else
				{
					return null;
				}
			}
			else 
			{
				throw new SJIOException("[SJSmtpParser] Shouldn't get in here.");
			}
		}
		/*catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}*/
	}
}
