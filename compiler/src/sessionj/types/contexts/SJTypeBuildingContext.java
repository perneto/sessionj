/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import polyglot.types.*;

import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sesstry.*;
import sessionj.types.sesstypes.*;

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
	
	abstract public void recurseSessions(List<String> targetNames) throws SemanticException;
	
	abstract protected void pushContextElement(SJContextElement ce);
	abstract protected SJContextElement popContextElement() throws SemanticException;*/
		
	// The methods re-declared from SJContext need to be overridden.
	SJSessionType sessionRemaining(String sjname) throws SemanticException;
	
	void advanceSession(String sjname, SJSessionType st) throws SemanticException;
	SJSessionType delegateSession(String sjname) throws SemanticException;
	void recurseSessions(List<String> sjnames) throws SemanticException;
	
	void pushSJSessionTry(SJSessionTry st) throws SemanticException;
	void pushSJServerTry(SJServerTry st) throws SemanticException;
	
	void pushSJBranchOperation(SJBranchOperation b) throws SemanticException;
	void pushSJBranchCase(SJBranchCase bc) throws SemanticException;
	void pushSJWhile(SJWhile w) throws SemanticException;
	void pushSJRecursion(SJRecursion r) throws SemanticException;
	
	SJContextElement pop() throws SemanticException;
	
	void pushContextElement(SJContextElement ce);
	SJContextElement popContextElement() throws SemanticException;
	
	void checkSessionsCompleted() throws SemanticException;
	
	boolean inSJBranchCaseContext();
	boolean inSJSessionLoopContext();

    void pushSJTypecase(SJTypecase typecase) throws SemanticException;
}
