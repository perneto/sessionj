//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/SmtpCommand.sj -d tests/classes/ 

package esmtp.sj.messages;

abstract public class SmtpCommand extends SmtpMessage
{
	abstract public String command();	
	
	public String prefix()
	{
		return command();
	}
}
