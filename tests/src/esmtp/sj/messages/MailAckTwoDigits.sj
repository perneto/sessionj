//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MailAckTwoDigits.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MailAckTwoDigits
{
	private String msg;
	
	public MailAckTwoDigits(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
