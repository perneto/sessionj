package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;
import sessionj.util.SJLabel;

public class SJRecurse_c extends SJBasicOperation_c implements SJRecurse 
{	
	private SJLabel lab;
	
	public SJRecurse_c(Position pos, Receiver target, Id name, List arguments, List targets, SJLabel lab)
	{
		super(pos, target, name, arguments, targets);
		
		this.lab = lab;
	}
	
	public SJLabel label()
	{
		return lab;
	}
}
