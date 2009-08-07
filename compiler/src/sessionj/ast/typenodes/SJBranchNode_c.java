package sessionj.ast.typenodes;

import java.util.List;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

abstract public class SJBranchNode_c extends SJTypeNode_c implements SJBranchNode
{
	private List<SJBranchCaseNode> branchCases;

	public SJBranchNode_c(Position pos, List<SJBranchCaseNode> branchCases)
	{
		super(pos);

		this.branchCases = branchCases;
	}

	public List<SJBranchCaseNode> branchCases()
	{
		return branchCases;
	}

	public SJBranchNode branchCases(List<SJBranchCaseNode> branchCases)
	{
		this.branchCases = branchCases;

		return this;
	}
}
