package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJRecursionExit_c extends SJBasicOperation_c implements SJRecursionExit  
{	
	public SJRecursionExit_c(Position pos, Receiver target, Id name, List arguments, List targets)
	{
		super(pos, target, name, arguments, targets);
	}
}
