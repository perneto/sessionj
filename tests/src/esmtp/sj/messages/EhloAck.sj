//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/EhloAck.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class EhloAck extends SmtpAck
{
	public static final String EHLO_ACK_REPLY_CODE = "250";
	
	private String msg;
	
	public EhloAck(String msg)
	{
		this.msg = msg;
	}
	
	public String replyCode()
	{
		return EHLO_ACK_REPLY_CODE;
	}
	
	public String body() 
	{
		return msg;
	}
		
	public boolean isParseable(String m)
	{
		return m.endsWith(SJSmtpFormatter.LINE_FEED);
	}
	
	//public EhloAck parse(String m) // Annoying: covariant return types not supported until Java 5.
	public SmtpAck parse(String m) 
	{
		return new EhloAck(SmtpAck.removeTrailingLineFeed(m));
	}	
}
