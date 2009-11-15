//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/Rcpt.sj -d tests/classes/ 

package smtp.sj.messages;

public class Rcpt
{
	private String msg;
	
	public Rcpt(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
