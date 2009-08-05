package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

import sessionj.ast.sessvars.SJSocketVariable;

abstract public class SJBasicOperation_c extends Call_c implements SJBasicOperation
{	
	private List targets; // Could override the relevant methods to perform type building and type checking (disambiguation not necessary?) via the base passes, but currently it is done manually in the "session operation parsing" pass.
	
	public SJBasicOperation_c(Position pos, Receiver target, Id name, List arguments, List targets)
	{
		super(pos, target, name, arguments);
		
		this.targets = targets;
	}
	
	public List targets()
	{
		return targets; 
	}
	
	public SJBasicOperation targets(List targets)
	{
		this.targets = targets;
		
		return this;
	}		
}
