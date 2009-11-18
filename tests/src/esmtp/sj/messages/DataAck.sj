//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/DataAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class DataAck
{
	private String msg;
	
	public DataAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
