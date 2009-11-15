//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/Helo.sj -d tests/classes/ 

package smtp.sj.messages;

public class Helo
{
	private String msg;
	
	public Helo(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
