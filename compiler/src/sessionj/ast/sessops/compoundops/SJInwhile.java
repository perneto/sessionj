package sessionj.ast.sessops.compoundops;

import polyglot.ast.*;

public interface SJInwhile extends SJWhile
{
	public SJInwhile cond(Expr cond);	
	public SJInwhile body(Stmt body);
}
