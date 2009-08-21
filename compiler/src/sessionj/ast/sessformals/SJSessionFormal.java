package sessionj.ast.sessformals;

import polyglot.ast.Formal;

import sessionj.ast.SJNamed;
import sessionj.ast.protocoldecls.SJProtocolDecl;
import sessionj.ast.typenodes.SJTypeNode;

public interface SJSessionFormal extends SJFormal // extends SJNoaliasFormal
{
	public SJSessionFormal sessionType(SJTypeNode tn);
}
