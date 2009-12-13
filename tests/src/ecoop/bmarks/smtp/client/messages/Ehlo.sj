package ecoop.bmarks.smtp.client.messages;

public class Ehlo extends SmtpCommand 
{
	public static final String EHLO_COMMAND = "EHLO";
	
	private String fqdn;
	
	public Ehlo(String fqdn)
	{
		this.fqdn = fqdn;
	}
		
	public String command()
	{
		return EHLO_COMMAND;
	}
	
	public String body()
	{
		return fqdn;
	}
}
