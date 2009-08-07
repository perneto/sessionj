package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

import sessionj.ast.*;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sessvars.*;
import sessionj.extension.*;
import sessionj.types.typeobjects.SJLocalSocketInstance;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * 
 * @author Raymond
 *
 * Also parses SJSpawns (which aren't session operations - "multiply" typed and can have channel targets.
 * 
 */
public class SJSessionOperationParser extends ContextVisitor // Doesn't do any "proper" parsing of session operations (done by actual parser), but does some argument adjusting. // Except SJSpawns.
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
	
	static 
	{
		/*SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_SEND);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_PASS);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_COPY);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_RECEIVE);
		SJ_BASIC_OPERATION_KEYWORDS.add(SJ_KEYWORD_RECEIVEINT);*/
	}
	
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
		
		for (Iterator i = s.targets().iterator(); i.hasNext(); )
		{
			args.add((SJVariable) i.next());
		}
		
		s = (SJSpawn) s.arguments(args);		
		s = (SJSpawn) buildAndCheckTypes(job(), this, s);
		
		return s;
	}
	
	private Call parseCall(Call c) throws SemanticException
	{
		Receiver target = c.target();
		
		if (target instanceof SJSocketVariable)
		{
			String name = c.name();
			
			if (SJ_BASIC_OPERATION_KEYWORDS.contains(name)) // Currently empty.
			{
				SJBasicOperation bo = null;
				
				List<Expr> args = new LinkedList<Expr>();
				List<SJSocketVariable> targets = new LinkedList<SJSocketVariable>();
				
				targets.add((SJSocketVariable) target);
			
				/*if (name.equals(SJ_KEYWORD_SEND)) // Done by parser.
				{
					bo = sjnf.SJSend(c.position(), args, targets); // Maybe should instead change the factory method to create the proper NewArray argument directly.
				}
				else if (name.equals(SJ_KEYWORD_PASS)) 
				{
					bo = sjnf.SJPass(c.position(), args, targets); 
				}
				else if (name.equals(SJ_KEYWORD_COPY)) 
				{
					bo = sjnf.SJCopy(c.position(), args, targets); 
				}
				else*/ /*if (name.equals(SJ_KEYWORD_RECEIVE)) // Maybe SJReceive should be implemented more like a regular Call, rather than like e.g. a SJSend. // Now parsed by parser.
				{
					bo = sjnf.SJReceive(c.position(), args, targets); // Maybe should instead change the factory method to create the proper NewArray argument directly.
				}*/
				
				bo = (SJBasicOperation) fixSJBasicOperationArguments(bo);
				bo = (SJBasicOperation) buildAndCheckTypes(job(), this, bo);
				
				c = bo;
			}
			else if (!RUNTIME_SOCKET_OPERATIONS.contains(name))// FIXME: should allow for socket parameter setter methods, etc.
			{
				throw new SemanticException("[SJSessionOperationParser] Unknown session operation: " + c);
			}
		}
		
		return c;
	}
	
	private SJBasicOperation fixSJBasicOperationArguments(SJBasicOperation bo) throws SemanticException
	{
		List targets = bo.targets(); // Already type built and checked by SJVariableParser.		
		
		List<LocalInstance> seen = new LinkedList<LocalInstance>(); 
		
		for (Iterator i = targets.iterator(); i.hasNext(); )
		{
			Receiver r = (Receiver) i.next();
			
			if (!(r instanceof SJSocketVariable))
			{
				throw new SemanticException("[SJSessionOperationParser] Expected session socket target, not: " + r);
			}
			
			LocalInstance li = (LocalInstance) ((SJLocalSocket) r).localInstance(); // SJLocalSocketInstance not built until SJSessionTypeChecker.
			
			if (seen.contains(li))
			{
				throw new SemanticException("[SJSessionOperationParser] Repeated session target: " + li);				
			}
			
			seen.add(li);
		}
		
		List orig = bo.arguments();		
		NewArray na = (NewArray) orig.get(0); // Factor out constant?
		
		ArrayInit ai = sjnf.ArrayInit(bo.position(), targets);
		ai = (ArrayInit) buildAndCheckTypes(job(), this, ai); 
		
		na = na.init(ai);
		na = na.dims(Collections.EMPTY_LIST); 
		na = na.additionalDims(1); // Factor out constant?
		
		List<Expr> args = new LinkedList<Expr>(); 
		
		args.add(na);
		args.addAll(orig);
		args.remove(1); // Factor out constant?
		
		bo = (SJBasicOperation) bo.arguments(args);
		
		return bo;
	}
}
