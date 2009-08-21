package sessionj.ast.typenodes;

import polyglot.util.Position;

import static sessionj.SJConstants.*;

abstract public class SJLoopNode_c extends SJTypeNode_c implements SJLoopNode
{
	private SJTypeNode body;

	public SJLoopNode_c(Position pos, SJTypeNode body)
	{
		super(pos);

		this.body = body;
	}

	public SJTypeNode body()
	{
		return body;
	}

	public SJLoopNode body(SJTypeNode body)
	{
		this.body = body;

		return this;
	}
}
