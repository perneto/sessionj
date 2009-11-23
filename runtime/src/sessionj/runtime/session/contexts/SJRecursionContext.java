package sessionj.runtime.session.contexts;

import sessionj.types.sesstypes.SJRecursionType;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.util.SJLabel;

public class SJRecursionContext extends SJLoopContext // Analogous to the compiler contexts.
{
	private SJLabel lab;

	private SJRecursionType original;
	
	/*public SJRecursionContext(SJLabel lab, SJSessionType active)
	{
		super(active);

		System.out.println("d: " + active);
		
		this.lab = lab;
	}*/

	public SJRecursionContext(SJRecursionType rt)
	{
		super(rt.body());

		this.lab = rt.label();
		this.original = rt;
	}
	
	public SJLabel label()
	{
		return lab;
	}

	public SJRecursionType originalType()
	{
		//return original;
		return (SJRecursionType) original.nodeClone(); // Needed?
	}
	
	/*public final boolean isRecursionContext()
	{
		return true;
	}*/
}
