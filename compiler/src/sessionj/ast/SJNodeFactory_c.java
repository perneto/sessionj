package sessionj.ast;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.qq.*;
import polyglot.types.*;
import polyglot.util.*;

import sessionj.SJConstants;
import sessionj.ast.noalias.*;
import sessionj.ast.protocoldecls.*;
import sessionj.ast.createops.*;
import sessionj.ast.chanops.*;
import sessionj.ast.sesscasts.*;
import sessionj.ast.sessformals.*;
import sessionj.ast.sessops.*;
import sessionj.ast.servops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sesstry.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.typenodes.*;
//import sessionj.del.*;
import sessionj.extension.*;
import sessionj.types.SJTypeSystem;
import sessionj.util.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * NodeFactory for sessionj extension.
 */
public class SJNodeFactory_c extends NodeFactory_c implements SJNodeFactory 
{
    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
	
	private ExtensionInfo extInfo; 
	
	private SJExtFactory sjef = (SJExtFactory) super.extFactory();
	//private SJDelFactory sjdf = (SJDelFactory) super.delFactory();
	
	public SJNodeFactory_c()
	{
		super(new SJExtFactory_c());
		//super(new SJExtFactory_c(), new SJDelFactory_c());		
	}

	public SJExtFactory extFactory()
	{
		return sjef;
	}
	
	/*public SJDelFactory delFactory()
	{
		return sjdf;
	}*/
	
	/*
	 * public SJDelFactory delFactory() { return (SJDelFactory)
	 * super.delFactory(); }
	 */

	public void setExtensionInfo(ExtensionInfo extInfo)
	{
		this.extInfo = extInfo;
	}
	
	public SJAmbNoAliasTypeNode SJAmbNoAliasTypeNode(Position pos, AmbTypeNode atn)
	{
		SJAmbNoAliasTypeNode n = new SJAmbNoAliasTypeNode_c(pos, atn);

		// n = (SJAmbNoAliasTypeNode) n.ext(extFactory().SJNoAliasExt()); // No
		// point: is discarded by disambiguation pass, and is not actually needed
		// before then. So is now made and attached to the (new unambiguous) node by
		// the disambiguation pass.

		return n;
	}

	public SJNoAliasArrayTypeNode SJNoAliasArrayTypeNode(Position pos, ArrayTypeNode atn)
	{
		SJNoAliasArrayTypeNode n = new SJNoAliasArrayTypeNode_c(pos, atn);

		return n;
	}

	public SJNoAliasCanonicalTypeNode SJNoAliasCanonicalTypeNode(Position pos, CanonicalTypeNode ctn)
	{
		SJNoAliasCanonicalTypeNode n = new SJNoAliasCanonicalTypeNode_c(pos, ctn);

		return n;
	}
	
	public SJFieldProtocolDecl SJFieldProtocolDecl(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoAlias)
	{
		//flags = makeFinal(flags); // To utilise base type checking.

		SJFieldProtocolDecl n = new SJFieldProtocolDecl_c(pos, flags, CanonicalTypeNode(pos, SJ_PROTOCOL_TYPE), name, NullLit(pos), tn); // Null initialization overwritten by protocol declaration translation pass (dummy init. needed to satisfy base type checking).		
		
		if (isNoAlias) // Actually, the type system will enforce it to be na-final.
		{
			n = (SJFieldProtocolDecl) n.type(convertToNoAliasTypeNode(n.type(), true)); // Makes the object type: noalias SJProtocol. Also adds the extension objects for the session type (later disambiguated, etc. by SJProtocolDeclTypeBuilder).
		}

		return n; 
	}
	
	public SJLocalProtocolDecl SJLocalProtocolDecl(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoAlias)
	{
		//flags = makeFinal(flags); // To utilise base type checking.

		SJLocalProtocolDecl n = new SJLocalProtocolDecl_c(pos, flags, CanonicalTypeNode(pos, SJ_PROTOCOL_TYPE), name, NullLit(pos), tn);
		
		if (isNoAlias) // Will be enforced by type system.
		{
			n = (SJLocalProtocolDecl) n.type(convertToNoAliasTypeNode(n.type(), true));  
		}
		
		return n;
	}
	
	public SJCBeginNode SJCBeginNode(Position pos)
	{
		return new SJCBeginNode_c(pos);
	}
	
