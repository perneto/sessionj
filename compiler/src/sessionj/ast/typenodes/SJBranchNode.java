package sessionj.ast.typenodes;

import java.util.List;

public interface SJBranchNode extends SJTypeNode
{
	public List<SJBranchCaseNode> branchCases();
	public SJBranchNode branchCases(List<SJBranchCaseNode> branchCases);
}
