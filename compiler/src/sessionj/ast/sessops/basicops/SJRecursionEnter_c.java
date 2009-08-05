package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJRecursionEnter_c extends SJBasicOperation_c implements SJRecursionEnter  
{	
	public SJRecursionEnter_c(Position pos, Receiver target, Id name, List arguments, List targets)
	{
		super(pos, target, name, arguments, targets);
	}
}
