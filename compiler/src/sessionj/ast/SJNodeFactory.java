package sessionj.ast;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Flags;
import polyglot.util.Position;
import sessionj.ast.chanops.SJRequest;
import sessionj.ast.createops.SJChannelCreate;
import sessionj.ast.createops.SJServerCreate;
import sessionj.ast.createops.SJSocketCreate;
import sessionj.ast.noalias.SJAmbNoAliasTypeNode;
import sessionj.ast.noalias.SJNoAliasArrayTypeNode;
import sessionj.ast.noalias.SJNoAliasCanonicalTypeNode;
import sessionj.ast.protocoldecls.SJFieldProtocolDecl;
import sessionj.ast.protocoldecls.SJLocalProtocolDecl;
import sessionj.ast.servops.SJAccept;
import sessionj.ast.sesscasts.SJAmbiguousCast;
import sessionj.ast.sesscasts.SJChannelCast;
import sessionj.ast.sesscasts.SJSessionCast;
import sessionj.ast.sessformals.SJChannelFormal;
import sessionj.ast.sessformals.SJSessionFormal;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sesstry.SJAmbiguousTry;
import sessionj.ast.sesstry.SJServerTry;
import sessionj.ast.sesstry.SJSessionTry;
import sessionj.ast.sessvars.SJLocalChannel;
import sessionj.ast.sessvars.SJLocalServer;
import sessionj.ast.sessvars.SJLocalSocket;
import sessionj.ast.typenodes.*;
import sessionj.extension.SJExtFactory;
import sessionj.util.SJLabel;

import java.util.List;

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
	public SJReceive SJReceive(Position pos, List<Expr> arguments, List targets);
	public SJReceive SJReceiveInt(Position pos, List<Expr> arguments, List targets);
	public SJReceive SJReceiveBoolean(Position pos, List<Expr> arguments, List targets);
	public SJReceive SJReceiveDouble(Position pos, List<Expr> arguments, List targets);
	public SJRecurse SJRecurse(Position pos, SJLabel lab, List targets);
	
	public SJSpawn SJSpawn(Position pos, New w, List targets);
	
	public SJOutlabel SJOutlabel(Position pos, SJLabel lab, List targets);
	public SJInlabel SJInlabel(Position pos, List arguments, List targets);
	public SJRecursionEnter SJRecursionEnter(Position pos, List targets);
	public SJRecursionExit SJRecursionExit(Position pos, List targets);
	
	public SJOutbranch SJOutbranch(Position pos, List<Stmt> stmts, SJLabel lab, List<Receiver> targets);
	public SJInbranch SJInbranch(Position pos, List arguments, List<SJInbranchCase> branchCases, List targets);
	public SJInbranchCase SJInbranchCase(Position pos, List stmts, SJLabel lab);
	public SJOutwhile SJOutwhile(Position pos, Expr condition, Stmt body, List targets);
	public SJOutInwhile SJOutInwhile(Position pos, Stmt body, List<Receiver> sources, List<Receiver> targets, Expr condition);
	public SJInwhile SJInwhile(Position pos, Stmt body, List targets);
	//public SJRecursion SJRecursion(SJTypeSystem ts, Position pos, List stmts, SJLabel lab, List targets);
	public SJRecursion SJRecursion(Position pos, Block body, SJLabel lab, List targets);
	
	public SJAccept SJAccept(Position pos, Receiver target, List arguments);
	
	public SJChannelCast SJChannelCast(Position pos, Expr expr, SJTypeNode tn);
	public SJSessionCast SJSessionCast(Position pos, Expr expr, SJTypeNode tn);
	public SJAmbiguousCast SJAmbiguousCast(Position pos, Expr expr, SJTypeNode tn);
	
	public SJChannelFormal SJChannelFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias);
	public SJSessionFormal SJSessionFormal(Position pos, Flags flags, Id name, SJTypeNode tn, boolean isNoalias);

    /*private Flags makeFinal(Flags flags)
{
return (!flags.isFinal()) ? flags.Final() : flags;
}*/
    NewArray makeSocketsArray(Position pos, int size);
}