	public SJSBeginNode SJSBeginNode(Position pos)
	{
		return new SJSBeginNode_c(pos);
	}
	
	public SJSendNode SJSendNode(Position pos, TypeNode messageType)
	{
		return new SJSendNode_c(pos, messageType);
	}

	public SJOutbranchNode SJOutbranchNode(Position pos, List<SJBranchCaseNode> branchCases)
	{
		return new SJOutbranchNode_c(pos, branchCases);
	}
	
	public SJInbranchNode SJInbranchNode(Position pos, List<SJBranchCaseNode> branchCases)
	{
		return new SJInbranchNode_c(pos, branchCases);
	}
	
	public SJBranchCaseNode SJBranchCaseNode(Position pos, SJLabel lab, SJTypeNode body)
	{
		return new SJBranchCaseNode_c(pos, lab, body);
	}
	
	public SJReceiveNode SJReceiveNode(Position pos, TypeNode messageType)
	{
		return new SJReceiveNode_c(pos, messageType);
	}

	public SJOutwhileNode SJOutwhileNode(Position pos, SJTypeNode body)
	{
		return new SJOutwhileNode_c(pos, body);
	}
	
	public SJInwhileNode SJInwhileNode(Position pos, SJTypeNode body)
	{
		return new SJInwhileNode_c(pos, body);
	}

	public SJRecursionNode SJRecursionNode(Position pos, SJLabel lab, SJTypeNode body)
	{
		return new SJRecursionNode_c(pos, lab, body);
	}
	
	public SJRecurseNode SJRecurseNode(Position pos, SJLabel lab)
	{
		return new SJRecurseNode_c(pos, lab);
	}
	
	public SJProtocolRefNode SJProtocolRefNode(Position pos, Receiver target)
	{
		return new SJProtocolRefNode_c(pos, target);
	}
	
	public SJProtocolDualNode SJProtocolDualNode(Position pos, Receiver target)
	{
		return new SJProtocolDualNode_c(pos, target);
	}
	
	public SJChannelCreate SJChannelCreate(Position pos, List arguments)
	{
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_CHANNEL_TYPE);
		Id name = Id(pos, SJ_CHANNEL_CREATE);

		SJChannelCreate n = new SJChannelCreate_c(pos, target, name, arguments);

		//return (SJChannelCreate) n.ext(extFactory().SJCreateOperationExt());
		
