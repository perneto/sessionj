package sessionj.ast.typenodes;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJSBeginNode_c extends SJBeginNode_c implements SJSBeginNode
{
	public SJSBeginNode_c(Position pos)
	{
		super(pos);
	}

	// Duplicated from corresponding SJSessionType implementations. But may be needed before types have been built.
	public String nodeToString()
	{
		return SJ_STRING_SBEGIN;
	}
}
