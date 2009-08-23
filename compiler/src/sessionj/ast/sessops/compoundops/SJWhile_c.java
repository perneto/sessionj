package sessionj.ast.sessops.compoundops;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.While_c;
import polyglot.util.Position;

import java.util.List;

public class SJWhile_c extends While_c implements SJWhile
{
	private List targets; // Duplicated from SJBasicOperation_c.
	
	public SJWhile_c(Position pos, Expr cond, Stmt body, List targets)
	{
		super(pos, cond, body);
		
		this.targets = targets;
	}

	public SJWhile cond(Expr cond)
	{
		return (SJWhile) super.cond(cond);
	}

	public SJWhile body(Stmt body)
	{
		return (SJWhile) super.body(body);
	}

	public List targets()
	{
		return targets; 
	}
	
	public SJWhile_c targets(List targets)
	{
		this.targets = targets;
		
		return this;
	}		
}
