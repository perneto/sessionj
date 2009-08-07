/**
 * 
 */
package sessionj.util;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.visit.*;

import sessionj.SJConstants;
import sessionj.ast.*;
import sessionj.ast.typenodes.*;
import sessionj.ast.sessops.*;
import sessionj.extension.*;
import sessionj.extension.noalias.*;
import sessionj.extension.sessops.*;
import sessionj.extension.sesstypes.*;
import sessionj.types.*;
import sessionj.types.noalias.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;
import sessionj.visit.SJProtocolDeclTypeBuilder;

/**
 * @author Raymond 
 *
 */
public class SJCompilerUtils
{
	private SJCompilerUtils() { }
	
	public static void updateSJFieldInstance(FieldInstance fi, SJFieldInstance sjfi)
	{
		SJParsedClassType pct = (SJParsedClassType) fi.container();
		List<FieldInstance> fields = new LinkedList<FieldInstance>();	
		
		for (Iterator i = pct.fields().iterator(); i.hasNext(); )
		{
			fields.add((FieldInstance) i.next());
		}
		
		fields.remove(fi); // Done like this, just to be uniform with ConstructorDecl and MethodDecl.
		fields.add((FieldInstance) sjfi);
		
		pct.setFields(fields); // Works because ParsedClassType is mutable (no need to reassign defensive copy).
	}
	
	public static void updateSJConstructorInstance(ConstructorInstance ci, SJConstructorInstance sjci)
	{
		SJParsedClassType pct = (SJParsedClassType) ci.container();
		List<ConstructorInstance> constructors = (List<ConstructorInstance>) copyProcedureInstanceList(pct.constructors());
		
		constructors.remove(ci); 
		constructors.add((ConstructorInstance) sjci);
		
		pct.setConstructors(constructors); 
	} 

	public static void updateSJMethodInstance(MethodInstance mi, SJMethodInstance sjmi)
	{
		SJParsedClassType pct = (SJParsedClassType) mi.container();
		List<MethodInstance> methods = (List<MethodInstance>) copyProcedureInstanceList(pct.methods());
		
		methods.remove(mi); 
		methods.add((MethodInstance) sjmi);
		
		pct.setMethods(methods); 
	} 
	
	public static final SJTypeableExt getSJTypeableExt(Node n)
	{
		if (n.ext(2) == null) // SJCompoundOperation.
		{
			return (SJTypeableExt) n.ext(1);
		}
		else 
		{
			return (SJTypeableExt) n.ext(2); // Factor out constant.
		}
	}
	
	public static final Node setSJTypeableExt(SJExtFactory sjef, Node n, SJSessionType st)
	{
		return n.ext(2, sjef.SJTypeableExt(st)); // Factor out constant. // Won't be a SJCompoundOperation (so should have a SJNoAliasExt.
	}
	
	public static final SJNamedExt getSJNamedExt(Node n)
	{
		return (SJNamedExt) getSJTypeableExt(n); 
	}
	
	public static final Node setSJNamedExt(SJExtFactory sjef, Node n, SJSessionType st, String sjname)
	{
		return n.ext(2, sjef.SJNamedExt(st, sjname)); // Factor out constant. // Won't be a SJCompoundOperation.			
	}
	
	public static final SJSessionOperationExt getSJSessionOperationExt(SJSessionOperation so)
	{
		return (SJSessionOperationExt) getSJTypeableExt(so);		
	}
	
	public static final Node setSJSessionOperationExt(SJExtFactory sjef, SJSessionOperation so, SJSessionType st, List<String> sjnames)
	{
		if (so.ext(1) == null) // SJCompoundOperation.
		{
			return so.ext(1, sjef.SJSessionOperationExt(st, sjnames)); // Factor out constant.
		}
		else 
		{
			return so.ext(2, sjef.SJSessionOperationExt(st, sjnames)); // Factor out constant. 
		}
	}
	
