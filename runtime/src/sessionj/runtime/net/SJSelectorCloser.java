package sessionj.runtime.net;

public class SJSelectorCloser
{
	private SJSelectorAllTransports s; // FIXME: would be better for SJSelector.   
	
	protected SJSelectorCloser(SJSelectorAllTransports s)
	{
		this.s = s;
	}
	
	public void close()
	{
		try
		{
			this.s.close();
		}
		catch (Exception x) // FIXME.
		{
			x.printStackTrace();
		}
	}
}
