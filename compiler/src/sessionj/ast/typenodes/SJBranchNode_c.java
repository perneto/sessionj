package sessionj.ast.typenodes;

import java.util.List;
import java.util.LinkedList;

import polyglot.util.Position;
import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import polyglot.types.SemanticException;

import static sessionj.SJConstants.*;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.SJBranchType;
import sessionj.util.SJCompilerUtils;

abstract public class SJBranchNode_c extends SJTypeNode_c implements SJBranchNode
{
	private List<SJBranchCaseNode> branchCases;

	public SJBranchNode_c(Position pos, List<SJBranchCaseNode> branchCases)
	{
		super(pos);

		this.branchCases = branchCases;
	}

	protected List<SJBranchCaseNode> branchCases()
	{
		return branchCases;
	}

	protected SJBranchNode branchCases(List<SJBranchCaseNode> branchCases)
	{
		this.branchCases = branchCases;

		return this;
	}

    public SJTypeNode disambiguateSJTypeNode(Job job, ContextVisitor cv, SJTypeSystem sjts) throws SemanticException {

        SJBranchType bt = createType(sjts);

        List<SJBranchCaseNode> branchCases = new LinkedList<SJBranchCaseNode>();

        for (SJBranchCaseNode bcn : branchCases()) {
            bcn = (SJBranchCaseNode) SJCompilerUtils.disambiguateSJTypeNode(job, cv, bcn);

            branchCases.add(bcn);

            bt = bt.branchCase(bcn.label(), bcn.type());
        }

        return (SJTypeNode) branchCases(branchCases).type(bt);
    }

    protected abstract SJBranchType createType(SJTypeSystem sjts);

}
