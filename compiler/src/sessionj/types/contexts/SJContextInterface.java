/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import polyglot.ast.ClassDecl;
import polyglot.ast.MethodDecl;
import polyglot.types.*;
import polyglot.visit.ContextVisitor;

import sessionj.ast.*;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sesstry.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;

/**
 * @author Raymond
 *
 */
public interface SJContextInterface
{	
	public void setVisitor(ContextVisitor cv);
	
	public SJSessionType findProtocol(String sjname) throws SemanticException;
	public SJSessionType findChannel(String sjname) throws SemanticException;
	public SJSessionType findSocket(String sjname) throws SemanticException;
	public SJSessionType findServer(String sjname) throws SemanticException;
	
	public void addChannel(SJNamedInstance ni);
	public void addSocket(SJNamedInstance ni);
	public void addServer(SJNamedInstance ni);
	public void addSession(SJNamedInstance ni);	
	
	public void openService(String sjname, SJSessionType st) throws SemanticException;
	public void openSession(String sjname, SJSessionType st) throws SemanticException;
	
	public void advanceSession(String sjname, SJSessionType st) throws SemanticException;
	public SJSessionType delegateSession(String sjname) throws SemanticException; // Maybe this operation should take the type as an argument instead of calculating it itself (which should be done by the equivalent in SJTypeBuildingContext).
	
	public SJSessionType sessionExpected(String sjname);
	public SJSessionType sessionImplemented(String sjname);
	public SJSessionType sessionRemaining(String sjname) throws SemanticException;
	
	public boolean serviceInScope(String sjname);
	public boolean serviceOpen(String sjname);
	public boolean sessionInScope(String sjname);	
	public boolean sessionActive(String sjname);
	
	//public void pushCode();
	public void pushBlock();	
	public void pushTry();
	public void pushBranch();
	public void pushLoop(); 
	//public void pushTry();
	public void pushMethodBody(MethodDecl md) throws SemanticException;
	
	public void pushSJSessionTry(SJSessionTry st) throws SemanticException;
	public void pushSJServerTry(SJServerTry st) throws SemanticException;
	
	public void pushSJBranchOperation(SJBranchOperation b) throws SemanticException;
	public void pushSJBranchCase(SJBranchCase bc) throws SemanticException;
	public void pushSJWhile(SJWhile w) throws SemanticException;
	public void pushSJRecursion(SJRecursion r) throws SemanticException;
	
	public SJContextElement pop() throws SemanticException;
	
	public void setSessionRequested(String sjname, SJSessionType st);
	public void setSessionActive(String sjname, SJSessionType st); // public so that noalias session method parameters can be initialised.
	public void setSessionImplemented(String sjname, SJSessionType st);
	
	public SJNamedInstance getChannel(String sjname) throws SemanticException;	
	public SJNamedInstance getSocket(String sjname) throws SemanticException;	
	public SJNamedInstance getServer(String sjname) throws SemanticException;
}
