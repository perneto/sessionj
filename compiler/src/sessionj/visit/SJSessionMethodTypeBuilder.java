/**
 * 
 */
package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.ast.createops.*;
import sessionj.ast.protocoldecls.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.sesscasts.SJAmbiguousCast;
import sessionj.ast.sesscasts.SJSessionTypeCast;
import sessionj.ast.sessformals.SJFormal;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.typenodes.SJProtocolNode;
import sessionj.ast.typenodes.SJProtocolRefNode;
import sessionj.ast.typenodes.SJTypeNode;
import sessionj.extension.*;
import sessionj.extension.noalias.*;
import sessionj.types.*;
import sessionj.types.contexts.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;
import sessionj.types.noalias.*;
import sessionj.util.noalias.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * @author Raymond
 * 
 * Currently, only session method parameters are supported. Simply records the session parameter information.
 *  
 */
public class SJSessionMethodTypeBuilder extends ContextVisitor
{	
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	private SJExtFactory sjef = sjnf.extFactory();
	
	/**
	 * 
	 */
	public SJSessionMethodTypeBuilder(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException
	{		
		return this;
	}
	
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{				
		if (n instanceof ProcedureDecl)
		{
			n = buildProcedureDecl((ProcedureDecl) n);
		}
		
		return n;
	}
	
	private ProcedureDecl buildProcedureDecl(ProcedureDecl pd) throws SemanticException
	{
		List formals = pd.formals();
		List<Type> sft = new LinkedList<Type>();
			
		for (Iterator i = formals.iterator(); i.hasNext(); )
		{
			Formal f = (Formal) i.next();
			Type t = f.type().type();
						
			if (f instanceof SJFormal) // Could check here for channel parameters declared using protocol references, but it's a bit late.
			{
				SJTypeNode tn = ((SJFormal) f).sessionType();
				SJSessionType st = tn.type();
				
				if (tn instanceof SJProtocolNode && st instanceof SJCBeginType) // Currently, they are incorrectly parsed as SJSocket types. However, if a (otherwise correct) call is actually made to this method, this check is too late - base type checking will have failed. 
				{
					throw new SemanticException("[SJMethodTypeBuilder] Protocol reference for channel type parameters not yet supported: " + f);
				}
				
				sft.add(st);
			}
			else
			{
				sft.add(t);
			}
		}
		
		// FIXME: session method return types not supported yet.
		
		((SJProcedureInstance) pd.procedureInstance()).setSessionFormalTypes(sft); // SJNoAliasTypeBuilder has already converted the procedure instance object to a SJProcedureInstance (with noalias information).
		
		return pd;
	}
}
