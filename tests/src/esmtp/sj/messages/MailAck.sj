//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MailAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MailAck
{
	private String msg;
	
	public MailAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
