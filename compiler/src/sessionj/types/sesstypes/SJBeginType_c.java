package sessionj.types.sesstypes;

import polyglot.types.*;

import static sessionj.SJConstants.*;

abstract public class SJBeginType_c extends SJSessionType_c implements SJBeginType
{
	public SJBeginType_c(TypeSystem ts)
	{
		super(ts);
	}

	public boolean nodeWellFormed() 
	{
		return false; // begin as a session type element other than initial prefix is bad; the primary wellFormed routine in SJSessionType_c handles the prefix separately.    
	}
}
