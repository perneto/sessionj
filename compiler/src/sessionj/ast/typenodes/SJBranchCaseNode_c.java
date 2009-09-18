package sessionj.ast.typenodes;

import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import static sessionj.SJConstants.SJ_STRING_LABEL;
import sessionj.util.SJLabel;
import sessionj.util.SJCompilerUtils;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.types.SJTypeSystem;

public class SJBranchCaseNode_c extends SJTypeNode_c implements SJBranchCaseNode
{
	private SJLabel lab;
	private SJTypeNode body;

	public SJBranchCaseNode_c(Position pos, SJLabel lab, SJTypeNode body)
	{
		super(pos);

		this.lab = lab;
		this.body = body;
	}

	public SJLabel label()
	{
		return lab;
	}

	public SJTypeNode body()
	{
		return body;
	}

	public SJTypeNode disambiguateSJTypeNode(Job job, ContextVisitor cv, SJTypeSystem ts) throws SemanticException {
        SJSessionType st = null;
        if (body != null) {
            st = SJCompilerUtils.disambiguateSJTypeNode(job, cv, body).type();
        }
        return type(st);
    }

    public String nodeToString()
	{
		return label() + SJ_STRING_LABEL + ' ' + body();
	}
}
