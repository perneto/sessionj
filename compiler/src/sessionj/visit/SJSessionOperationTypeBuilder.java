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
import sessionj.ast.sessvars.*;
import sessionj.ast.sesscasts.SJSessionTypeCast;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.typenodes.SJMessageCommunicationNode;
import sessionj.ast.typenodes.SJProtocolDualNode;
import sessionj.ast.typenodes.SJProtocolNode;
import sessionj.ast.typenodes.SJProtocolRefNode;
import sessionj.ast.typenodes.SJTypeNode;
import sessionj.extension.*;
import sessionj.extension.noalias.*;
import sessionj.types.*;
import sessionj.types.contexts.*;
import sessionj.types.sesstypes.SJCBeginType;
import sessionj.types.sesstypes.SJInbranchType;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.types.typeobjects.*;
import sessionj.types.noalias.*;
import sessionj.util.noalias.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * 
 * @author Raymond
 *
 * Also does casts for receive.
 *
 * Should make a "SJSessionReturn" type for session socket create, session-receive (and casts) and session return type method calls. Similarly for channels and servers.
 *
 */
public class SJSessionOperationTypeBuilder extends ContextVisitor
{	
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	private SJExtFactory sjef = sjnf.extFactory();
	
	/**
	 * 
	 */
	public SJSessionOperationTypeBuilder(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException
	{
		return this;
	}
	
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{		
		/*if (n instanceof SJSessionTry)
		{
			// Could give something like SJSessionOperationExt (i.e. list of sjnames and maybe SJUnknownTypes to session-try).
		}
		else */if (n instanceof SJSessionOperation)
		{
			if (n instanceof SJBasicOperation)
			{
				if (n instanceof SJPass) // Includes SJSend.
				{
					if (n instanceof SJCopy)
					{
						n = buildSJCopy((SJCopy) n);						
					}
					else
					{
						n = buildSJPass((SJPass) n);
					}					
				}
				else if (n instanceof SJReceive)
				{
					n = buildSJReceive((SJReceive) n);
				}
				else if (n instanceof SJRecurse)
				{
					n = buildSJRecurse((SJRecurse) n);
				}
				else if(!(n instanceof SJInternalOperation))
				{
					throw new SemanticException("[SJSessionOperationTypeBuilder] Session operation not yet supported: " + n);
				}	
			}
			else if (n instanceof SJCompoundOperation)
			{
				if (n instanceof SJBranchOperation)
				{
					n = buildSJBranchOperation((SJBranchOperation) n);
				}
				else if (n instanceof SJLoopOperation)
				{
					if (n instanceof SJWhile) // SJLoopOperation interface not working for javac...
					{
						n = buildSJWhile((SJWhile) n);				
					}
					else //if (n instanceof SJRecursion)
					{
						n = buildSJRecursion((SJRecursion) n);
					}
				}
				else
				{
					throw new SemanticException("[SJSessionOperationTypeBuilder] Session operation not yet supported: " + n);
				}
			}
			else
			{
				throw new SemanticException("[SJSessionOperationTypeBuilder] Session operation not yet supported: " + n);
			}
		}	
		else if (n instanceof SJSpawn)
		{
			n = buildSJSpawn((SJSpawn) n);
		}
		else if (n instanceof Cast)
		{
			n = buildCast((Cast) n);
		}
		
		return n;
	}
	
	private SJCopy buildSJCopy(SJCopy c) throws SemanticException
	{				
		Expr arg = (Expr) c.arguments().get(1); // Factor out constant.
		
		if (arg instanceof SJChannelVariable /*|| arg instanceof SJSocketVariable*/) // Only SJCopy can accept na-final args.
		{
			SJNamedInstance ni = (SJNamedInstance) context().findLocal(((SJVariable) arg).sjname());			
			SJSessionType st = sjts.SJSendType(ni.sessionType()); // For session sockets, should always be SJUnknownType. 
			
			c = (SJCopy) buildSJPassAux(c, st);
		}
		else
		{
			c = (SJCopy) buildSJPass(c);
		}
		
		return c;
	}
	
	private SJPass buildSJPass(SJPass p) throws SemanticException
	{		
		Expr arg = (Expr) p.arguments().get(1); // Factor out constant.
		
		Type messageType = null;
		
		if (arg instanceof SJSocketVariable) 
		{
			SJNamedInstance ni = (SJNamedInstance) context().findLocal(((SJVariable) arg).sjname());
			
			messageType = ni.sessionType(); // For session sockets, should always be SJUnknownType. 
		}
		else
		{
			messageType = ((Expr) p.arguments().get(1)).type(); // Factor out constant.
		}

		SJSessionType st = sjts.SJSendType(messageType);
		
		p = (SJPass) buildSJPassAux(p, st);
		
		return p;
	}
	
	private SJPass buildSJPassAux(SJPass p, SJSessionType st)
	{
		List<String> sjnames = getTargetNames(p.targets(), false);
		
		p = (SJPass) setSJSessionOperationExt(sjef, p, st, sjnames);		
		
		return p;
	}
	
	private SJReceive buildSJReceive(SJReceive r) throws SemanticException
	{		
		String name = r.name();
		
		SJSessionType st;
		
		if (name.equals(SJ_SOCKET_RECEIVE)) // FIXME: when SJNodeFactory is fixed to use the "runtime" constants rather than the "socket" constants, this routine should be fixed as well. // Includes the higher-order operations. 
		{
			st = sjts.SJReceiveType(sjts.SJUnknownType());
		}
		else if (name.equals(SJ_SOCKET_RECEIVEINT))
		{
			st = sjts.SJReceiveType(sjts.Int());  		
		}
		else if (name.equals(SJ_SOCKET_RECEIVEBOOLEAN))
		{
			st = sjts.SJReceiveType(sjts.Boolean());  		
		}
		else if (name.equals(SJ_SOCKET_RECEIVEDOUBLE))
		{
			st = sjts.SJReceiveType(sjts.Double());  		
		}
		else
		{
			throw new SemanticException("[SJSessionOperationTypeBuilder] Shouldn't get in here: " + name);
		}
		
		List<String> sjnames = getTargetNames(r.targets(), false); 
		
		r = (SJReceive) setSJSessionOperationExt(sjef, r, st, sjnames);
		
		return r;
	}
	
	private SJRecurse buildSJRecurse(SJRecurse r)
	{
		SJSessionType st = sjts.SJRecurseType(((SJRecurse) r).label());  		
		List<String> sjnames = getTargetNames(r.targets(), false);
		
		r = (SJRecurse) setSJSessionOperationExt(sjef, r, st, sjnames);
		
		return r;
	}
	
	private Cast buildCast(Cast c) throws SemanticException
	{
		Expr e = c.expr();
		
		if (e instanceof SJReceive)
		{
			SJReceive r = (SJReceive) e;
			List<String> sjnames = getSJSessionOperationExt(r).sjnames();			
			Type t = null;
			
			if (c instanceof SJSessionTypeCast)
			{
				SJTypeNode tn = ((SJSessionTypeCast) c).sessionType();
				SJSessionType st = tn.type();
				
				if (tn instanceof SJProtocolNode && st instanceof SJCBeginType) // Duplicated in SJSessionMethodTypeBuilder.
				{
					throw new SemanticException("[SJSessionOperationTypeBuilder] Protocol reference for channel type casts not yet supported: " + c);
				}
				
				t = st;
			}
			else
			{
				t = c.type();
			}

			SJSessionType st = sjts.SJReceiveType(t);
			
			c = c.expr((SJReceive) setSJSessionOperationExt(sjef, r, st, sjnames));
		}
		
		return c;
	}
	
	private SJBranchOperation buildSJBranchOperation(SJBranchOperation bo)
	{
		List sjnames = getTargetNames(bo.targets(), false);
		SJSessionType st = sjts.SJUnknownType();
		
		if (bo instanceof SJOutbranch)
		{
			sjnames = getTargetNames(bo.targets(), false);
				 
			st = sjts.SJOutbranchType().branchCase(((SJOutbranch) bo).label(), st);
		}
		else //if (bo instanceof SJInbranch)
		{
			SJInbranchType ibt = sjts.SJInbranchType();
			
			for (SJInbranchCase ibc : ((SJInbranch) bo).branchCases())
			{
				ibt.branchCase(ibc.label(), sjts.SJUnknownType()); // Could just alias st?
			}
			
			st = ibt;
		}				
		
		bo = (SJBranchOperation) setSJSessionOperationExt(sjef, bo, st, sjnames);		
		
		return bo;
	}
	
	private SJWhile buildSJWhile(SJWhile w)
	{
		List sjnames = getTargetNames(w.targets(), false);
		SJSessionType st = sjts.SJUnknownType();
		
		if (w instanceof SJOutwhile || w instanceof SJOutInwhile) // FIXME: hacky, inwhile element not explicitly typed.
		{
			sjnames = getTargetNames(w.targets(), false);
				 
			st = sjts.SJOutwhileType().body(st);
		}
		else //if (w instanceof SJInwhile)
		{
			st = sjts.SJInwhileType().body(st);
		}				
		
		w = (SJWhile) setSJSessionOperationExt(sjef, w, st, sjnames);

		return w;
	}
	
	private SJRecursion buildSJRecursion(SJRecursion r)
	{
		List sjnames = getTargetNames(r.targets(), false);
		SJSessionType st = sjts.SJRecursionType(r.label()).body(sjts.SJUnknownType());
		
		r = (SJRecursion) setSJSessionOperationExt(sjef, r, st, sjnames);
		
		return r;
	}
	
	private SJSpawn buildSJSpawn(SJSpawn s) throws SemanticException
	{
		List sjnames = getTargetNames(s.targets(), true);
		List<SJSessionType> sts = new LinkedList<SJSessionType>();
		
		for (String sjname : (List<String>) sjnames)
		{
			sts.add(sjts.SJSendType(sjts.SJUnknownType()));
		}
		
		s = (SJSpawn) setSJSessionOperationExt(s, sjnames, sts);
		
		return s;
	}
	
	private static List<String> getTargetNames(List targets, boolean channelsAllowed)
	{
		List<String> sjnames = new LinkedList<String>();
		
		for (Iterator i = targets.iterator(); i.hasNext(); )
		{
			SJVariable v = (SJVariable) i.next();
			
			if (channelsAllowed && v instanceof SJLocalChannel)
			{
				sjnames.add(((SJLocalChannel) v).sjname());
			}
			else
			{
				sjnames.add(((SJSocketVariable) v).sjname());
			}
		} 
		
		return sjnames;
	}
}