		return n;
	}

	public SJSocketCreate SJSocketCreate(Position pos, List arguments)
	{
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_SOCKET_INTERFACE_TYPE);
		Id name = Id(pos, SJ_SOCKET_CREATE);

		SJSocketCreate n = new SJSocketCreate_c(pos, target, name, arguments);

		//return (SJSocketCreate) n.ext(extFactory().SJCreateOperationExt());
		
		return n;
	}	
	
	public SJServerCreate SJServerCreate(Position pos, List arguments)
	{
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_SERVER_TYPE);
		Id name = Id(pos, SJ_SERVER_CREATE);

		SJServerCreate n = new SJServerCreate_c(pos, target, name, arguments);

		//return (SJServerCreate) n.ext(extFactory().SJCreateOperationExt());
		
		return n;
	}
	
	public SJLocalChannel SJLocalChannel(Position pos, Id name)
	{
		SJLocalChannel lc = new SJLocalChannel_c(pos, name);
		
		return lc;
	}
	
	public SJLocalSocket SJLocalSocket(Position pos, Id name)
	{
		SJLocalSocket ls = new SJLocalSocket_c(pos, name);
		
		return ls;		
	}

	public SJLocalServer SJLocalServer(Position pos, Id name)
	{
		SJLocalServer ls = new SJLocalServer_c(pos, name);
		
		return ls;		
	}
	
	public SJAmbiguousTry SJAmbiguousTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets)
	{
		SJAmbiguousTry n = new SJAmbiguousTry_c(pos, tryBlock, catchBlocks, finallyBlock, targets);
		
		return n;
	}
	
	public SJSessionTry SJSessionTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets)
	{
		SJSessionTry n = new SJSessionTry_c(pos, tryBlock, catchBlocks, finallyBlock, targets);
		
		return n;
	}
	
	public SJServerTry SJServerTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets)
	{
		SJServerTry n = new SJServerTry_c(pos, tryBlock, catchBlocks, finallyBlock, targets);
		
		return n;
	}
	
	public SJRequest SJRequest(Position pos, Receiver target, List arguments)
	{	
		Id name = Id(pos, SJ_CHANNEL_REQUEST);				  
		
		SJRequest n = new SJRequest_c(pos, target, name, arguments);
		
		return n;
	}
	
	public SJSend SJSend(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_SEND); // FIXME: the name shouldn't associated with the socket but rather the runtime.						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJSend n = new SJSend_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJPass SJPass(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_PASS);						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJPass n = new SJPass_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJCopy SJCopy(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_COPY);						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJCopy n = new SJCopy_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	// This is called by SJSessionOperationParser, not the parser, so the target has already been disambiguated and the ArrayInit could be created directly here.
	public SJReceive SJReceive(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECEIVE);	
		
		arguments.add(0, makeSocketsArray(pos, targets.size()));
		
		SJReceive n = new SJReceive_c(pos, target, name, arguments, targets);
		
		return n;
	}	

	public SJReceive SJReceiveInt(Position pos, List arguments, List targets) // FIXME: a bit hacky when it comes to type building - need to explicitly distinguish primitive and object type usage of the single SJReceive node class. 
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECEIVEINT);	
		
		arguments.add(0, makeSocketsArray(pos, targets.size()));
		
		SJReceive n = new SJReceive_c(pos, target, name, arguments, targets);
		
		return n;
	}	
	
	public SJReceive SJReceiveBoolean(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECEIVEBOOLEAN);	
		
		arguments.add(0, makeSocketsArray(pos, targets.size()));
		
		SJReceive n = new SJReceive_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJReceive SJReceiveDouble(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECEIVEDOUBLE);	
		
		arguments.add(0, makeSocketsArray(pos, targets.size()));
		
		SJReceive n = new SJReceive_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	//public SJRecurse SJRecurse(Position pos, List arguments, List targets)
	public SJRecurse SJRecurse(Position pos, SJLabel lab, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECURSE);						
				
		List arguments = new LinkedList();
		
		arguments.add(0, makeSocketsArray(pos, targets.size())); 
		arguments.add(1, StringLit(pos, lab.labelValue()));
		
		SJRecurse n = new SJRecurse_c(pos, target, name, arguments, targets, lab);
		
		return n;
	}

	public SJSpawn SJSpawn(Position pos, New w, List targets)
	{	
		Id name = Id(pos, SJ_THREAD_SPAWN);						
				
		List arguments = new LinkedList();
		
		/*for (Iterator i = targets.iterator(); i.hasNext(); )
		{
			arguments.add(i.next());
		}*/
		
		SJSpawn n = new SJSpawn_c(pos, w, name, arguments, targets);
		
		//n = (SJSpawn) n.del(sjdf.SJSpawnDel()); // Had some type checking problems (because new arguments are inserted) due to a previously missing but needed barrier between SJThreadParsing (generate the target spawn method in the SJThread and build types for the class) and SJSessionOperationParsing (translate the spawn call and type check against the target method).
		
		return n;
	}
	
	public SJOutlabel SJOutlabel(Position pos, SJLabel lab, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_OUTLABEL);						
				
		List arguments = new LinkedList();
		
		arguments.add(makeSocketsArray(pos, targets.size()));
		arguments.add(StringLit(pos, lab.labelValue()));
		
		SJOutlabel n = new SJOutlabel_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJInlabel SJInlabel(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_INLABEL);						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJInlabel n = new SJInlabel_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJOutsync SJOutsync(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_OUTSYNC);						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJOutsync n = new SJOutsync_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJInsync SJInsync(Position pos, List arguments, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_INSYNC);						
				
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJInsync n = new SJInsync_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJRecursionEnter SJRecursionEnter(Position pos, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECURSIONENTER);						
				
		List arguments = new LinkedList();
		
		arguments.add(0, makeSocketsArray(pos, targets.size()));   
		
		SJRecursionEnter n = new SJRecursionEnter_c(pos, target, name, arguments, targets);
		
		return n;
	}
	
	public SJRecursionExit SJRecursionExit(Position pos, List targets)
	{	
		CanonicalTypeNode target = CanonicalTypeNode(pos, SJ_RUNTIME_TYPE);
		Id name = Id(pos, SJ_SOCKET_RECURSIONEXIT);						
				
		List arguments = new LinkedList();
		
		if (targets.size() > 1) // Hacky? SJCompoundOperationTranslator currently needs to come after SJUnicastOptimiser.
		{
			NewArray na = makeSocketsArray(pos, targets.size());
			
			ArrayInit ai = ArrayInit(pos, targets); // Duplicated from SJSessionParser, SJRecursionExit only used by SJCompoundOperationTranslator - i.e. targets already disambiguated. 
			
			na = na.init(ai);
			na = na.dims(Collections.EMPTY_LIST); 
			na = na.additionalDims(1); // Factor out constant?		
			
			arguments.add(0, na);
		}
		else
		{
			arguments.add(targets.get(0));
		}
		
		SJRecursionExit n = new SJRecursionExit_c(pos, target, name, arguments, targets);
		
		return n;
	}
		
	public SJOutbranch SJOutbranch(Position pos, List stmts, SJLabel lab, List targets)
	{
		SJOutlabel os = SJOutlabel(pos, lab, targets); 
		
		List foo = new LinkedList();
		
		foo.add(Eval(pos, os));
		foo.addAll(stmts);
		
		SJOutbranch n = new SJOutbranch_c(pos, foo, lab, targets);

		//n = (SJOutbranch) n.del(delFactory().SJStructuralOperationDel());
		//n = (SJOutbranch) n.ext(extFactory().SJStructuralOperationExt(target));
		
		return n;
	}
	
	public SJInbranch SJInbranch(Position pos, List arguments, List<SJInbranchCase> branchCases, List targets)
	{
		SJInlabel il = SJInlabel(pos, arguments, targets);
		
		SJInbranch ib = new SJInbranch_c(pos, branchCases, il);
		
		return ib;
	}
	
	public SJInbranchCase SJInbranchCase(Position pos, List stmts, SJLabel lab)
	{
		SJInbranchCase ibc = new SJInbranchCase_c(pos, stmts, lab);
		
		return ibc;
	}
	
	public SJOutwhile SJOutwhile(Position pos, List arguments, Stmt body, List targets)
	{
		SJOutsync os = SJOutsync(pos, arguments, targets); 		
		SJOutwhile n = new SJOutwhile_c(pos, os, body, targets);

		//n = (SJOutwhile) n.del(delFactory().SJStructuralOperationDel());
		//n = (SJOutwhile) n.ext(extFactory().SJStructuralOperationExt(target));

		return n;
	}

	public SJOutInwhile SJOutInwhile(Position pos, Stmt body, List<Receiver> sources, List<Receiver> targets, Expr condition)
	{
		SJInsync is = SJInsync(pos, new LinkedList(), sources); // Factor out constants.
		
		List arguments = new LinkedList();
		arguments.add(is);
		
		SJOutsync os = SJOutsync(pos, arguments, targets);

        List<Receiver> all = new LinkedList<Receiver>(sources);
        all.addAll(targets);
        return new SJOutInwhile_c(pos, os, body, all, condition);
	}
	
	public SJInwhile SJInwhile(Position pos, List arguments, Stmt body, List targets)
	{
		SJInsync is = SJInsync(pos, arguments, targets);		
		SJInwhile n = new SJInwhile_c(pos, is, body, targets);

		//n = (SJInwhile) n.del(delFactory().SJStructuralOperationDel());
		//n = (SJInwhile) n.ext(extFactory().SJStructuralOperationExt(target));

		return n;
	}

	public SJRecursion SJRecursion(Position pos, Block body, SJLabel lab, List targets) // Inconvenient to ...
	{
		QQ qq = new QQ(extInfo, pos);
		
		String translation = null;
		List<Object> mapping = new LinkedList<Object>();
		
		translation = "for ( ; new Boolean(false).booleanValue(); ) { }"; // Dummy condition later replaced by SJCompoundOperationTranslator. Used because we cannot give the intended loop-variable the correct name yet (targets are ambiguous). 
		
		For f = (For) qq.parseStmt(translation, mapping.toArray());
		
		SJRecursionEnter re = SJRecursionEnter(pos, targets); 
		
		translation = "%E;";
		mapping.add(re);
		
		Eval e = (Eval) qq.parseStmt(translation, mapping.toArray());
		
		body = body.prepend(e);
		
		SJRecursion n = new SJRecursion_c(pos, f.inits(), f.cond(), body, lab, targets);

		return n;
	}
	
	public SJAccept SJAccept(Position pos, Receiver target, List arguments)
	{	
		Id name = Id(pos, SJ_SERVER_ACCEPT);				  
		
		SJAccept n = new SJAccept_c(pos, target, name, arguments);
		
		return n;
	}	

	public SJChannelCast SJChannelCast(Position pos, Expr expr, SJTypeNode tn)
	{
		SJChannelCast n = new SJChannelCast_c(pos, CanonicalTypeNode(pos, SJConstants.SJ_CHANNEL_TYPE), expr, tn);
		
		return n;
	}
	
	public SJSessionCast SJSessionCast(Position pos, Expr expr, SJTypeNode tn)
	{
		SJSessionCast n = new SJSessionCast_c(pos, CanonicalTypeNode(pos, SJConstants.SJ_SOCKET_INTERFACE_TYPE), expr, tn);
		
		return n;
	}
	
	public SJAmbiguousCast SJAmbiguousCast(Position pos, Expr expr, SJTypeNode tn)
	{
		SJAmbiguousCast n = new SJAmbiguousCast_c(pos, CanonicalTypeNode(pos, SJConstants.SJ_CHANNEL_SOCKET_HACK_TYPE), expr, tn);
		
		return n;
	}
	
	public SJChannelFormal SJChannelFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias) // Based on SJSessionFormal. 
	{
		SJChannelFormal n = new SJChannelFormal_c(pos, flags, CanonicalTypeNode(pos, SJConstants.SJ_CHANNEL_TYPE), name, tn);
		
		if (isNoalias) // Redundant: session variables are required by design to be noalias. Parser should enforce this.
		{
			n = (SJChannelFormal) n.type(convertToNoAliasTypeNode(n.type(), flags.isFinal()));
		}
		
		return n;
	}
	
	public SJSessionFormal SJSessionFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias) // Based on SJProtocolDecl. // The choice is between modifying the base types to signal noalias, or make a separate (sub)class for noalias session formals. Going with the former, as for SJProtocolDecls.
	{
		SJSessionFormal n = new SJSessionFormal_c(pos, flags, CanonicalTypeNode(pos, SJConstants.SJ_SOCKET_INTERFACE_TYPE), name, tn);
		
		if (isNoalias) // Redundant: session variables are required by design to be noalias. Parser should enforce this.
		{
			n = (SJSessionFormal) n.type(convertToNoAliasTypeNode(n.type(), flags.isFinal()));
		}
		
		return n;
	}
	
	/*public SJNoaliasFormal SJNoaliasFormal(Position pos, Flags flags, Id name, SJTypeNode tn)
	{
		SJNoaliasFormal n = new SJNoaliasFormal_c(pos, flags, CanonicalTypeNode(pos, SJConstants.SJ_SOCKET_INTERFACE_TYPE), name, tn);
		
		return n;
	}*/
	
	
	private NewArray makeSocketsArray(Position pos, int size)
	{
		CanonicalTypeNode base = CanonicalTypeNode(pos, SJ_SOCKET_INTERFACE_TYPE);
		
		List<Expr> dims = new LinkedList<Expr>();
		dims.add(IntLit(pos, IntLit.INT, size));
		
		return NewArray(pos, base, dims, 0, null); // Cannot add actual targets (sockets) to an ArrayInit until after disambiguation, when the targets are resolved from Receivers to Exprs.		
	}
	
	private TypeNode convertToNoAliasTypeNode(TypeNode tn, boolean isFinal)
	{		
		if (tn instanceof AmbTypeNode)
		{
			tn = SJAmbNoAliasTypeNode(tn.position(), (AmbTypeNode) tn);
		}
		else if (tn instanceof ArrayTypeNode) 
		{
			tn = SJNoAliasArrayTypeNode(tn.position(), (ArrayTypeNode) tn);
		}
		/*else
		{
			// CanonicalTypeNode won't be replaced by disambigation pass (so extension object won't be lost).
		}*/
		
		return (TypeNode) setSJNoAliasFinalExt(sjef, tn, true, isFinal); // Protocols are na-final.
	}
	
	/*private Flags makeFinal(Flags flags)
	{
		return (!flags.isFinal()) ? flags.Final() : flags;
	}*/	
}
