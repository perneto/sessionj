package sessionj.ast.typenodes;

import sessionj.util.SJLabel;

public interface SJBranchCaseNode extends SJTypeNode
{
	public SJLabel label();

	public SJTypeNode body();
	public SJBranchCaseNode body(SJTypeNode body);
}
