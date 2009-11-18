//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/RcptFiveAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class RcptFiveAck
{
	private String msg;
	
	public RcptFiveAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
