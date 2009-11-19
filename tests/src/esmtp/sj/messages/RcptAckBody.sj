//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/RcptAckBody.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class RcptAckBody implements SmtpParseable
{
	private String msg;
	
	public RcptAckBody(String msg)
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
	
	public SmtpParseable parse(String m)
	{
		return new RcptAckBody(SmtpAck.removeTrailingLineFeed(m));
	}		
}
