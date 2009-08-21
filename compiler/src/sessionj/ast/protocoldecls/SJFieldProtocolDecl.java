package sessionj.ast.protocoldecls;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import sessionj.ast.typenodes.SJTypeNode;

public interface SJFieldProtocolDecl extends FieldDecl, SJProtocolDecl
{
	public SJFieldProtocolDecl init(Expr init);
	
	public SJFieldProtocolDecl sessionType(SJTypeNode tn);
}