	public static final Node setSJSessionOperationExt(/*SJExtFactory sjef, */SJSpawn s, List<String> sjnames, List<SJSessionType> sts) // FIXME: SJSpawn is an example of multiply typed session operations.
	{
		/*if (s.ext(1) == null) // Not possible for SJSpawn. 
		{
			return so.ext(1, sjef.SJSessionOperationExt(st, sjnames)); // Factor out constant.
		}
		else*/ 
		{
			s = s.sjnames(sjnames);
			s = s.sessionTypes(sts); // FIXME: create an extension object for this information.
			
			return s; 
		}
	}
	
	public static SJSessionType getSessionType(Node n) // Should be SJTypeable parameter?
	{
		return getSJTypeableExt(n).sessionType();
	}
	
	public static String getSJName(Node n)
	{
		return getSJNamedExt(n).sjname();
	}
	
	public static final SJNoAliasExt getSJNoAliasExt(Node n) // First ext link is for noalias.
	{
		return (SJNoAliasExt) n.ext(1); // Factor out constant.
	}
	
	public static final Node setSJNoAliasExt(SJExtFactory sjef, Node n, boolean isNoAlias)
	{
		return n.ext(1, sjef.SJNoAliasExt(isNoAlias)); // Factor out constant.
	}

	public static final SJNoAliasFinalExt getSJNoAliasFinalExt(Node n) 
	{
		return (SJNoAliasFinalExt) getSJNoAliasExt(n);
	}
	
	public static final Node setSJNoAliasFinalExt(SJExtFactory sjef, Node n, boolean isNoAlias, boolean isFinal)
	{
		SJNoAliasFinalExt nafe = sjef.SJNoAliasFinalExt(isNoAlias, isFinal);

		return n.ext(1, nafe);
	}
	
	public static final SJNoAliasVariablesExt getSJNoAliasVariablesExt(Node n) // First ext link is for noalias.
	{
		return (SJNoAliasVariablesExt) n.ext(1);
	}
	
	public static final Node setSJNoAliasVariablesExt(SJExtFactory sjef, Node n, List<Field> fields, List<Local> locals, List<ArrayAccess> arrayAccesses)
	{
		SJNoAliasVariablesExt nave = sjef.SJNoAliasVariablesExt();
		
		nave.addFields(fields);
		nave.addLocals(locals);
		nave.addArrayAccesses(arrayAccesses);
		
		return n.ext(1, nave);
	}
	
	public static final SJNoAliasExprExt getSJNoAliasExprExt(Node n) // First ext link is for noalias.
	{
		return (SJNoAliasExprExt) getSJNoAliasExt(n);
	}
	
	public static final Node setSJNoAliasExprExt(SJExtFactory sjef, Node n, boolean isNoAlias, boolean isExpr, List<Field> fields, List<Local> locals, List<ArrayAccess> arrayAccesses)
	{
		SJNoAliasExprExt naee = sjef.SJNoAliasExprExt(isNoAlias, isExpr);
		
		naee.addFields(fields);
		naee.addLocals(locals);
		naee.addArrayAccesses(arrayAccesses);
		
		return n.ext(1, naee);
	}
	
	//Only works post disambiguation pass. Prior to that, represented by SJAmbNoAliasTypeNode.
	public static final boolean isNoAlias(Node n) 
	{
		if (n instanceof TypeNode) // Ext object can be null for regular TypeNodes.*/
		{
			SJNoAliasExt nae = getSJNoAliasExt(n);		
			
			return (nae == null) ? false : nae.isNoAlias(); 
		}
		else
		{
			return getSJNoAliasExt(n).isNoAlias();
			//return e.type() instanceof SJNoAliasReferenceType;
		}			
	}

	public static final boolean isFinal(Node n) 
	{		
		return getSJNoAliasFinalExt(n).isFinal();
		//return e.type() instanceof SJNoAliasReferenceType;
	}
	
