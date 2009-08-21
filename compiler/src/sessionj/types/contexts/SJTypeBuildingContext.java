/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import polyglot.ast.ClassDecl;
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
public interface SJTypeBuildingContext extends SJContextInterface
{
	/*public SJTypeBuildingContext(ContextVisitor cv)
	{
		super(cv);
	}
	
	abstract public void recurseSessions(List<String> sjnames) throws SemanticException;
	
	abstract protected void pushContextElement(SJContextElement ce);
	abstract protected SJContextElement popContextElement() throws SemanticException;*/
		
	// The methods re-declared from SJContext need to be overridden.
	public SJSessionType sessionRemaining(String sjname) throws SemanticException;
	
	public void advanceSession(String sjname, SJSessionType st) throws SemanticException;
	public SJSessionType delegateSession(String sjname) throws SemanticException;
	public void recurseSessions(List<String> sjnames) throws SemanticException;
	
	public void pushSJSessionTry(SJSessionTry st) throws SemanticException; 
	public void pushSJServerTry(SJServerTry st) throws SemanticException;
	
	public void pushSJBranchOperation(SJBranchOperation b) throws SemanticException;
	public void pushSJBranchCase(SJBranchCase bc) throws SemanticException;
	public void pushSJWhile(SJWhile w) throws SemanticException;
	public void pushSJRecursion(SJRecursion r) throws SemanticException;
	
	public SJContextElement pop() throws SemanticException;
	
	public void pushContextElement(SJContextElement ce);
	public SJContextElement popContextElement() throws SemanticException;
	
	public void checkSessionsCompleted() throws SemanticException;
	
	public boolean inSJBranchCaseContext();
	public boolean inSJSessionLoopContext();
}
