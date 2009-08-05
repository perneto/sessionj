package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.qq.*;
import polyglot.util.*;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.ast.sessvars.SJSocketVariable;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.extension.SJExtFactory;
import sessionj.extension.noalias.SJNoAliasExprExt;
import sessionj.extension.sessops.SJSessionOperationExt;
import sessionj.extension.sesstypes.SJTypeableExt;
import sessionj.types.SJTypeSystem;
import sessionj.util.*;

import static sessionj.SJConstants.*; 
import static sessionj.util.SJCompilerUtils.*;

/**
 * 
 * @author Raymond
 *
 * Needs to come before noalias translation, so can tell if arguments are noalias.
 *
 */
public class SJSendTranslator extends ContextVisitor //ErrorHandlingVisitor
{
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	private SJExtFactory sjef = sjnf.extFactory(); 

	public SJSendTranslator(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof SJSend)
		{
			n = translateSJSend((SJSend) n);
		}

		return n;
	}

	private SJPass translateSJSend(SJSend s) throws SemanticException
	{				
		List arguments = s.arguments();
		Expr e = (Expr) arguments.get(1); // Factor out constant.
		
		SJPass p = null;
		
		if (isNoAlias(e) && !e.type().isPrimitive())
		{
			List args = new LinkedList();
			
			args.add(e);
			
			p = sjnf.SJPass(s.position(), args, s.targets());
			
			args = new LinkedList(p.arguments());
			args.remove(0);
			args.add(0, arguments.get(0));
			
			p = (SJPass) p.arguments(args);			
			
			SJNoAliasExprExt naee = getSJNoAliasExprExt(s);
			SJTypeableExt te = getSJTypeableExt(s);
			
			p = (SJPass) buildAndCheckTypes(job(), this, p);
			p = (SJPass) setSJNoAliasExprExt(sjef, p, naee.isNoAlias(), naee.isFinal(), naee.fields(), naee.locals(), naee.arrayAccesses());						
			p = (SJPass) setSJSessionOperationExt(sjef, p, te.sessionType(), ((SJSessionOperationExt) te).sjnames());
		}
		else
		{
			p = s;
		}
		
		return p;
	}	
}