	/*public static final void replaceASTNode(Job job, Node old, Node n)
	{
		job.ast(job.ast().visit(new SJNodeReplacer(old, n)));
	}*/
	
	public static Type subsumeSendTypes(Type t1, Type t2) throws SemanticException
	{
		return subsumeMessageTypes(t1, t2, true);
	}

	public static Type subsumeReceiveTypes(Type t1, Type t2) throws SemanticException
	{
		return subsumeMessageTypes(t1, t2, false);
	}
	
	private static Type subsumeMessageTypes(Type t1, Type t2, boolean forSend) throws SemanticException // false forSend means for receive.
	{
		if (t1 instanceof SJSessionType)
		{
			if (!(t2 instanceof SJSessionType))
			{
				throw new SemanticException("[SJCompilerUtils] Not subsumable: " + t1 + ", " + t2);
			}
	
			return ((SJSessionType) t1).subsume((SJSessionType) t2);
		}
		else if (t1 instanceof ReferenceType)
		{
			if (!(t2 instanceof ReferenceType)) // FIXME: could do some autoboxing for primitive types. 
			{
				throw new SemanticException("[SJCompilerUtils] Not subsumable: " + t1 + ", " + t2);
			}
	
			if (t1.isSubtype(t2)) // Could be checked earlier.
			{
				return (forSend) ? t2 : t1;
			}
			else if (t2.isSubtype(t1))
			{
				return (forSend) ? t1 : t2;
			}
			else
			{
				throw new SemanticException("[SJCompilerUtils] Full send type subsumption not yet supported: " + t1 + ", " + t2); // FIXME: e.g. String + Integer -> Object
			}
		}
		else if (t1.typeEquals(t2))
		{
			return t1;
		}
	
		throw new SemanticException("[SJCompilerUtils] Not subsumable: " + t1 + ", " + t2); // FIXME: some primitive types subsumable (e.g. char->int, int->dobule).
	}
	
	public static Node buildAndCheckTypes(Job job, ContextVisitor cv, Node n) throws SemanticException
	{
		TypeSystem ts = cv.typeSystem();
		NodeFactory nf = cv.nodeFactory();
			
		AmbiguityRemover ar = (AmbiguityRemover) new AmbiguityRemover(job, ts, nf, true, true).context(cv.context());
		
		n = disambiguateNode(ar, n); 
		
		TypeChecker tc = (TypeChecker) new TypeChecker(job, ts, nf).context(ar.context());		
		
		n = n.visit(tc);
		
		ConstantChecker cc = (ConstantChecker) new ConstantChecker(job, ts, nf).context(tc.context());
		
		n = n.visit(cc); // FIXME: breaks SJProtocolDeclTypeBuilding pass.

		return n;		
	}
	
	public static Node disambiguateNode(AmbiguityRemover ar, Node n) throws SemanticException
	{
		TypeSystem ts = ar.typeSystem();
		NodeFactory nf = ar.nodeFactory();
		
		TypeBuilder tb = new TypeBuilder(ar.job(), ts, nf).pushContext(ar.context());
		
		n = n.visit(tb);
		n = n.visit(ar);
		
		return n;
	}
	
