//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/EhloAckBody.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class EhloAckBody implements SmtpParseable
{
	private String msg;
	
	public EhloAckBody(String msg)
	{
		this.msg = msg;
	}
		
	public String toString()
	{
		return msg;
	}
	
	public boolean isParseable(String m)
	{
		return m.endsWith(SJSmtpFormatter.LINE_FEED);
	}
	
	//public EhloAckBody parse(String m) // Annoying: covariant return types not supported until Java 5.
	public SmtpParseable parse(String m) 
	{
		return new EhloAckBody(SmtpAck.removeTrailingLineFeed(m));
	}	
}
