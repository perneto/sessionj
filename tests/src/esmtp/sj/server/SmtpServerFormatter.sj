//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/SmtpServerFormatter.sj -d tests/classes/ 

package esmtp.sj.server;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.session.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;

import esmtp.sj.messages.*;

// Message formatters are like a localised version of the protocol: the messages received by writeMessage should follow the dual protocol to the messages returned by readNextMessage. But the formatter is an object: need object-based session types to control this.
public class SmtpServerFormatter extends SJUtf8Formatter
{			
	// Maybe these should be in the Server class.
	protected static final String MAIL_FROM_LABEL = "MAIL_FROM"; 
	protected static final String RCPT_TO_LABEL = "RCPT_TO";
	protected static final String DATA_LABEL = "DATA";
	protected static final String QUIT_LABEL = "QUIT";	
	
	private static final Ehlo EHLO = new Ehlo("");
	private static final Object MAIL_OR_RCPT_OR_DATA_OR_QUIT = "MAIL_OR_RCPT_OR_DATA_OR_QUIT";
	private static final EmailAddress EMAIL_ADDRESS = new EmailAddress("");
	private static final MessageBody MESSAGE_BODY = new MessageBody("");
	
	private Object state = EHLO;	
	private Object prevState = null; // Not currently needed.
	
	public Object parseMessage(ByteBuffer bb, boolean eof) throws SJIOException // bb is read-only and already flipped (from SJCustomeMessageFormatter).
	{
		try
		{
			String m = decodeFromUtf8(bb);
			
			if (eof) 
			{
				throw new SJIOException("[SmtpServerFormatter] Unexpected EOF: " + m);
			}
			
			if (state == EHLO)
			{
				if (EHLO.isParseableFrom(m))
				{
					prevState = state;
					state = MAIL_OR_RCPT_OR_DATA_OR_QUIT;
						
					return EHLO.parse(m);
				}				
			}
			else if (state == MAIL_OR_RCPT_OR_DATA_OR_QUIT)
			{
				m = m.toUpperCase();
				
				if (m.equals("MAIL FROM:"))
				{
					prevState = state;
					state = EMAIL_ADDRESS;
					
					return MAIL_FROM_LABEL;
				}
				else if (m.equals("RCPT TO:"))
				{
					prevState = state;
					state = EMAIL_ADDRESS;	
					
					return RCPT_TO_LABEL;
				}
				else if (deleteSpaces(m).equals("DATA\n"))
				{
					prevState = state;
					state = MESSAGE_BODY;	
					
					return DATA_LABEL;
				}
				else if (deleteSpaces(m).equals("QUIT\n"))
				{
					prevState = null;
					state = EHLO;
					
					return QUIT_LABEL;
				}
			}
			else if (state == EMAIL_ADDRESS)
			{
				//m = m.trim(); // Also removes the final '\n'.
				m = deleteSpaces(m);
				
				if (EMAIL_ADDRESS.isParseableFrom(m))
				{
					prevState = state;
					state = MAIL_OR_RCPT_OR_DATA_OR_QUIT;
						
					return EMAIL_ADDRESS.parse(m);
				}				
			}
			else if (state == MESSAGE_BODY)
			{
				if (MESSAGE_BODY.isParseableFrom(m))
				{
					prevState = state;
					state = MAIL_OR_RCPT_OR_DATA_OR_QUIT;
						
					return MESSAGE_BODY.parse(m);
				}						
			}
			else 
			{
				throw new SJIOException("[SmtpServerFormatter] Shouldn't get in here.");
			}
			
			return null;
		}
		catch (CharacterCodingException cce)
		{
			throw new SJIOException(cce);
		}
	}
	
	private static final String deleteSpaces(String m) // Just spaces, not white space.
	{
		return m.replaceAll(" ", "");
	}
}
