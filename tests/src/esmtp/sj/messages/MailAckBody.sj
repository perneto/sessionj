//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/MailAckBody.sj -d tests/classes/ 

package esmtp.sj.messages;

public class MailAckBody extends SmtpMessage
{
	private static final String prefix1 = SmtpMessage.HYPHEN_SEPARATOR;
	private static final String prefix2 = SmtpMessage.SPACE_SEPARATOR;
	private static final String suffix = SmtpMessage.LINE_FEED;
		
	public MailAckBody(String msg)
	{
		super(msg);
	}

	public boolean isParseableFrom(String m)
	{
		return (m.startsWith(prefix1) || m.startsWith(prefix2)) && m.endsWith(suffix);
	}
	
	public SmtpMessage parse(String m)
	{
		return new MailAckBody(SmtpMessage.removeLineFeedSuffix(m)); // Keeps the prefix.
	}		
	
	public String format()
	{
		return content() + suffix;
	}
}
