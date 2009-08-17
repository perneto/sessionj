package sessionj.ast.sessops.compoundops;

import java.util.List;

import polyglot.ast.*;

import sessionj.ast.sessops.basicops.SJInlabel;

public interface SJInbranch extends CompoundStmt, SJBranchOperation
{
	public List<SJInbranchCase> branchCases();
	public SJInbranch branchCases(List<SJInbranchCase> branchCases);
	
	public SJInlabel inlabel();
	public SJInbranch inlabel(SJInlabel il);
}
