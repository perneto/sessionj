package sessionj.ast.typenodes;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJInwhileNode_c extends SJLoopNode_c implements SJInwhileNode
{
	public SJInwhileNode_c(Position pos, SJTypeNode body)
	{
		super(pos, body);
	}

	public String nodeToString()
	{
		String m = SJ_STRING_INWHILE_OPEN;
		
		if (body() != null)
		{
			m += body().toString();
		}
		
		return m + SJ_STRING_INWHILE_CLOSE;
	}
}
