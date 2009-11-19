//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/DataLineFeed.sj -d tests/classes/ 

package esmtp.sj.messages;

public class DataLineFeed extends SmtpCommand
{	
	public String command()
	{
		return "";
	}
	
	public String body()
	{
		return "";
	}
}
