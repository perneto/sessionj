package sessionj.ast.sessops.compoundops;

import polyglot.ast.*;

import sessionj.ast.sessops.*;

/**
 * 
 * @author Raymond
 *
 * Currently not working alongside While. Strange because Eclipse allows it, but manual javac doesn't.
 * 
 * Confusingly corresponds to SJSessionLoopContext, not SJLoopContext. 
 *
 */
public interface SJLoopOperation extends SJCompoundOperation
{
	/*public SJLoopOperation cond(Expr cond);	
	public SJLoopOperation body(Stmt body);*/
}
