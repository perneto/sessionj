//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/Quit.sj -d tests/classes/ 

package smtp.sj.messages;

public class Quit
{
	private String msg;
	
	public Quit(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
