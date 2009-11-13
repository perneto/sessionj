//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/MyMessage.sj -d tests/classes/ 

package smtp.sj.messages;

public class MyMessage
{
	private String m;
	
	public MyMessage(String m)
	{
		this.m = m;
	}
	
	public String toString()
	{
		return m;
	}
}
