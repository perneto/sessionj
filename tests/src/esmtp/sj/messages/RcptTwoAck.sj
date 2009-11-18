//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/RcptTwoAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class RcptTwoAck
{
	private String msg;
	
	public RcptTwoAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
