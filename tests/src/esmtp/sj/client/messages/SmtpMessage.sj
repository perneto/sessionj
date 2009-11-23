//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/client/messages/SmtpMessage.sj -d tests/classes/ 

package esmtp.sj.client.messages;

abstract public class SmtpMessage
{
	public static final String LINE_FEED = "\n";
	public static final String SMTP_MESSAGE_SUFFIX = LINE_FEED;
	
	public SmtpMessage()
	{

	}
	
	abstract public String prefix();	
	abstract public String body();
	
	public String suffix()
	{
		return SMTP_MESSAGE_SUFFIX;
	}
	
	public String construct()
	{
		return prefix() + body() + suffix();
	}
	
	public String toString()
	{
		return construct();
	}
}
