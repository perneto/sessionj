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
abstract public class SJAbstractSessionVisitor extends ContextVisitor  
{	
	protected SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	protected SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	protected SJExtFactory sjef = sjnf.extFactory();
	
	protected SJContext sjcontext = new SJContext_c(this);
	
	/**
	 * 
	 */
	public SJAbstractSessionVisitor(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	abstract protected NodeVisitor sjEnterCall(Node parent, Node n) throws SemanticException;
	abstract protected Node sjLeaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException;
	
	// This will be called on the visitor cloned by the parent ContextVisitor enter routine. 
	protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException
	{
		enterSJContext(parent, n);
		
		return sjEnterCall(parent, n);
		
		//return this; // Otherwise need to hand over the session context object and update the cv field to the new visitor.
	}
	
	protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{		
		n = sjLeaveCall(parent, old, n, v); // Want the Visitor to do stuff whilst in the current context, i.e. before it is popped. 
		
		SJContextElement ce = leaveSJContext(old, n, v); // We don't need the popped context for anything (just needed to pop it). But we need to pop and process compound contexts before performing type building etc. for those operations.		
		
		if (n instanceof SJSessionOperation) // Must come before Expr, SJBasicOperations are Exprs.
		{
			if (!(n instanceof SJInternalOperation))
			{
				n = recordSJSessionOperation(parent, (SJSessionOperation) n);
			}
		}
		else if (n instanceof SJSpawn)
		{
			n = recordSJSpawn((SJSpawn) n);
		}
		else if (n instanceof Expr)
		{
			if (n instanceof Assign)
			{
				n = recordAssign((Assign) n);
			}
			else if (n instanceof SJChannelOperation) 
			{
				n = recordSJChannelOperation((SJChannelOperation) n);
			}
			else if (n instanceof SJServerOperation)
			{
				n = recordSJServerOperation((SJServerOperation) n); 
			}
			else if (n instanceof Cast)
			{
				if (n instanceof SJChannelCast)
				{
					//n = recordSJChannelCast(parent, (SJChannelCast) n); //FIXME.
				}
				/*else if (n instanceof SJSessionCast) // Already taken care of by recordAssign.
				{
					n = recordSJSessionCast(parent, (SJSessionCast) n);
				}*/
			}
		}				
		else if (n instanceof ProcedureCall)
		{
			n = recordProcedureCall((ProcedureCall) n); 
		}
		
		return n;
	}
	
	private SJSessionOperation recordSJSessionOperation(Node parent, SJSessionOperation so) throws SemanticException
	{
		List<String> sjnames = getSJSessionOperationExt(so).sjnames();
		SJSessionType st = getSessionType(so);
		
		for (String sjname : sjnames)
		{
			if (so instanceof SJPass) //FIXME: also do channel passing.
			{
				Expr arg = (Expr) ((SJPass) so).arguments().get(1);
						
				if (arg instanceof SJLocalSocket) // A bit lucky that this still works after SJHigherOrderTranslator? Need to be careful about translation is allowed? Or about which passes are allowed to SJSessionVisitors.
				{					
					String s = ((SJLocalSocket) arg).sjname();
					
					if (s.startsWith(SJ_TMP_LOCAL))
					{
						s = s.substring(SJ_TMP_LOCAL.length() + "_".length());
					}

					sjcontext.delegateSession(s); // Maybe should instead just record the type from the extension object rather than recalculating the delegated (remaining) type here.
				}
			}
			
			sjcontext.advanceSession(sjname, st);
		}
		
		return so;
	}
	
	private SJSpawn recordSJSpawn(SJSpawn s) throws SemanticException
	{
		Iterator<SJSessionType> i = s.sessionTypes().iterator();
		
		for (String sjname : s.sjnames())
		{
			SJSessionType st = i.next();
			
			if (st instanceof SJDelegatedType) // For sessions.
			{
				sjcontext.delegateSession(sjname); 
			}
			else
			{
				// Channels.
			}
		}
		
		return s;
	}
	
	private Assign recordAssign(Assign a) throws SemanticException
	{
		if (a.type().isSubtype(SJ_CHANNEL_TYPE))
		{
			Expr right = a.right();
			
			if (right instanceof SJChannelCreate)
			{
				Expr left = a.left();						
				SJChannelVariable cv = (SJChannelVariable) left; 			
				String sjname = cv.sjname();
				SJSessionType st = getSJTypeableExt(right).sessionType();										
				SJLocalChannelInstance lci = (SJLocalChannelInstance) sjcontext.getChannel(sjname); 	
				
				sjcontext.addChannel(sjts.SJLocalChannelInstance(lci, st, sjname)); 
			}
		}
		else if (a.type().isSubtype(SJ_SERVER_INTERFACE_TYPE))
		{
			Expr right = a.right();
			
			if (right instanceof SJServerCreate)
			{
				Expr left = a.left();						
				SJServerVariable sv = (SJServerVariable) left; 				
				String sjname = sv.sjname();
				SJSessionType st = getSJTypeableExt(right).sessionType();											
				SJLocalServerInstance lsi = (SJLocalServerInstance) sjcontext.getServer(sjname); 
				
				sjcontext.addServer(sjts.SJLocalServerInstance(lsi, st, sjname)); // Replaces the SJUnknownType added by the LocalDecl (for the server socket). 
				sjcontext.openService(sjname, st); // Replaces the SJUnknownType added at server-try enter.
			}
		}
		else if (a.type().isSubtype(SJ_SOCKET_INTERFACE_TYPE))
		{
			Expr right = a.right();
			
			if (right instanceof SJRequest || right instanceof SJAccept || right instanceof SJSessionCast) // FIXME: make common interface for session return operations.   
			{							
				Expr left = a.left();				
				SJSocketVariable sv = (SJSocketVariable) left;				
				String sjname = sv.sjname();				
				SJLocalSocketInstance lsi = (SJLocalSocketInstance) sjcontext.getSocket(sjname); 				
				SJSessionType st = getSJTypeableExt(right).sessionType();
				
				sjcontext.addSocket(sjts.SJLocalSocketInstance(lsi, st, sjname)); // Not really necessary.										   				
				sjcontext.openSession(sjname, st);
				
				if (right instanceof SJRequest)
				{
					sjcontext.advanceSession(sjname, sjts.SJCBeginType());
				}
				else if (right instanceof SJAccept)
				{
					sjcontext.advanceSession(sjname, sjts.SJSBeginType());
				}
			}
		}
		
		return a;
	}

	private SJChannelOperation recordSJChannelOperation(SJChannelOperation co)
	{
		// Nothing needed.
		
		return co;
	}
	
	private SJServerOperation recordSJServerOperation(SJServerOperation so)
	{
		// Nothing needed.
		
		return so;
	}
	
	/*private SJSessionCast recordSJSessionCast(Node parent, SJSessionCast sc)
	{
		if (parent instanceof LocalAssign)
		{
			
		}
		else
		{
			throw new SJRuntimeException("[SJAbstractVisitor] Shouldn't get here: " + parent);
		}
		
		return sc;
	}*/
	
	private ProcedureCall recordProcedureCall(ProcedureCall pc) throws SemanticException // Based on SJSessionTypeChecker counterpart.
	{
		ProcedureInstance pi = pc.procedureInstance();
		
		if (pi instanceof SJProcedureInstance)
		{		
			List<Type> sft = ((SJProcedureInstance) pi).sessionFormalTypes();
			
			if (sft != null) // null means no arguments? Or Polyglot bug (should be empty list)?
			{			
				Iterator i = pc.arguments().iterator();
				
				for (Type theirs : sft)
				{	
					Expr arg = (Expr) i.next();					
					
					if (theirs instanceof SJSessionType)
					{
						if (arg instanceof SJLocalSocket) //FIXME: need to support channel arguments.
						{	
							String sjname = ((SJLocalSocket) arg).sjname();
							
							SJSessionType ours = sjcontext.sessionRemaining(sjname); 
							
							if (((SJLocalSocket) arg).flags().isFinal())
							{					
								for (SJSessionType st = (SJSessionType) theirs; st != null; st = st.child())
								{
									sjcontext.advanceSession(sjname, ours.nodeClone());
									
									ours = ours.child();
								}
							}
							else
							{
								sjcontext.delegateSession(sjname); 
							}
						}																
					}					
				}
			}
		}
		
		return pc;
	}
	
	private void enterSJContext(Node parent, Node n) throws SemanticException // Basically duplicated from SJTypeChecker.
	{
		if (n instanceof LocalDecl) 
		{
			LocalDecl ld = (LocalDecl) n;
			LocalInstance li = ld.localInstance(); 
			
			if (li instanceof SJNamedInstance)
			{
				Type dt = ld.declType(); 
				
				if (dt.isSubtype(SJ_CHANNEL_TYPE))
				{				
					sjcontext.addChannel((SJNamedInstance) li);
				}
				else if (dt.isSubtype(SJ_SOCKET_INTERFACE_TYPE))
				{				
					sjcontext.addSocket((SJNamedInstance) li); // Should be SJUnknownType.
				}
				else if (dt.isSubtype(SJ_SERVER_INTERFACE_TYPE))
				{				
					sjcontext.addServer((SJNamedInstance) li);					
				}
			}
		}
		else if (n instanceof Try)
		{
			if (n instanceof SJTry)
			{
				if (n instanceof SJSessionTry)		
				{
					sjcontext.pushSJSessionTry((SJSessionTry) n);
				}
				else //if (n instanceof SJServerTry)		
				{
					sjcontext.pushSJServerTry((SJServerTry) n);
				} 
			}
			else
			{
				sjcontext.pushTry();
			}			
		}
		else if (n instanceof SJCompoundOperation) // Must come before the base class cases for Loop/Block.
		{
			if (n instanceof SJBranchOperation)
			{
				if (n instanceof SJOutbranch)
				{
					sjcontext.pushSJBranchCase((SJBranchCase) n);
				}
				else //if (n instanceof SJInbranch)
				{
					sjcontext.pushSJBranchOperation((SJBranchOperation) n);
				}
			}
			else if (n instanceof SJLoopOperation)
			{
				if (n instanceof SJWhile)
				{
					sjcontext.pushSJWhile((SJWhile) n); // FIXME: should differentiate the contexts (including outinwhile).
				}
				else //if (n instanceof SJRecursion)
				{
					sjcontext.pushSJRecursion((SJRecursion) n);
				}
			}
		}
		else if (n instanceof If)
		{			
			sjcontext.pushBranch();
		}				
		else if (n instanceof Loop)
		{
			sjcontext.pushLoop();
		}
		else if (n instanceof Block)
		{
			if (n instanceof SJBranchCase)
			{
				sjcontext.pushSJBranchCase((SJBranchCase) n);
			}			
			else
			{
				sjcontext.pushBlock();
				
				if (parent instanceof MethodDecl)
				{			
					sjcontext.pushMethodBody((MethodDecl) parent);
				}
			}
		}
		else if (n instanceof MethodDecl)
		{
			sjcontext.pushBlock();
		}
		
		sjcontext.setVisitor(this); // ContextVisitor returns a new visitor for each scope.
	}
	
	private SJContextElement leaveSJContext(Node old, Node n, NodeVisitor v) throws SemanticException // Duplicated from SJTypeChecker.
	{
		SJContextElement ce = null;
		
		if (n instanceof Try) // Includes SJTry (SJSessionTry and SJServerTry).
		{
			ce = sjcontext.pop();
		}
		else if (n instanceof SJInbranch)
		{
			ce = sjcontext.pop();
		}
		else if (n instanceof If)
		{
			ce = sjcontext.pop();
		}		
		else if (n instanceof Loop) // Includes SJLoopOperation.
		{
			ce = sjcontext.pop();
		}
		else if (n instanceof Block) // Includes SJRecursion, SJBranchCase and method bodies.
		{
			ce = sjcontext.pop();
		}		
		else if (n instanceof MethodDecl)
		{
			ce = sjcontext.pop();
		}
		
		sjcontext.setVisitor(this); // Is this needed?
		
		return ce;
	}
}