	public static SJTypeNode disambiguateSJTypeNode(Job job, ContextVisitor cv, SJTypeNode tn) throws SemanticException
	{
		SJTypeSystem sjts = (SJTypeSystem) cv.typeSystem();		
		SJSessionType st = null;
		
		if (tn instanceof SJBeginNode)
		{
			if (tn instanceof SJCBeginNode)
			{
				st = sjts.SJCBeginType();
			}
			else //if (tn instanceof SJSBeginNode)
			{
				st = sjts.SJSBeginType();
			}				
		}
		else if (tn instanceof SJMessageCommunicationNode)
		{
			SJMessageCommunicationNode mcn = (SJMessageCommunicationNode) tn;
			TypeNode mtn = mcn.messageType();
			Type mt;

			if (mtn instanceof SJTypeNode)
			{
				mtn = (SJTypeNode) disambiguateSJTypeNode(job, cv, (SJTypeNode) mtn);
			}
			else
			{
				mtn = (TypeNode) buildAndCheckTypes(job, cv, mtn);
			}

			mcn = mcn.messageType(mtn);		
			mt = mtn.type();
			
			if (mcn instanceof SJSendNode)
			{			
				st = sjts.SJSendType(mt);				
			}
			
			else //if (mcn instanceof SJReceiveNode)
			{
				st = sjts.SJReceiveType(mt);
			}
			
			tn = mcn;
		}
		else if (tn instanceof SJBranchNode)
		{	
			SJBranchType bt = null;
			
			if (tn instanceof SJOutbranchNode)
			{
				bt = sjts.SJOutbranchType();
			}
			else //if (tn instanceof SJInbranchNode)
			{
				bt = sjts.SJInbranchType();
			}
			
			List<SJBranchCaseNode> branchCases = new LinkedList<SJBranchCaseNode>();			
			
			for (SJBranchCaseNode bcn : ((SJBranchNode) tn).branchCases())
			{
				bcn = (SJBranchCaseNode) disambiguateSJTypeNode(job, cv, bcn);							

				branchCases.add(bcn);
				
				bt = bt.branchCase(bcn.label(), bcn.type());				
			}
			
			tn = ((SJBranchNode) tn).branchCases(branchCases);
			
			st = bt;
		}
		else if (tn instanceof SJBranchCaseNode)
		{
			SJTypeNode body = ((SJBranchCaseNode) tn).body();
			
			if (body != null)
			{
				st = disambiguateSJTypeNode(job, cv, body).type(); // st is null by default.
			}
		}
		else if (tn instanceof SJLoopNode)
		{
			SJTypeNode body = ((SJLoopNode) tn).body();
			
			SJSessionType bt = null;
									
			if (body != null)
			{
				bt = disambiguateSJTypeNode(job, cv, body).type();
			}
			
			if (tn instanceof SJOutwhileNode)
			{
				st = sjts.SJOutwhileType().body(bt);
			}
			else if (tn instanceof SJInwhileNode)
			{
				st = sjts.SJInwhileType().body(bt);
			}
			else //if (tn instanceof SJRecursionNode)
			{
				st = sjts.SJRecursionType(((SJRecursionNode) tn).label()).body(bt);
			}
		}
		else if (tn instanceof SJRecurseNode)
		{
			st = sjts.SJRecurseType(((SJRecurseNode) tn).label());
		}
		else if (tn instanceof SJProtocolNode)
		{
			SJProtocolNode pn = (SJProtocolNode) tn;			
						
			SJTypeSystem ts = (SJTypeSystem) cv.typeSystem();
			SJNodeFactory nf = (SJNodeFactory) cv.nodeFactory();
			AmbiguityRemover ar = (AmbiguityRemover) new AmbiguityRemover(job, ts, nf, true, true).context(cv.context());
			
			//Receiver target = (Receiver) disambiguateNode(ar, pn.target());
			Receiver target = (Receiver) buildAndCheckTypes(job, cv, pn.target());
			
			if (target instanceof Field)
			{
				Field f = (Field) target;
				
				SJFieldInstance fi = (SJFieldInstance) sjts.findField((ReferenceType) f.target().type(), f.name(), cv.context().currentClass());
				
				if (fi instanceof SJFieldInstance)
				{
					if (!(fi instanceof SJFieldProtocolInstance)) // Similar mutually recursive lookahead routine to that of SJNoAliasTypeBuilder/SJNoAliasProcedureChecker. Also similar in that we build ahead, but cannot store the result, so have to do again later. 
					{
						// FIXME: this assumes the target class must have been visited (compiled up to this stage) before the current class. But this can break for mutually dependent classes.
						
						//SJProtocolDeclTypeBuilder pdtb = (SJProtocolDeclTypeBuilder) new SJProtocolDeclTypeBuilder(job, ts, nf).begin(); // Doesn't seem to be enough. 
						SJProtocolDeclTypeBuilder pdtb = (SJProtocolDeclTypeBuilder) new SJProtocolDeclTypeBuilder(job, ts, nf).context(cv.context()); // Seems to work, but does it make sense?
	
						SJParsedClassType pct = (SJParsedClassType) ts.typeForName(((Field) target).target().toString());
						ClassDecl cd = findClassDecl((SourceFile) job.ast(), pct.name()); // Need to qualify type name?
						
						if (cd == null)
						{
							throw new SemanticException("[SJCompilerUtils.disambiguateSJTypeNode] Compiling " + ((SourceFile) job.ast()).source().name() + ", class declaration not found: " + pct.name());
						}					
						
						cd = (ClassDecl) cd.visit(pdtb); // FIXME: will cycle for mutually recursive fields.
												
						fi = (SJFieldProtocolInstance) sjts.findField((SJParsedClassType) cd.type(), f.name(), pdtb.context().currentClass());
					}
					
					st = ((SJFieldProtocolInstance) fi).sessionType();
				}
				else // If trying to access a protocol field, target class must have been compiled using sessionjc. // FIXME: or 
				{
					throw new RuntimeException("[SJCompilerUtils.disambiguateSJTypeNode] Shouldn't get in here.");
				}
			}
			else if (target instanceof Local)
			{
				String protocol = ((Local) target).name(); 
				
				st = ((SJNamedInstance) cv.context().findLocal(protocol)).sessionType(); // No clone, immutable.
			}
			else //if (target instanceof ArrayAccess)
			{
				// FIXME: if the target class is only mention in the protocol reference, but not actually used otherwise, the above target disambiguation routine will fail. Similar for any message classes (e.g. Address) referred to in protocols, but not otherwise used. 
				
				throw new SemanticException("[SJCompilerUtils.disambiguateSJTypeNode] Protocol reference not yet supported for: " + target);
			}
						
			if (tn instanceof SJProtocolDualNode)
			{
				/*if (st instanceof SJSBeginType) // Only necessary to check this for session casts (done in SJSessionOperationTypeBuilder) and method parameters (done in SJMethodTypebuilder).
				{
					throw new SemanticException("[SJCompilerUtils.disambiguateSJTypeNode] Protocol reference to channel types not yet supported: " + target);
				}*/
				
				st = dualSessionType(st);
			}
			/*else
			{
				if (st instanceof SJCBeginType) // It's too late to check this here - base type checking will have failed (for otherwise correct programs) since the protocol reference parameter would be converted to type SJSocket.
				{
					throw new SemanticException("[SJCompilerUtils.disambiguateSJTypeNode] Protocol reference to channel types not yet supported: " + target);
				}
			}*/
			
			tn = pn.target(target);
		}
		else
		{
			throw new SemanticException("[SJCompilerUtils.disambiguateSJTypeNode] Unsupported session type node: " + tn);
		}
		
		tn = (SJTypeNode) tn.type(st);
		
		SJTypeNode child = tn.child();
		
		if (child != null)
		{
			tn = tn.child(disambiguateSJTypeNode(job, cv, child));
		}
		
		return tn;
	}
	
