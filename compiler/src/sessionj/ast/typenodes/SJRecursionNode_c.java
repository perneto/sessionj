package sessionj.ast.typenodes;

import polyglot.util.Position;

import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

public class SJRecursionNode_c extends SJLoopNode_c implements SJRecursionNode
{
	private SJLabel lab;

	public SJRecursionNode_c(Position pos, SJLabel lab, SJTypeNode body)
	{
		super(pos, body);

		this.lab = lab;
	}

	public SJLabel label()
	{
		return lab;
	}

	public String nodeToString()
	{
		String m = SJ_STRING_REC + " " + label() + SJ_STRING_RECURSION_OPEN;
		
		if (body() != null)
		{
			m += body().toString();
		}
		
		return m + SJ_STRING_RECURSION_CLOSE;
	}	
}
