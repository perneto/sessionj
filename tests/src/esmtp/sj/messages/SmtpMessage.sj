//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/SmtpMessage.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

abstract public class SmtpMessage
{
	public static final String SMTP_MESSAGE_SUFFIX = SJSmtpFormatter.LINE_FEED; //"\n";
	
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
