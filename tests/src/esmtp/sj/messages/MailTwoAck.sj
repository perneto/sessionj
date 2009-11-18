//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MailTwoAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MailTwoAck
{
	private String msg;
	
	public MailTwoAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
