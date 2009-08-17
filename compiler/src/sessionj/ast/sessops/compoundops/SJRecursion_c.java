package sessionj.ast.sessops.compoundops;

import java.util.Collections;
import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

import sessionj.util.SJLabel;

public class SJRecursion_c extends For_c implements SJRecursion
{
	private List targets; // Duplicated from SJBasicOperation_c.	
	
	private SJLabel lab;

	public SJRecursion_c(Position pos, List inits, Expr cond, Block body, SJLabel lab, List targets)
	{
		super(pos, inits, cond, Collections.EMPTY_LIST, body);

		this.targets = targets;
		this.lab = lab;
	}

	public SJLabel label()
	{
		return lab;
	}

	public SJRecursion label(SJLabel lab)
	{
		this.lab = lab;

		return this;
	}
		
	public List targets()
	{
		return targets; 
	}
	
	public SJRecursion targets(List targets)
	{
		this.targets = targets;
		
		return this;
	}		
	
	public Block body()
	{
		return (Block) super.body();
	}
}
