package sessionj.ast.typenodes;

import polyglot.ast.Receiver;
import polyglot.util.Position;

import static sessionj.SJConstants.*;

abstract public class SJProtocolNode_c extends SJTypeNode_c implements SJProtocolNode
{
	private Receiver target;

	public SJProtocolNode_c(Position pos, Receiver target)
	{
		super(pos);

		this.target = target;
	}

	public Receiver target()
	{
		return target;
	}

	public SJProtocolNode_c target(Receiver target)
	{
		this.target = target;

		return this;
	}
}
