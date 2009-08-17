package sessionj.ast.sessformals;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import sessionj.ast.protocoldecls.SJProtocolDecl;
import sessionj.ast.typenodes.SJTypeNode;

public class SJChannelFormal_c extends SJFormal_c implements SJChannelFormal // extends SJNoaliasFormal_c
{
	public SJChannelFormal_c(Position pos, Flags flags, TypeNode typeNode, Id name, SJTypeNode tn)
	{
		super(pos, flags, typeNode, name, tn);
	}
	
	public SJChannelFormal sessionType(SJTypeNode tn)
	{
		return (SJChannelFormal) super.sessionType(tn);
	}
}