	private static SJSessionType dualSessionType(SJSessionType st) throws SemanticException
	{			
		SJTypeSystem sjts = null;
		
		if (st != null) // Only needed for badly-typed programs (i.e. something went wrong)?
		{
			sjts = st.typeSystem();
		}
		
		SJSessionType dual = null; 
		
		for ( ; st != null; st = st.child())
		{
			SJSessionType next = null;
			
			if (st instanceof SJBeginType)
			{
				if (st instanceof SJCBeginType)
				{
					next = sjts.SJSBeginType();
				}
				else //if (st instanceof SJSBeginType)
				{
					next = sjts.SJCBeginType();
				}
			}
			else if (st instanceof SJMessageCommunicationType)
			{
				Type mt = ((SJMessageCommunicationType) st).messageType();
				
				if (st instanceof SJSendType)
				{
					next = sjts.SJReceiveType(mt); // For higher-order types, don't dual the message type. 
				}
				else //if (st instanceof SJReceiveType)
				{
					next = sjts.SJSendType(mt);
				}
			}
			else if (st instanceof SJBranchType)
			{
				SJBranchType bt = (SJBranchType) st; 			
					
				if (st instanceof SJOutbranchType)
				{
					next = sjts.SJInbranchType();
				}
				else //if (st instanceof SJInbranchType)
				{
					next = sjts.SJOutbranchType();
				}
				
				for (SJLabel lab : bt.labelSet())
				{					
					next = ((SJBranchType) next).branchCase(lab, dualSessionType(bt.branchCase(lab)));
				}
			}
			else if (st instanceof SJLoopType)
			{
				SJLoopType lt = (SJLoopType) st; 			
				
				//if (st instanceof SJWhileType)
				//{
					if (st instanceof SJOutwhileType)
					{
						next = sjts.SJInwhileType();
					}
					else if (st instanceof SJInwhileType)
					{
						next = sjts.SJOutwhileType();
					}
				//}
				else //if (st instanceof SJRecursionType)
				{
					next = sjts.SJRecursionType(((SJRecursionType) st).label());
				}
								
				next = ((SJLoopType) next).body(dualSessionType(lt.body()));				
			}
			else if (st instanceof SJRecurseType)
			{
				next = sjts.SJRecurseType(((SJRecurseType) st).label()); 
			}
			else
			{
				throw new SemanticException("[SJCompilerUtils.dualSessionType] Unsupported session type: " + st);
			}	
			
			dual = (dual == null) ? next : dual.append(next);
		}
		
		return dual;
	}
	
