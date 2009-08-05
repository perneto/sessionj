package sessionj.runtime;

import java.io.Serializable;

import sessionj.runtime.net.SJRuntime;

import static sessionj.SJConstants.*;

public class SJProtocol implements Serializable
{
	public static final long serialVersionUID = SJ_VERSION;

	private String encoded;

	public SJProtocol(String encoded)
	{
		this.encoded = encoded;
	}

	public String encoded()
	{
		return encoded; // Maybe should return the decoded type. 
	}
	
	public String toString()
	{
		try
		{
			return SJRuntime.getTypeEncoder().decode(encoded).toString();
		}
		catch (SJIOException ioe)
		{
			return ioe.getMessage();
		}
	}
}
