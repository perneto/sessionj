package sessionj.types.sesstypes;

import java.util.*;

import polyglot.types.*;

import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

public class SJInbranchType_c extends SJBranchType_c implements SJInbranchType
{
	public static final long serialVersionUID = SJ_VERSION;

	public SJInbranchType_c(TypeSystem ts)
	{
		super(ts);
	}

	public SJInbranchType branchCase(SJLabel lab, SJSessionType st)
	{
		return (SJInbranchType) super.branchCase(lab, st);
	}
	
	protected boolean eligibleForEquals(SJSessionType st)
	{
		return (st instanceof SJInbranchType) && (labelSet().equals(((SJInbranchType) st).labelSet()));
	}
	
	protected boolean eligibleForSubtype(SJSessionType st)
	{
		return (st instanceof SJInbranchType) && (((SJInbranchType) st).labelSet()).containsAll(labelSet());
	}
	
	protected boolean eligibleForDualtype(SJSessionType st)
	{
		return (st instanceof SJOutbranchType)  && (labelSet().containsAll(((SJOutbranchType) st).labelSet()));
	}
	
	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException
	{
		if (!(st instanceof SJInbranchType)) 
		{
			throw new SemanticException("[SJInbranchType_c] Not subsumable: " + this + ", " + st);
		}
		
		SJBranchType them = (SJBranchType) st;
		SJBranchType res = skeleton();

		for (SJLabel lab : labelSet())
		{
			if (them.hasCase(lab))
			{
				SJSessionType ours = branchCase(lab);
				SJSessionType theirs = them.branchCase(lab);
				
				if (ours == null)
				{
					if (theirs != null)
					{
						throw new SemanticException("[SJInbranchType_c] Not subsumable: " + this + ", " + st);
					}
					
					res = res.branchCase(lab, null);
				}
				else
				{
					if (theirs == null)
					{
						throw new SemanticException("[SJInbranchType_c] Not subsumable: " + this + ", " + st);
					}
					
					res = res.branchCase(lab, ours.subsume(theirs));
				}
			}
		}

		return res;
	}
	
	protected Set<SJLabel> selectComparsionLabelSet(Set<SJLabel> ourLabels, Set<SJLabel> theirLabels, NodeComparison op)  
	{
		switch (op)
		{
			case EQUALS: return ourLabels;
			case SUBTYPE: return ourLabels; // The requirements of a sent inbranch are met by an implementation that can receive a bigger inbranch.
			case DUALTYPE: return theirLabels;
		}
		
		throw new RuntimeException("[SJInbranchType_c] Shouldn't get here: " + ourLabels + " " + op + " " + theirLabels);
	}	
	
	protected SJInbranchType skeleton()
	{
		return typeSystem().SJInbranchType();
	}
	
	protected String branchConstructorOpen()
	{
		return SJ_STRING_INBRANCH_OPEN;
	}
	
	protected String branchConstructorClose()
	{
		return SJ_STRING_INBRANCH_CLOSE;
	}
}
