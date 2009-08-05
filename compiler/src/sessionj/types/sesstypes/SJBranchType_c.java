package sessionj.types.sesstypes;

import java.util.*;

import polyglot.types.*;

import sessionj.types.*;
import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

abstract public class SJBranchType_c extends SJSessionType_c implements SJBranchType
{
	private HashMap<SJLabel, SJSessionType> cases = new HashMap<SJLabel, SJSessionType>();

	public SJBranchType_c(TypeSystem ts)
	{
		super(ts);
	}

	public Set<SJLabel> labelSet()
	{
		return cases.keySet();
	}

	public boolean hasCase(SJLabel lab)
	{
		return labelSet().contains(lab);
	}

	protected SJSessionType getBranchCase(SJLabel lab)
	{
		return cases.get(lab);
	}
	
	protected void setBranchCase(SJLabel lab, SJSessionType st)
	{
		cases.put(lab, st);
	}
	
	public SJSessionType branchCase(SJLabel lab)
	{
		SJSessionType st = getBranchCase(lab);

		return (st == null) ? null : st.copy();
	}

	public SJBranchType branchCase(SJLabel lab, SJSessionType st) // Used by copy/clone routines, so those cannot be used here, else mutual dependency (will loop forever).
	{
		SJBranchType bt = (SJBranchType) nodeClone(); // Defensive copy.
		
		if (st != null)
		{
			st = st.copy();
		}

		((SJBranchType_c) bt).setBranchCase(lab, st); // Defensive copy.
		
		SJSessionType child = child(); // Returns a defensive copy.
		
		if (child != null)
		{
			((SJBranchType_c) bt).setChild(child);
		}
		
		return bt;  
	}

	public SJSessionType removeCase(SJLabel lab)
	{
		return cases.remove(lab); // No need to copy here.
	}

	protected boolean compareNode(NodeComparison op, SJSessionType st)
	{	
		SJBranchType_c them = (SJBranchType_c) st;		
		Set<SJLabel> theirLabels = them.labelSet(); 		
		
		for (SJLabel lab : selectComparsionLabelSet(labelSet(), theirLabels, op))
		{
			SJSessionType case1 = getBranchCase(lab);
			SJSessionType case2 = them.getBranchCase(lab);
	
			if (case1 == null)
			{
				if (case2 != null) return false;
			}
			else
			{
				switch (op)
				{
					case EQUALS: return case1.typeEquals(case2);
					case SUBTYPE: return case1.isSubtype(case2);
					case DUALTYPE: return case1.isDualtype(case2);						
				}
			}
		}						
		
		throw new RuntimeException("[SJBranchType_c] Shouldn't get here: " + op);
	}	

	public boolean nodeWellFormed()
	{
		if (labelSet().isEmpty())
		{
			return false;
		}

		for (SJLabel lab : labelSet())
		{
			SJSessionType st = getBranchCase(lab);

			if (st != null && !st.treeWellFormed())
			{
				return false;
			}
		}

		return true;
	}

	public SJSessionType nodeClone()
	{
		SJBranchType bt = skeleton();

		for (SJLabel lab : labelSet())
		{
			bt = bt.branchCase(lab, getBranchCase(lab)); // null or else cloned by setter method. 
		}

		return bt;
	}

	public String nodeToString()
	{
		String s = branchConstructorOpen();

		for (Iterator<SJLabel> i = labelSet().iterator(); i.hasNext(); ) // Using an iterator because...
		{
			SJLabel lab = i.next();
			SJSessionType branchCase = getBranchCase(lab);

			s = s + lab + SJ_STRING_LABEL;
			s = s + ((branchCase == null) ? " " : branchCase.toString());

			if (i.hasNext()) s = s + SJ_STRING_CASE_SEPARATOR + " "; // ...want to do this look ahead check.
		}

		return s + branchConstructorClose();
	}

	abstract protected Set<SJLabel> selectComparsionLabelSet(Set<SJLabel> ourLabels, Set<SJLabel> theirLabels, NodeComparison op);
	
	abstract protected SJBranchType skeleton();
	
	abstract protected String branchConstructorOpen();
	abstract protected String branchConstructorClose();
}
