package sessionj.ast;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.*;
import polyglot.util.*;

import sessionj.SJConstants;
import sessionj.ast.createops.*;
import sessionj.ast.noalias.*;
import sessionj.ast.protocoldecls.*;
import sessionj.ast.chanops.*;
import sessionj.ast.sesscasts.*;
import sessionj.ast.sessformals.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.servops.*;
import sessionj.ast.sesstry.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.typenodes.*;
//import sessionj.del.*;
import sessionj.extension.*;
import sessionj.types.SJTypeSystem;
import sessionj.util.SJLabel;

import static sessionj.SJConstants.*;

/**
 * NodeFactory for sessionj extension.
 */
public interface SJNodeFactory extends NodeFactory 
{
    // TODO: Declare any factory methods for new AST nodes.
	
	public void setExtensionInfo(ExtensionInfo extInfo); // For QQ.
	
	public SJExtFactory extFactory();
	//public SJDelFactory delFactory();

	public SJAmbNoAliasTypeNode SJAmbNoAliasTypeNode(Position pos, AmbTypeNode atn);
	public SJNoAliasArrayTypeNode SJNoAliasArrayTypeNode(Position pos, ArrayTypeNode atn);
	public SJNoAliasCanonicalTypeNode SJNoAliasCanonicalTypeNode(Position pos, CanonicalTypeNode ctn);
	
	public SJFieldProtocolDecl SJFieldProtocolDecl(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoAlias);
	public SJLocalProtocolDecl SJLocalProtocolDecl(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoAlias);
	
	public SJCBeginNode SJCBeginNode(Position pos);
	public SJSBeginNode SJSBeginNode(Position pos);
	public SJSendNode SJSendNode(Position pos, TypeNode messageType);
	public SJReceiveNode SJReceiveNode(Position pos, TypeNode messageType);
	public SJOutbranchNode SJOutbranchNode(Position pos, List<SJBranchCaseNode> branchCases);
	public SJInbranchNode SJInbranchNode(Position pos, List<SJBranchCaseNode> branchCases);
	public SJBranchCaseNode SJBranchCaseNode(Position pos, SJLabel lab, SJTypeNode body);	
	public SJOutwhileNode SJOutwhileNode(Position pos, SJTypeNode body);
	public SJInwhileNode SJInwhileNode(Position pos, SJTypeNode body);
	public SJRecursionNode SJRecursionNode(Position pos, SJLabel lab, SJTypeNode body);
	public SJRecurseNode SJRecurseNode(Position pos, SJLabel lab);
	public SJProtocolRefNode SJProtocolRefNode(Position pos, Receiver target);	
	public SJProtocolDualNode SJProtocolDualNode(Position pos, Receiver target);
	
	public SJChannelCreate SJChannelCreate(Position pos, List arguments);
	public SJSocketCreate SJSocketCreate(Position pos, List arguments);
	public SJServerCreate SJServerCreate(Position pos, List arguments);
	
	public SJLocalChannel SJLocalChannel(Position pos, Id name);
	public SJLocalSocket SJLocalSocket(Position pos, Id name);
	public SJLocalServer SJLocalServer(Position pos, Id name);
	
	public SJAmbiguousTry SJAmbiguousTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets);
	public SJSessionTry SJSessionTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets);
	public SJServerTry SJServerTry(Position pos, Block tryBlock, List catchBlocks, Block finallyBlock, List targets);
	
	public SJRequest SJRequest(Position pos, Receiver target, List arguments);
	public SJSend SJSend(Position pos, List arguments, List targets);
	public SJPass SJPass(Position pos, List arguments, List targets);
	public SJCopy SJCopy(Position pos, List arguments, List targets);
	public SJReceive SJReceive(Position pos, List arguments, List targets);
	public SJReceive SJReceiveInt(Position pos, List arguments, List targets);
	public SJReceive SJReceiveBoolean(Position pos, List arguments, List targets);
	public SJReceive SJReceiveDouble(Position pos, List arguments, List targets);
	public SJRecurse SJRecurse(Position pos, SJLabel lab, List targets);
	
	public SJSpawn SJSpawn(Position pos, New w, List targets);
	
	public SJOutlabel SJOutlabel(Position pos, SJLabel lab, List targets);
	public SJInlabel SJInlabel(Position pos, List arguments, List targets);
	public SJOutsync SJOutsync(Position pos, List arguments, List targets); // No need to be public?
	public SJInsync SJInsync(Position pos, List arguments, List targets);
	public SJRecursionEnter SJRecursionEnter(Position pos, List targets);
	public SJRecursionExit SJRecursionExit(Position pos, List targets);
	
	public SJOutbranch SJOutbranch(Position pos, List stmts, SJLabel lab, List targets);
	public SJInbranch SJInbranch(Position pos, List arguments, List<SJInbranchCase> branchCases, List targets);
	public SJInbranchCase SJInbranchCase(Position pos, List stmts, SJLabel lab);
	public SJOutwhile SJOutwhile(Position pos, List arguments, Stmt body, List targets);
	public SJOutInwhile SJOutInwhile(Position pos, Stmt body, List targets);
	public SJInwhile SJInwhile(Position pos, List arguments, Stmt body, List targets);
	//public SJRecursion SJRecursion(SJTypeSystem ts, Position pos, List stmts, SJLabel lab, List targets);
	public SJRecursion SJRecursion(Position pos, Block body, SJLabel lab, List targets);
	
	public SJAccept SJAccept(Position pos, Receiver target, List arguments);
	
	public SJChannelCast SJChannelCast(Position pos, Expr expr, SJTypeNode tn);
	public SJSessionCast SJSessionCast(Position pos, Expr expr, SJTypeNode tn);
	public SJAmbiguousCast SJAmbiguousCast(Position pos, Expr expr, SJTypeNode tn);
	
	public SJChannelFormal SJChannelFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias);
	public SJSessionFormal SJSessionFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias);
}
