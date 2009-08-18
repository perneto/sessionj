package sessionj.util;

import java.io.Serializable;

import static sessionj.SJConstants.*;

// Immutable.
public final class SJLabel implements Cloneable, Serializable // Serializable hack - fix translation.
{
	public static final long serialVersionUID = SJ_VERSION;	
	
	private final String lab;

	public SJLabel(String lab)
	{
		this.lab = lab;
	}

	public final String labelValue()
	{
		return lab;
	}

	public final String toString()
	{
		return labelValue();
	}

	public final boolean equals(Object obj)
	{
		if (obj instanceof SJLabel)
		{
			return labelValue().equals(((SJLabel) obj).labelValue());
		}
		else
		{
			return false;
		}
	}

	public final int hashCode()
	{
		return labelValue().hashCode();
	}

	public final SJLabel clone()
	{
		return new SJLabel(lab); // String is immutable, so shallow clone is enough.
	}
}
