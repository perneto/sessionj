package sessionj.types.sesstypes;

import java.util.*;

import polyglot.types.*;

import sessionj.SJConstants;
import sessionj.util.SJLabel;
import sessionj.util.SJCompilerUtils;

import static sessionj.SJConstants.*;

abstract public class SJBranchType_c extends SJSessionType_c implements SJBranchType
{
	private static final long serialVersionUID = SJConstants.SJ_VERSION;
	
	private HashMap<SJLabel, SJSessionType> cases = new HashMap<SJLabel, SJSessionType>();

	private boolean isDependentlyTyped;
	
	public SJBranchType_c(TypeSystem ts) // Probably redundant now.
	{
		this(ts, false);
	}

	public SJBranchType_c(TypeSystem ts, boolean isDependentlyTyped)
	{
		super(ts);
		
		this.isDependentlyTyped = isDependentlyTyped;
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

		return st == null ? null : st.copy();
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
			((SJSessionType_c) bt).setChild(child);
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
		
		// Loops on labels, but returns as soon as the matching label on the
        // counterpart is found. Hence, the loops won't go through all labels.
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
                return op.apply(case1, case2);
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

    private static final int TRIM_LEN = SJ_STRING_CASE_SEPARATOR.length() + 1;
	public String nodeToString()
	{
		StringBuilder s = new StringBuilder(branchConstructorOpen());

        for (SJLabel lab : labelSet()) {
            SJSessionType branchCase = getBranchCase(lab);

            s.append(lab).append(SJ_STRING_LABEL);
            s.append(branchCase == null ? " " : branchCase.toString());

            s.append(SJ_STRING_CASE_SEPARATOR).append(' ');
        }
        s.delete(s.length()-TRIM_LEN, s.length()); // Deletes the constructor prefix if the branch cases are empty (it shouldn't be empty, but this messes up error printing when it is).

		return s.append(branchConstructorClose()).toString();
	}

	abstract protected Set<SJLabel> selectComparsionLabelSet(Set<SJLabel> ourLabels, Set<SJLabel> theirLabels, NodeComparison op);
	
	abstract protected SJBranchType skeleton();
	
	abstract protected String branchConstructorOpen();
	abstract protected String branchConstructorClose();
    protected abstract SJSessionType dualSkeleton();
    public SJSessionType nodeDual() throws SemanticException {
        SJSessionType dual = dualSkeleton();
        for (SJLabel lab : labelSet())
        {
            dual = ((SJBranchType) dual).branchCase
                (lab, SJCompilerUtils.dualSessionType(branchCase(lab)));
        }
        return dual;
    }
    
  public final boolean isDependentlyTyped()
  {
  	return isDependentlyTyped;
  }
}
