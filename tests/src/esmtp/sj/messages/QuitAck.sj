//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/QuitAck.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class QuitAck extends SmtpAck
{
	public static final String QUIT_REPLY_CODE = "221";
	
	private String msg;
	
	public QuitAck(String msg)
	{
		this.msg = msg;
	}
	
	public String replyCode()
	{
		return QUIT_REPLY_CODE;
	}
	
	public String body()
	{
		return msg;
	}
	
	public boolean isParseable(String m)
	{
		return m.startsWith(QUIT_REPLY_CODE) && m.endsWith(SJSmtpFormatter.LINE_FEED);
	}
	
	public SmtpParseable parse(String m)
	{
		return new MailAckBody(SmtpAck.removeTrailingLineFeed(m).substring(QUIT_REPLY_CODE.length()));
	}		
}
