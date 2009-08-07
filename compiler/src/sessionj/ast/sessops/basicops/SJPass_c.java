package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJPass_c extends SJBasicOperation_c implements SJPass 
{	
	public SJPass_c(Position pos, Receiver target, Id name, List arguments, List targets)
	{
		super(pos, target, name, arguments, targets);
	}
}
