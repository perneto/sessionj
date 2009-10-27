package sessionj.runtime.session.contexts;

import sessionj.types.sesstypes.SJSessionType;
import sessionj.util.SJLabel;

public class SJRecursionContext extends SJLoopContext // Analogous to the compiler contexts.
{
	private SJLabel lab;

	public SJRecursionContext(SJLabel lab, SJSessionType active)
	{
		super(active);

		this.lab = lab;
	}

	public SJLabel label()
	{
		return lab;
	}

	/*public final boolean isRecursionContext()
	{
		return true;
	}*/
}
