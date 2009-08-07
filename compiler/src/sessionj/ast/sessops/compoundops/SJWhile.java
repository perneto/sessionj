package sessionj.ast.sessops.compoundops;

import polyglot.ast.*;

public interface SJWhile extends While, SJLoopOperation
{
	public SJWhile cond(Expr cond);	
	public SJWhile body(Stmt body);
}
