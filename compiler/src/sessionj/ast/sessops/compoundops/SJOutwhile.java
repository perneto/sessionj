package sessionj.ast.sessops.compoundops;

import polyglot.ast.*;

public interface SJOutwhile extends SJWhile
{
	public SJOutwhile cond(Expr cond);	
	public SJOutwhile body(Stmt body);
}
