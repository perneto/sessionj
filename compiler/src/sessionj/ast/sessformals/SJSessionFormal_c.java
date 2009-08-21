package sessionj.ast.sessformals;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import sessionj.ast.protocoldecls.SJProtocolDecl;
import sessionj.ast.typenodes.SJTypeNode;

public class SJSessionFormal_c extends SJFormal_c implements SJSessionFormal // extends SJNoaliasFormal_c
{
	public SJSessionFormal_c(Position pos, Flags flags, TypeNode typeNode, Id name, SJTypeNode tn)
	{
		super(pos, flags, typeNode, name, tn);
	}
	
	public SJSessionFormal sessionType(SJTypeNode tn)
	{
		return (SJSessionFormal) super.sessionType(tn);
	}
}
