package sessionj.ast.typenodes;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJCBeginNode_c extends SJBeginNode_c implements SJCBeginNode
{
	public SJCBeginNode_c(Position pos)
	{
		super(pos);
	}

	// Duplicated from corresponding SJSessionType implementations. But may be needed before types have been built.
	public String nodeToString()
	{
		return SJ_STRING_CBEGIN;
	}
}
