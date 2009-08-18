package sessionj.ast.typenodes;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJOutwhileNode_c extends SJLoopNode_c implements SJOutwhileNode
{
	public SJOutwhileNode_c(Position pos, SJTypeNode body)
	{
		super(pos, body);
	}
	
	public String nodeToString()
	{
		String m = SJ_STRING_OUTWHILE_OPEN;
		
		if (body() != null)
		{
			m += body().toString();
		}
		
		return m + SJ_STRING_OUTWHILE_CLOSE;
	}	
}
