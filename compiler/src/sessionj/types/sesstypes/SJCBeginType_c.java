package sessionj.types.sesstypes;

import polyglot.types.*;
import sessionj.types.sesstypes.SJSessionType_c.NodeComparison;

import static sessionj.SJConstants.*;

public class SJCBeginType_c extends SJBeginType_c implements SJCBeginType
{
	public static final long serialVersionUID = SJ_VERSION;

	public SJCBeginType_c(TypeSystem ts)
	{
		super(ts);
	}

	protected boolean eligibleForEquals(SJSessionType st)
	{
		return st instanceof SJCBeginType;
	}
	
	protected boolean eligibleForSubtype(SJSessionType st)
	{
		return st instanceof SJCBeginType;
	}
	
	protected boolean eligibleForDualtype(SJSessionType st)
	{
		return st instanceof SJSBeginType;
	}
	
	protected boolean compareNode(NodeComparison op, SJSessionType st)
	{
		switch (op)
		{
			case EQUALS:  
			case SUBTYPE:   
			case DUALTYPE: 
				return true; // Checking eligibleFor... is already enough.
		}
		
		throw new RuntimeException("[SJCBeginType_c] Shouldn't get here: " + op);
	}
	
	// Could refine the return types for this and nodeClone, but not very useful.
	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException
	{
		if (!(st instanceof SJCBeginType))
		{
			throw new SemanticException("[SJCBeginType_c] Not subsumable: " + this + ", " + st);
		}

		return typeSystem().SJCBeginType();
	}

	public SJSessionType nodeClone()
	{
		return typeSystem().SJCBeginType();
	}

	public String nodeToString()
	{
		return SJ_STRING_CBEGIN;
	}
}
