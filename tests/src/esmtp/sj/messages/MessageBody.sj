//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MessageBody.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MessageBody extends SmtpMessage
{
	public static final String MESSAGEBODY_SUFFIX = ".\n";
	
	private String body;
	
	public MessageBody(String body)
	{
		this.body = body;
	}
	
	public String prefix()
	{
		return "";
	}
	
	public String body()
	{
		return body;
	}
	
	public String suffix()
	{
		return super.suffix() + MESSAGEBODY_SUFFIX; 
	}
}
