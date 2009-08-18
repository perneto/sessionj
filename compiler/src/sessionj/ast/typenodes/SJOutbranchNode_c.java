package sessionj.ast.typenodes;

import java.util.*;

import polyglot.util.Position;

import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

public class SJOutbranchNode_c extends SJBranchNode_c implements SJOutbranchNode
{
	public SJOutbranchNode_c(Position pos, List<SJBranchCaseNode> branchCases)
	{
		super(pos, branchCases);
	}
	
	public SJOutbranchNode branchCases(List<SJBranchCaseNode> branchCases)
	{
		return (SJOutbranchNode) super.branchCases(branchCases); 
	}
	
	public String nodeToString()
	{
		String s = SJ_STRING_OUTBRANCH_OPEN;

		for (Iterator<SJBranchCaseNode> i = branchCases().iterator(); i.hasNext(); )
		{
			SJBranchCaseNode branchCase = i.next();
			SJLabel lab = branchCase.label();
			SJTypeNode body = branchCase.body();

			s += lab + SJ_STRING_LABEL;
			s += (body == null) ? " " : body.toString();

			if (i.hasNext()) 
			{
				s += SJ_STRING_CASE_SEPARATOR + " ";				
			}
		}

		return s + SJ_STRING_OUTBRANCH_CLOSE;
	}
}