	public static final ClassDecl findClassDecl(SourceFile sf, String name)
	{
		for (Iterator i = sf.decls().iterator(); i.hasNext(); )
		{
			ClassDecl cd = findClassDecl((ClassDecl) i.next(), name); // ClassDecl is the only subtype of TopLevelDecl.
			
			if (cd != null)
			{
				return cd;
			}
		}
		
		return null;
	}
	
	private static final ClassDecl findClassDecl(ClassDecl cd, String name)
	{
		if (cd.name().equals(name))
		{
			return cd;
		}
		
		for (Iterator i = cd.body().members().iterator(); i.hasNext(); )
		{
			ClassMember cm = (ClassMember) i.next();
			
			if (cm instanceof ClassDecl)
			{
				ClassDecl res = findClassDecl((ClassDecl) cm, name);
				
				if (res != null)
				{
					return res;
				}
			}
		}
		
		return null;
	}	
	
	// Expects a list of Expr.
	public static List<Type> getArgumentTypes(List arguments)
	{
		List<Type> argumentTypes = new LinkedList<Type>();
		
		for (Iterator i = arguments.iterator(); i.hasNext(); )
		{
			argumentTypes.add(((Expr) i.next()).type());
		}
		
		return argumentTypes;
	}
	
	public static final void debugPrint(String m)
	{
		if (SJConstants.SJ_DEBUG_PRINT)
		{
			System.out.print(m);
		}
	}
	
	public static final void debugPrintln(String m)
	{
		debugPrint(m + "\n");
	}
	
	private static List<? extends ProcedureInstance> copyProcedureInstanceList(List procedures)
	{		
		List<ProcedureInstance> copy = new LinkedList<ProcedureInstance>();
		
		for (Iterator i = procedures.iterator(); i.hasNext(); )
		{
			copy.add((ProcedureInstance) i.next());
		}			
	
		return copy;
	}	
}
