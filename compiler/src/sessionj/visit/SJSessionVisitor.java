/**
 * 
 */
package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.ast.createops.*;
import sessionj.ast.protocoldecls.*;
import sessionj.ast.servops.*;
import sessionj.ast.sesstry.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.chanops.*;
import sessionj.ast.sesscasts.SJChannelCast;
import sessionj.ast.sesscasts.SJSessionCast;
import sessionj.ast.sesscasts.SJSessionTypeCast;
import sessionj.ast.sessformals.SJSessionFormal;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.typenodes.SJTypeNode;
import sessionj.extension.*;
import sessionj.extension.noalias.*;
import sessionj.extension.sessops.SJSessionOperationExt;
import sessionj.types.*;
import sessionj.types.contexts.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;
import sessionj.types.noalias.*;
import sessionj.util.SJLabel;
import sessionj.util.noalias.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * @author Raymond
 *
 * Post session type checking visitor which reads the session type information build and recorded by the preceding passes.
 *
 */
public class SJSessionVisitor extends SJAbstractSessionVisitor  
{	
	//private static boolean debug = true;
	private static boolean debug = false;
		
	/**
	 * 
	 */
	public SJSessionVisitor(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected NodeVisitor sjEnterCall(Node parent, Node n) throws SemanticException // FIXME: make abstract.
	{
		return this;
	}
	
	protected Node sjLeaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{		
		if (n instanceof SJSessionOperation && !(n instanceof SJInternalOperation))
		{
			SJSessionOperation so = (SJSessionOperation) n;			
			List<String> sjnames = getSJSessionOperationExt(so).sjnames();
			SJSessionType st = getSessionType(so);
			
			if (debug)
			{
				System.out.println("\n" + sjnames + ": " + st + " (" + so + ")\n");
			}
		}
		else if (n instanceof SJSpawn)
		{
			SJSpawn s = (SJSpawn) n;
			
			if (debug)
			{
				System.out.println("\n" + s.sjnames() + ": " + s.sessionTypes() + " (" + s + ")\n");
			}
		}
		else if (n instanceof SJChannelOperation)
		{
			SJChannelOperation co = (SJChannelOperation) n;			
			String sjname = getSJNamedExt(co).sjname();
			SJSessionType st = getSessionType(co);
			
			if (debug)
			{
				System.out.println("\n" + sjname + ": " + st + " (" + co + ")\n");
			}
		}
		else if (n instanceof SJServerOperation)
		{
			SJServerOperation so = (SJServerOperation) n;			
			SJSessionType st = getSessionType(so);
			
			if (debug)
			{
				System.out.println("\n" + so + ": " + st + "\n");
			}
		}
			
		
		return n;
	}	
}
