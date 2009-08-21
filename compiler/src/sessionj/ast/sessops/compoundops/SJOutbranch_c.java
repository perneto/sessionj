package sessionj.ast.sessops.compoundops;

import polyglot.ast.Block_c;
import polyglot.ast.Receiver;
import polyglot.util.Position;
import sessionj.util.SJLabel;

import java.util.List;

public class SJOutbranch_c extends Block_c implements SJOutbranch
{
	private List targets; // Duplicated from SJBasicOperation_c.
	
	private SJLabel lab;

	public SJOutbranch_c(Position pos, List statements, SJLabel lab, List<Receiver> targets)
	{
		super(pos, statements);

		this.lab = lab;
		this.targets = targets;
	}

	public SJLabel label()
	{
		return lab;
	}

	public List targets()
	{
		return targets; 
	}
	
	public SJOutbranch targets(List targets)
	{
		this.targets = targets;
		
		return this;
	}	
	
	// Should set entry point to the socket operation for flow graph building. This is common for all structural operations.
}
