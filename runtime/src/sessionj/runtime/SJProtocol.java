package sessionj.runtime;

import java.io.Serializable;

import sessionj.runtime.net.SJRuntime;

import static sessionj.SJConstants.*;
import sessionj.types.sesstypes.SJSessionType;

public class SJProtocol implements Serializable
{
	public static final long serialVersionUID = SJ_VERSION;

	private final String encoded;
    private SJSessionType canonicalForm = null;

    public SJProtocol(String encoded) {
		this.encoded = encoded;
    }

	public String encoded()
	{
		return encoded; // Maybe should return the decoded type. 
	}

    public synchronized SJSessionType type() throws SJIOException 
    {
        if (canonicalForm == null) {
            canonicalForm = SJRuntime.decodeType(encoded).getCanonicalForm();
        }
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
