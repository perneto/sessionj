//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/Mail.sj -d tests/classes/ 

package smtp.sj.messages;

public class Mail
{
	private String msg;
	
	public Mail(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
