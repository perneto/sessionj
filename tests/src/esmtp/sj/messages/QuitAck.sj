//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/QuitAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class QuitAck
{
	private String msg;
	
	public QuitAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
