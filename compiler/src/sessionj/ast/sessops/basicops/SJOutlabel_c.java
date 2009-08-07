package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJOutlabel_c extends SJBasicOperation_c implements SJOutlabel  
{	
	public SJOutlabel_c(Position pos, Receiver target, Id name, List arguments, List targets)
	{
		super(pos, target, name, arguments, targets);
	}
}
