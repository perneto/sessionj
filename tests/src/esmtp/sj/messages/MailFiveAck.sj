//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MailFiveAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MailFiveAck
{
	private String msg;
	
	public MailFiveAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
