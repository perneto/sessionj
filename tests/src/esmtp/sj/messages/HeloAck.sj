//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/HeloAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class HeloAck
{
	private String msg;
	
	public HeloAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
