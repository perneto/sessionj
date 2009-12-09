package sessionj.runtime.session;

public class SJDelegationSignal extends SJControlSignal
{
	private String hostName;
	private int port;
	
	public SJDelegationSignal(String hostName, int port)
	{
		this.hostName = hostName;
		this.port = port;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String toString()
	{
		return "SJDelegationSignal=" + hostName + ":" + port; 
	}
}
