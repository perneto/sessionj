package sessionj.ast.sessformals;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import sessionj.ast.protocoldecls.SJProtocolDecl;
import sessionj.ast.typenodes.SJTypeNode;

abstract public class SJFormal_c extends Formal_c implements SJFormal // Abstract for now, since don't think this class will ever be needed directly.
{
	private SJTypeNode tn; 
	
	public SJFormal_c(Position pos, Flags flags, TypeNode typeNode, Id name, SJTypeNode tn)
	{
		super(pos, flags, typeNode, name);
		
		this.tn = tn;
	}
	
	public SJTypeNode sessionType()
	{
		return tn;
	}
	
	public SJFormal sessionType(SJTypeNode tn)
	{
		this.tn = tn;
		
		return this;
	}
}
