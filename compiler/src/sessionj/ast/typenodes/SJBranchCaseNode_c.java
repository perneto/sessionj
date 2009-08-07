package sessionj.ast.typenodes;

import polyglot.util.Position;

import sessionj.types.sesstypes.SJSessionType;
import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

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

	public SJBranchCaseNode body(SJTypeNode body)
	{
		this.body = body;

		return this;
	}

	/*public SJSessionType type() // SJBranchCaseNode is the only SJTypeNode that can null type, and does not have children.
	{
		SJSessionType st = (SJSessionType) super.type(); // This routine doesn't work because we can't access the super.super method.
		
		if (st != null)
		{
			st = st.copy();  		
		}

		return st;
	}*/
	
	public String nodeToString()
	{
		return label() + SJ_STRING_LABEL + " " + body();
	}
}
