//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/DataAck.sj -d tests/classes/ 

package esmtp.sj.messages;

import esmtp.sj.*;

public class DataAck extends SmtpAck
{
	public static final String DATA_REPLY_CODE = "354";
	
	private String msg;
	
	public DataAck(String msg)
	{
		this.msg = msg;
	}
	
	public String replyCode()
	{
		return DATA_REPLY_CODE;
	}
	
	public String body()
	{
		return msg;
	}	
	
	public boolean isParseable(String m)
	{
		return m.startsWith(DATA_REPLY_CODE) && m.endsWith(SJSmtpFormatter.LINE_FEED);
	}
	
	public SmtpParseable parse(String m)
	{
		return new DataAck(SmtpAck.removeTrailingLineFeed(m).substring(DATA_REPLY_CODE.length()));
	}		
}
