package sessionj.ast.sessformals;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import sessionj.ast.typenodes.SJTypeNode;

/**
 * 
 * @author Raymond
 * @deprecated
 *
 */
public class SJNoaliasFormal_c extends SJFormal_c implements SJNoaliasFormal
{
	public SJNoaliasFormal_c(Position pos, Flags flags, TypeNode typeNode, Id name, SJTypeNode tn)
	{
		super(pos, flags, typeNode, name, tn);
	}
}
