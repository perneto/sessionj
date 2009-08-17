package sessionj.ast.sessops.basicops;

import polyglot.ast.Expr;
import polyglot.util.Position;
import sessionj.ast.SJNodeFactory;

import java.util.List;

public class SJReceive_c extends SJBasicOperation_c implements SJReceive
{	
	public SJReceive_c(Position pos, SJNodeFactory nf, String name, List<Expr> arguments, List targets)
	{
		super(pos, nf, name, arguments, targets);
	}
}
