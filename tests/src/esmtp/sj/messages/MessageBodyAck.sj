//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MessageBodyAck.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MessageBodyAck
{
	private String msg;
	
	public MessageBodyAck(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
