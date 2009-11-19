//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MessageBodyAck.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class MessageBodyAck extends SmtpAck
{
	public static final String MESSAGE_BODY_REPLY_CODE = "250";
	
	private String msg;
	
	public MessageBodyAck(String msg)
	{
		this.msg = msg;
	}
	
	public String replyCode()
	{
		return MESSAGE_BODY_REPLY_CODE;
	}
	
	public String body()
	{
		return msg;
	}	
	
	public boolean isParseable(String m)
	{
		return m.startsWith(MESSAGE_BODY_REPLY_CODE) && m.endsWith(SJSmtpFormatter.LINE_FEED);
	}
	
	public SmtpParseable parse(String m)
	{
		return new MessageBodyAck(SmtpAck.removeTrailingLineFeed(m).substring(MESSAGE_BODY_REPLY_CODE.length()));
	}		
}
