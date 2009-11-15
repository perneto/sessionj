//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/messages/Data.sj -d tests/classes/ 

package smtp.sj.messages;

public class Data
{
	private String msg;
	
	public Data(String msg)
	{
		this.msg = msg;
	}
	
	public String toString()
	{
		return msg;
	}
}
