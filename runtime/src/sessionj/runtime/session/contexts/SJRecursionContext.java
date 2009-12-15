package sessionj.runtime.session.contexts;

import sessionj.types.sesstypes.SJRecursionType;
import sessionj.util.SJLabel;

public class SJRecursionContext extends SJLoopContext // Analogous to the compiler contexts.
{
	private final SJLabel lab;

	private final SJRecursionType original;

	public SJRecursionContext(SJRecursionType rt)
	{
		super(rt.body());

        lab = rt.label();
        original = rt;
	}
	
	public SJLabel label()
	{
		return lab;
	}

	public SJRecursionType originalType()
	{
		return original;
	}

    public boolean hasLabel(SJLabel lab) {
        return lab.equals(this.lab);
    }
}
