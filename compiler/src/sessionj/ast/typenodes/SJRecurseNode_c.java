package sessionj.ast.typenodes;

import polyglot.util.Position;

import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

public class SJRecurseNode_c extends SJTypeNode_c implements SJRecurseNode
{
	private SJLabel lab;

	public SJRecurseNode_c(Position pos, SJLabel lab)
	{
		super(pos);

		this.lab = lab;
	}

	public SJLabel label()
	{
		return lab;
	}
	
	public String nodeToString()
	{
		return SJ_STRING_RECURSE_PREFIX + label();
	}	
}
