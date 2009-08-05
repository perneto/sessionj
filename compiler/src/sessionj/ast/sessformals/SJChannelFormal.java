package sessionj.ast.sessformals;

import polyglot.ast.Formal;

import sessionj.ast.SJNamed;
import sessionj.ast.protocoldecls.SJProtocolDecl;
import sessionj.ast.typenodes.SJTypeNode;

public interface SJChannelFormal extends SJFormal // extends SJNoaliasFormal
{
	public SJChannelFormal sessionType(SJTypeNode tn);
}
