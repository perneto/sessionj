package sessionj.types.sesstypes;

import polyglot.types.*;

import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

public class SJRecurseType_c extends SJSessionType_c implements SJRecurseType
{
	public static final long serialVersionUID = SJ_VERSION;

	private SJLabel lab;

	public SJRecurseType_c(TypeSystem ts, SJLabel lab)
	{
		super(ts);

		this.lab = lab;
	}

	public SJLabel label()
	{
		return lab;
	}

	protected boolean eligibleForEquals(SJSessionType st)
	{
		return (st instanceof SJRecurseType) && compareLabel((SJRecurseType) st);
	}
	
	protected boolean eligibleForSubtype(SJSessionType st)
	{
		return (st instanceof SJRecurseType) && compareLabel((SJRecurseType) st);
	}
	
	protected boolean eligibleForDualtype(SJSessionType st)
	{
		return (st instanceof SJRecurseType) && compareLabel((SJRecurseType) st);
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
		
		throw new RuntimeException("[SJRecurseType_c] Shouldn't get here: " + op);
	}

	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException
	{
		if (!((st instanceof SJRecurseType) && compareLabel((SJRecurseType) st)))
		{
			throw new SemanticException("[SJRecurseType_c] Not subsumable: " + this + ", " + st);
		}

		return typeSystem().SJRecurseType(label()); // SJLabel is immutable, no need to clone.
	}

	public boolean nodeWellFormed() // Well-formed recursions checked by external routine.
	{
		return true;
	}

	public SJSessionType nodeClone()
	{
		return typeSystem().SJRecurseType(label()); // SJLabel is immutable, no need to clone.
	}

	public String nodeToString()
	{
		return SJ_STRING_RECURSE_PREFIX + label().toString();
	}
	
	private boolean compareLabel(SJRecurseType rt)
	{
		return label().equals(rt.label());
	}	
}
