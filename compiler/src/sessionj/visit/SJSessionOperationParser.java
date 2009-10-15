package sessionj.visit;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import sessionj.ast.SJNodeFactory;
import sessionj.ast.SJSpawn;
import sessionj.ast.sessops.basicops.SJBasicOperation;
import sessionj.ast.sessvars.SJLocalSocket;
import sessionj.ast.sessvars.SJSocketVariable;
import sessionj.ast.sessvars.SJVariable;
import static sessionj.util.SJCompilerUtils.buildAndCheckTypes;

import java.util.*;

/**
 * 
 * @author Raymond
 *
 * Also parses SJSpawns (which aren't session operations - "multiply" typed and can have channel targets.
 * 
 */
public class SJSessionOperationParser extends ContextVisitor
// Doesn't do any "proper" parsing of session operations (done by actual parser), but does some argument adjusting. // Except SJSpawns.
{
	public static final Set<String> RUNTIME_SOCKET_OPERATIONS = new HashSet<String>(); // Factor out as constants.
	
	{
		RUNTIME_SOCKET_OPERATIONS.add("getProtocol"); // Just the getters.
		RUNTIME_SOCKET_OPERATIONS.add("getHostName");
		RUNTIME_SOCKET_OPERATIONS.add("getPort");
		RUNTIME_SOCKET_OPERATIONS.add("getLocalHostName");
		RUNTIME_SOCKET_OPERATIONS.add("getLocalPort");
		RUNTIME_SOCKET_OPERATIONS.add("getParameters");
	}
	
	private static final Set<String> SJ_BASIC_OPERATION_KEYWORDS = new HashSet<String>();
	
/*	static
	{
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_SEND);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_PASS);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_COPY);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_RECEIVE);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_RECEIVEINT);
	}
*/
	
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	//private SJExtFactory sjef = ((SJNodeFactory) nodeFactory()).extFactory();

	public SJSessionOperationParser(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);				
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{		
		if (n instanceof Call)
		{
			if (n instanceof SJBasicOperation) // SJRequest or SJSend.
			{	
				n = parseSJBasicOperation((SJBasicOperation) n);
			}
			else if (n instanceof SJSpawn) // FIXME: needs to be done in a later pass to make sure the SJThreads have already been parsed.
			{
				n = parseSJSpawn((SJSpawn) n);		
			}
			else
			{				
				n = parseCall((Call) n);
			}
		}
		/*else if (n instanceof SJCompoundOperation) // Not needed, compound operation targets already type build and checked by SJVariableParser.
		{
			n = parseSJCompoundOperation((SJCompoundOperation) n);
		}*/

		return n;
	}

	private Call parseSJBasicOperation(SJBasicOperation bo) throws SemanticException
	{
		bo = fixSJBasicOperationArguments(bo);
		
		return bo;
	} 
	
	private SJSpawn parseSJSpawn(SJSpawn s) throws SemanticException
	{
		List<SJVariable> args = new LinkedList<SJVariable>();

        for (Object o : s.targets()) {
            args.add((SJVariable) o);
        }
		
		s = (SJSpawn) s.arguments(args);		
		s = (SJSpawn) buildAndCheckTypes(this, s);
		
		return s;
	}
	
	private Call parseCall(Call c) throws SemanticException
	{
		Receiver target = c.target();
		
		if (target instanceof SJSocketVariable)
		{
			String name = c.name();
			/*
			if (SJ_BASIC_OPERATION_KEYWORDS.contains(name)) // Currently empty.
			{
				SJBasicOperation bo = null;

				List<Expr> args = new LinkedList<Expr>();
				List<SJSocketVariable> targets = new LinkedList<SJSocketVariable>();
				
				targets.add((SJSocketVariable) target);

				bo = fixSJBasicOperationArguments(bo);
				bo = (SJBasicOperation) buildAndCheckTypes(job(), this, bo);
				
				c = bo;
			}
			else */
            if (!RUNTIME_SOCKET_OPERATIONS.contains(name))// FIXME: should allow for socket parameter setter methods, etc.
			{
				throw new SemanticException("[SJSessionOperationParser] Unknown session operation: " + c);
			}
		}
		
		return c;
	}
	
	private SJBasicOperation fixSJBasicOperationArguments(final SJBasicOperation bo) throws SemanticException
	{
		List targets = bo.targets(); // Already type built and checked by SJVariableParser.
		
		List<LocalInstance> seen = new LinkedList<LocalInstance>();

        for (Object target : targets) {
            Receiver r = (Receiver) target;

            if (!(r instanceof SJSocketVariable)) {
                throw new SemanticException("[SJSessionOperationParser] Expected session socket target, not: " + r);
            }

            LocalInstance li = ((SJLocalSocket) r).localInstance(); // SJLocalSocketInstance not built until SJSessionTypeChecker.

            if (seen.contains(li)) {
                throw new SemanticException("[SJSessionOperationParser] Repeated session target: " + li);
            }

            seen.add(li);
        }
        ArrayInit ai = sjnf.ArrayInit(bo.position(), targets);
        ai = (ArrayInit) buildAndCheckTypes(this, ai);
        final NewArray na = bo.dummyArray()
                .init(ai)
                .dims(Collections.emptyList())
                .additionalDims(1);

        List<Expr> newArgs = new LinkedList<Expr>() {{
            addAll(bo.realArgs());
            add(na);
        }};

		return (SJBasicOperation) bo.arguments(newArgs);		
	}
}
