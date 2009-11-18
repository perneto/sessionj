//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/ServerGreeting.sj -d tests/classes/ 

package esmtp.sj.messages;

public class ServerGreeting 
{
	private String greeting;
	
	public ServerGreeting(String greeting)
	{
		this.greeting = greeting;
	}
	
	public String toString()
	{
		return greeting;
	}
}
