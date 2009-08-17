package sessionj.ast.sessops.compoundops;

import polyglot.ast.*;

import sessionj.util.SJLabel;

public interface SJRecursion extends For, SJLoopOperation // But does not need a SJSocketOperation.
{
	public SJLabel label();
	public SJRecursion label(SJLabel lab);
	
	public Block body();
}
