package sessionj.runtime;

import java.io.Serializable;

import sessionj.runtime.net.SJRuntime;

import static sessionj.SJConstants.*;
import sessionj.types.sesstypes.SJSessionType;

public class SJProtocol implements Serializable
{
	public static final long serialVersionUID = SJ_VERSION;

	private final String encoded;
    private final SJSessionType canonicalForm;

    public SJProtocol(String encoded) throws SJIOException {
		this.encoded = encoded;
        canonicalForm = SJRuntime.decodeType(this.encoded).getCanonicalForm();
    }

	public String encoded()
	{
		return encoded; // Maybe should return the decoded type. 
	}

    public SJSessionType type() throws SJIOException 
    {
    	return canonicalForm;
    }
	
	public String toString()
	{
		try
		{
			return SJRuntime.decodeType(encoded).toString();
		}
		catch (SJIOException ioe)
		{
			return ioe.getMessage();
		}
	}
}
