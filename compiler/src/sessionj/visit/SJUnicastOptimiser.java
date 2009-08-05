package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.*;
import sessionj.util.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * 
 * @author Raymond
 *
 * Currently must be run before SJCompoundOperationTranslator (see ExtensionInfo).
 *
 */
public class SJUnicastOptimiser extends ContextVisitor
{
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();

	private SJTypeEncoder sjte = new SJTypeEncoder(sjts);

	public SJUnicastOptimiser(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof SJSessionOperation) 
		{
			if (n instanceof SJBasicOperation)
			{
				n = translateBasicOperation((SJBasicOperation) n);
			}		
		}		

		return n;
	}
	
	private SJBasicOperation translateBasicOperation(SJBasicOperation n)
	{
		List args = n.arguments();

		List sockets = ((NewArray) n.arguments().get(0)).init().elements();
		
		if (sockets.size() == 1)
		{
			List newargs = new LinkedList();
			
			newargs.add(sockets.get(0));
			
			for (int i = 1; i < args.size(); i++)
			{
				newargs.add(args.get(i));
			}
			
			n = (SJBasicOperation) n.arguments(newargs);
		}
		
		return n;
	}
}
