/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import polyglot.types.*;
import polyglot.visit.ContextVisitor;

import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sesstry.*;
import sessionj.ast.sessvars.*;
import sessionj.types.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;
import sessionj.util.*;

import static sessionj.util.SJCompilerUtils.*;

/**
 * @author Raymond
 *
 */
public class SJTypeBuildingContext_c extends SJContext_c implements SJTypeBuildingContext // Currently the "main" session context manager (used by SJSessionTypeChecker).  
{
	private final SJTypeSystem sjts;
	
	public SJTypeBuildingContext_c(ContextVisitor cv, SJTypeSystem sjts)
	{
		super(cv, sjts);
		
		this.sjts = sjts;
	}
	
	public void advanceSession(String sjname, SJSessionType st) throws SemanticException // st should be single type object.
	{
		if (currentContext() instanceof SJBranchContext)
        // Hacky? This is here because branch contexts are a kind of meta context,
        // should never be updated by session implementations directly. Prevents inline if-statements.
		{
			throw new SemanticException("[SJTypeBuildingContext_c] Unsupported branch context for session implementation: " + sjname);			
		}
		
		if (st != null && !(st.child() == null))
		{
			throw new RuntimeException("[SJContent_c] Shouldn't get in here.");
		}

		super.advanceSession(sjname, st);
	}
	
	public SJSessionType delegateSession(String sjname) throws SemanticException // Could be called completeSession (also used for session method argument passing).
	{
		SJSessionType remaining = sessionRemaining(sjname);
		
		if (remaining == null)
		{
			throw new SemanticException("[SJTypeBuildingContext_c] Cannot delegate a completed session: " + sjname);
		}
		
		//SJSessionType bar = remaining; // Will be the "difference" between remaining and innermostRemaining.
		
		//for (SJSessionType st = innermostRemaining; st != null; st = st.child())
		{
			/*if (expectedSessionOperation(sjname) == null) // There's a similar routine in popContextElement. We're basically "carrying over" the remainder of the complete session implementation to the outer scopes.
			{
				SJSessionType foo = sessionImplemented(sjname);
				
				setSessionImplemented(sjname, (foo == null) ? st : foo.append(st)); // Doesn't work: implementation that should be carried over outside of a compound operation, e.g. outbranch, gets sucked inside the operation.
				
				break;
			}*/
			
			//advanceSession(sjname, st.nodeClone());
			
			//bar = bar.child();
		}
		
		// FIXME: if remaining is a recurse type, unfold it back to the recursion type.
		
		SJSessionType foo = sessionImplemented(sjname);		
		SJSessionType bar = sjts.SJDelegatedType(remaining);
		
		setSessionActive(sjname, null);
		setSessionImplemented(sjname, foo == null ? bar : foo.append(bar));
		
		/*for (int i = contexts().size() - 2; i >= 0; i--) // Doesn't work for inner nested branches.
		{
			SJContextElement ce = contexts().get(i);
			
			if (!ce.sessionActive(sjname))
			{			
				ce.setImplementedOverflow(sjname, bar);
				
				break;
			}
		}*/
		
		/*int i = contexts().size() - 1; // Also tried to manually sort out the context stack here for the delegated session, but it didn't work. 
		
		SJContextElement ce = contexts().get(i--);
		
		SJSessionType active = ce.getActive(sjname);
		SJSessionType foo = null;
		
		for (SJSessionType st = remaining; st != null; st = st.child()) // Hacky? Alternative would be to propagate a "delegated" signal down the context stack (through to outer scopes).			
		{
			while (active == null)
			{
				ce = contexts().get(i--); 
				
				active = ce.getActive(sjname);
			}
			
			if (ce == currentContext())
			{
				advanceSession(sjname, st.nodeClone());
				
				foo = st.nodeClone();
			}
			else
			{
				//SJSessionType active = ce.getActive(sjname); // Duplicated from advanceSession.			
				SJSessionType implemented = ce.getImplemented(sjname);

				System.out.println("c: " + sjname + ", " + active + ", " + implemented + ", " + st);
				
				ce.setActive(sjname, active.child());
				ce.setImplemented(sjname, (implemented == null) ? st : implemented.append(st));
			}
		}*/
				
		return remaining;
	}
	
	public void recurseSessions(List<String> sjnames) throws SemanticException
	{
		for (SJContextElement ce : contexts())
		{
			if (ce instanceof SJSessionRecursionContext)
			{
				List<String> targets = ((SJSessionRecursionContext) ce).targets();
				
				for (String sjname : sjnames)
				{
					if (targets.contains(sjname))
					{
						if (!targets.equals(sjnames))
						{
							throw new SemanticException("[SJTypeBuildingContext_c] recurse does not match recursion target: " + targets);				
						}
						
						return;
					}
				}
			}
		}				
	}
	
	/*public SJSessionType expectedSessionOperation(String sjname)
	{	
		return currentContext().getActive(sjname);
	}
	
	public SJSessionType sessionImplemented(String sjname)
	{
		return currentContext().getImplemented(sjname);
	}*/
	
	public SJSessionType sessionRemaining(String sjname) throws SemanticException
	{
		SJSessionType remaining = null;
		SJSessionType innermostRemaining = null;
		
		for (SJContextElement ce : contexts()) // Starts from bottom of the stack (outermost context). We take the maximum possible active type (remaining session to be implemented) from the outermost scope, and take away the bits we've found have implemented as we move through the inner scopes. Remember, nesting of active types should only come from the first elements at each level being an open branch scope - delegation within loops not allowed.
		//for (int i = 0; i < contexts().size(); i++)
		{		
			//SJContextElement ce = contexts().get(i);
			
			if (remaining == null)
			{
				if (ce.sessionActive(sjname))
				{	
					remaining = ce.getActive(sjname);
					innermostRemaining = remaining;
				}
			}
			else
			{ 
				if (ce instanceof SJLoopContext)
				{
					throw new SemanticException("[SJTypeBuildingContext_c] Cannot delegate session within loop context: " + sjname);
				}
				else if (ce instanceof SJBranchCaseContext)
				{
					if (!((SJBranchCaseContext) ce).isTerminal(sjname)) // Currently a bit of a work around. Can we avoid this restriction?
					{
						throw new SemanticException("[SJTypeBuildingContext_c] Cannot delegate session within non-terminal branch case: " + sjname);
					}
					
					/*for (int j = i - 1; j >= 0; j--) // Doesn't work: manually updating the context stack doesn't work, we will damage or remove information that might be needed for subsequent branch cases. The best bet seems to be propagating an explicit delegated/completed signal. For now, using an implemented "overflow" hack.
					{
						SJContextElement foo = contexts().get(j);
						
						if (foo.sessionActive(sjname)) // HACK: we're just erasing the remainder of the active session past the first branch element. It was hard to carry this information over for the existing context pop mechanism to use (it gets sucked inside the implementation of the branch instead of following the branch), and hard to update those outer contexts right now (we need to do it after the context pop update).
						{
							foo.setActive(sjname, foo.getActive(sjname).nodeClone()); // Will redundantly go up the context stack for each nested branch scope. 
						}						
					}*/
					
					SJSessionType child = remaining.child();
					
					remaining = ((SJBranchType) remaining).branchCase(((SJBranchCaseContext) ce).label()); 
					innermostRemaining = remaining;
					
					remaining = remaining.append(child); // FIXME: gets the full remainder of the session for completion. But will break the advanceSession routine below if we're currently inside an inner scope that only has a fragment of the remainder as the active type, e.g. if we're delegating a session from within a branch on that session and there are operations after the branch. 
				}
				//else 
				{					
					SJSessionType implemented = ce.getImplemented(sjname);
					
					for ( ; implemented != null; implemented = implemented.child())
					{
						remaining = remaining.child();
						innermostRemaining = innermostRemaining.child();
					}
				}
			}
		}
		
		return remaining;
	}
	
	/*public boolean serviceInScope(String sjname)
	{
		return currentContext().serviceInScope(sjname);
	}
	
	public boolean serviceOpen(String sjname)
	{
		return currentContext().serviceOpen(sjname);
	}
	
	public boolean sessionInScope(String sjname)
	{
		return currentContext().sessionInScope(sjname);
	}
	
	public boolean sessionActive(String sjname)
	{
		return currentContext().sessionActive(sjname);
	}
	
	//public void pushCode();
	
	public void pushBlock()
	{
		pushContextElement(new SJContextElement_c(currentContext()));
	}
	
	public void pushBranch()
	{
		pushContextElement(new SJBranchContext_c(currentContext()));
	}
	
	public void pushLoop()
	{	
		SJLoopContext lc = new SJLoopContext_c(currentContext());
		
		//l.clearSockets(); // Prevent session being reopened whilst it could already be open.
		lc.clearSessions();	
		
		pushContextElement(lc);
	}
	
	public void pushTry()
	{
		SJTryContext tc = new SJTryContext_c(currentContext());
		
		tc.clearSessions();
		
		pushContextElement(tc);
	}
	
	public void pushMethodBody(MethodDecl md) throws SemanticException
	{
		MethodInstance mi = md.methodInstance(); 
		
		if (mi instanceof SJMethodInstance)
		{	
			SJMethodInstance sjmi = (SJMethodInstance) mi;
			
			Iterator i = md.formals().iterator();				
			
			for (Type t : sjmi.sessionFormalTypes())
			{
				Formal f = (Formal) i.next();
				
				if (t instanceof SJSessionType)
				{
					String sjname = ((SJFormal) f).name();
					
					if (f.flags().isFinal())
					{												
						openSession(sjname, (SJSessionType) t);
					}
					else
					{							
						setSessionActive(sjname, (SJSessionType) t);
						// Maybe should set session implemented as well (to null).
					}
				}
			}
		}
	}*/
	
	public void pushSJSessionTry(SJSessionTry st) throws SemanticException
	{
		SJSessionTryContext tc = new SJSessionTryContext_c(currentContext());
		
		//tc.clearSockets(); // Prevent session being reopened whilst it could already be open. // Instead check if session is alive. 
		tc.clearSessions();
		
		List<String> sjnames = new LinkedList<String>();
		
		for (Iterator i = st.targets().iterator(); i.hasNext(); )
		{
			//String sjname = getSocket(((SJSocketVariable) i.next()).sjname()).sjname(); // Checks that the socket is in scope.
			String sjname = ((SJSocketVariable) i.next()).sjname();
			
			for (int v = contexts().size() - 1; v >= 0; v--)
			{
				SJContextElement ce = contexts().get(v);
	
				if (ce.sessionActive(sjname)) // "Pushing" a session into an inner scope.
				{				
					if (ce.sessionInScope(sjname))
					{
						tc.setSession(sjname, ce.getActive(sjname));
						
						break;
					}					
					else if (ce instanceof SJLoopContext)
					{
						throw new SemanticException("[SJTypeBuildingContext_c] Cannot re-enter session context within a loop context: " + sjname);
					}
					else
					{
						try
						{
							findSocket(sjname); // Hacky? We're using the sockets to identify whether the session is one that has already been opened and has been passsed as a method argument. Will also use this to work out whether a session parameter needs to be closed or not (i.e. if it's final).
						}
						catch (SemanticException se) // noalias session method parameters.						 
						{
							tc.setSession(sjname, ce.getActive(sjname));
							
							break;
						}
					}
				}											
				else
				{
					SJNamedInstance ni = getSocket(sjname); // Socket is in context.
					
					tc.setSession(sjname, ni.sessionType()); // Should be SJUnknownType.
					
					break;
				}
			}
			
			sjnames.add(sjname);
		}
		
		tc.setSessions(sjnames);
		
		pushContextElement(tc);
	}
	
	public void pushSJServerTry(SJServerTry st) throws SemanticException
	{
		SJServerTryContext tc = new SJServerTryContext_c(currentContext());
		 
		tc.clearSessions(); // No need to clear services.
		
		List<String> sjnames = new LinkedList<String>();

        for (Object o : st.targets()) {
            String sjname = ((SJVariable) o).sjname();

            if (tc.serviceInScope(sjname)) {
                throw new SemanticException("[SJTypeBuildingContext_c] server-try already declared: " + sjname);
            }

            tc.setService(sjname, getServer(sjname).sessionType()); // Should be SJUnknownType.

            sjnames.add(sjname);
        }
		
		tc.setServers(sjnames);
		
		pushContextElement(tc);		
	}

    public void pushSJTypecase(SJTypecase typecase) throws SemanticException {
        SJContextElement current = currentContext();
        List<String> sjnames = getTargetNames(typecase);
        assert sjnames.size() == 1;

        String sjname = sjnames.get(0);
        current.checkActiveSessionStartsWith
            (sjname, SJSetType.class, "[SJTypeBuildingContext_c] found typecase, but expected: ");

        pushContextElement(new SJTypecaseContext(current, typecase, sjname));
    }

    public void pushSJBranchOperation(SJBranchOperation b) throws SemanticException // SJInbranch.
	{
		assert !(b instanceof SJOutbranch) : "Should call pushSJBranchCase instead";
        
		SJContextElement current = currentContext();
		
		List<String> sjnames = getTargetNames(b);

        assert sjnames.size() == 1 : "Session branch operations should only have a single target.";
		for (String sjname : sjnames)
		{
			current.checkActiveSessionStartsWith
                (sjname, SJInbranchType.class, "[SJTypeBuildingContext_c] found inbranch, but expected: ");
            // Maybe better to explicitly check session is active (open) as well.
		}
		
		pushContextElement(new SJSessionBranchContext_c(current, b, sjnames));
	}

    public void pushSJBranchCase(SJBranchCase bc) throws SemanticException
	{
		SJContextElement current = currentContext();				
		SJLabel lab = bc.label();
		
		if (bc instanceof SJOutbranch)
		{
			SJOutbranch ob = (SJOutbranch) bc;
			
			pushContextElement(new SJOutbranchContext_c(current, ob, getTargetNames(ob), lab));
			
			for (String sjname : getTargetNames(ob))
			{
				SJSessionType st = current.getActive(sjname);

                current.checkActiveSessionStartsWith
                    (sjname, SJOutbranchType.class, "[SJTypeBuildingContext_c] found outbranch, but expected: ");
				
				SJBranchType obt = (SJBranchType) st;
				
				if (!obt.hasCase(lab))
				{
					throw new SemanticException("[SJTypeBuildingContext_c] unexpected label: " + lab);
				}
				
				openSession(sjname, obt.branchCase(lab));
				
				if (st.child() == null)
				{
					((SJBranchCaseContext) currentContext()).addTerminal(sjname);
				}			
			}
		}
		else //if (bc instanceof SJInbranchCase)
		{
			pushContextElement(new SJBranchCaseContext_c(current, lab));			
			
			for (String sjname : ((SJSessionContext) current).targets()) // Should only be a single target.
			{
				SJSessionType st = current.getActive(sjname);

                current.checkActiveSessionStartsWith
                    (sjname, SJInbranchType.class, "[SJTypeBuildingContext_c] found inbranch, but expected: ");
				
				SJInbranchType ibt = (SJInbranchType) st;
				
				if (!ibt.hasCase(lab))
				{
					throw new SemanticException("[SJTypeBuildingContext_c] unexpected label: " + lab);
				}				
				
				openSession(sjname, ibt.branchCase(lab));
				
				if (st.child() == null)
				{
					((SJBranchCaseContext) currentContext()).addTerminal(sjname);
				}
			}			
		} 			
	}
	
	public void pushSJWhile(SJWhile w) throws SemanticException
	{
		SJContextElement current = currentContext();
		List<String> sjnames = getSJSessionOperationExt(w).targetNames();
		SJSessionLoopContext slc = new SJSessionLoopContext_c(current, w, sjnames);
		
		slc.clearSessions();
		
		pushContextElement(slc);
		
		for (String sjname : sjnames)
		{
			SJSessionType st = current.getActive(sjname);
			
			if (w instanceof SJOutwhile) 
			{
				if (st == null || !st.startsWith(SJOutwhileType.class))
				{
					throw new SemanticException("[SJTypeBuildingContext_c] found outwhile, but expected: " + st);
				}
			}
			else if (w instanceof SJOutInwhile)
			{
				Collection<String> sourceNames = new LinkedList<String>();
                for (Object r : ((SJOutInwhile) w).insyncSources())
                    sourceNames.add(((SJVariable) r).sjname());
                
                if (sourceNames.contains(sjname))
				{
					if (!st.startsWith(SJInwhileType.class))
					{
						throw new SemanticException("[SJTypeBuildingContext_c] found inwhile, but expected: " + st);
					} 
					
				}
				else
				{
					if (!st.startsWith(SJOutwhileType.class))
					{
						throw new SemanticException("[SJTypeBuildingContext_c] found outwhile, but expected: " + st);
					}					
				}
			}
			else //if (w instanceof SJInwhile)
			{
				if (st == null || !st.startsWith(SJInwhileType.class))
				{
					throw new SemanticException("[SJTypeBuildingContext_c] found inwhile, but expected: " + st);
				}
			}
			
			openSession(sjname, ((SJLoopType) st).body());
		}
	}
	
	public void pushSJRecursion(SJRecursion r) throws SemanticException
	{
		SJContextElement current = currentContext();
		List<String> sjnames = getSJSessionOperationExt(r).targetNames();
		
		SJContextElement slc = new SJSessionRecursionContext_c(current, r, sjnames);
		
		slc.clearSessions();
		
		pushContextElement(slc);
		
		for (String sjname : sjnames)
		{
			SJSessionType st = current.getActive(sjname);
			
			if (!st.startsWith(SJRecursionType.class))
			{
				throw new SemanticException("[SJTypeBuildingContext_c] found recursion, but expected: " + st);
			}
			
			SJLabel lab = r.label();
			
			if (!lab.equals(((SJRecursionType) st).label()))
			{
				throw new SemanticException("[SJTypeBuildingContext_c] unexpected recursion label: " + lab);
			}
			
			openSession(sjname, ((SJLoopType) st).body());
		}
	}
	
	public SJContextElement pop() throws SemanticException
	{
		return popContextElement();				
	}
	
	public void pushContextElement(SJContextElement ce)
	{
		contexts().push(ce);		
	}
	
	private boolean subsumeBranchSession(SJContextElement foo, SJContextElement bar, String sjname) throws SemanticException
	{
		boolean hasSessionImplementations = false;
		
		SJSessionType fimplemented = foo.getImplemented(sjname);
		SJSessionType bimplemented = bar.getImplemented(sjname);
		
		if (fimplemented == null)
		{
			if (bimplemented != null)
			{
				throw new SemanticException("[SJTypeBuildingContext_c] Incompatible branch: " + sjname);
			}
		}
		else
		{
			if (bimplemented == null) 
			{
				throw new SemanticException("[SJTypeBuildingContext_c] Incompatible branch: " + sjname);
			}
			else
			{								
				foo.setImplemented(sjname, fimplemented.subsume(bimplemented));
				
				hasSessionImplementations = true;
			}						
		}
		
		return hasSessionImplementations;
	}
	
	public SJContextElement popContextElement() throws SemanticException
	{
		SJContextElement poppedContext = contexts().pop();

        popBranchContext(poppedContext);

        // Until here, just sorting out popped branch (and inbranch) contexts (i.e. the branch case contexts ces for branch context poppedContext).
		
		if (!contexts().isEmpty())
		{
			SJContextElement current = currentContext();

			if (current instanceof SJBranchContext) // Now sorting out popped branch case contexts (ces merged into poppedContext) for the current branch context.
            // FIXME: sort out channels as well.
            // (Now not necessary? Since channels must be na-final, so cannot assign different
            // values across branches, or even use after being received in a branch - but this may be too restrictive).  
			{
                pullUpBranchContext(poppedContext, current);
			}
			else
			{			
				for (String sjname : poppedContext.channelSet())
				{
					SJSessionType st = poppedContext.getChannel(sjname).sessionType();
					SJLocalChannelInstance ni = (SJLocalChannelInstance) current.getChannel(sjname);
					
					if (ni != null && ni.sessionType() instanceof SJUnknownType)
					{
						current.setChannel(sjts.SJLocalChannelInstance(ni, st, sjname));
					}
				}
				
				for (String sjname : poppedContext.activeSessions())
				{				
					SJSessionType implemented = poppedContext.getImplemented(sjname);
					
					if (current.sessionActive(sjname))
                    // Can be not in scope but still active for e.g. try (s) { try { try (s) { ... 
					{
						if (poppedContext instanceof SJSessionTryContext) // FIXME: make SJSessionTryContext a SJSessionContext.
						{
                            handleSessionTryContext(poppedContext, sjname);
                        }
						
						if (poppedContext instanceof SJSessionContext)
						{																				
							SJSessionContext sc = (SJSessionContext) poppedContext;
							
							if (sc.targets().contains(sjname)) // Need to build back the type structure that was stripped when the compound operation context was entered.
							{							
								SJSessionType expected = poppedContext.getActive(sjname);
								
								if (expected != null && isNotTypecaseSet(poppedContext, expected)) // Maybe can be better factored out with above check for branch contexts and below check.
								{
                                    // No need to check for delegated types here because delegation within a branch must be terminal (and delegateSession clears the active type).
                                    throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [4], expected: " + expected);
                                }

                                if (poppedContext instanceof SJSessionBranchContext)
								{
									// SJInbranch (and SJInbranchCase) done above with branch merging.
								}
								else if (poppedContext instanceof SJOutbranchContext)
								{
                                    implemented = handleOutbranchContext(poppedContext, implemented);
								}
								else if (poppedContext instanceof SJSessionLoopContext)
								{
                                    implemented = handleSessionLoopContext(sjname, implemented, ((SJSessionContext) poppedContext).node());
                                }
                                else if (poppedContext instanceof SJTypecaseContext)
                                {
                                    implemented = ((SJTypecaseContext) poppedContext).getActiveSetType();
                                }
								else 
								{
									throw new SemanticException("[SJTypeBuildingContext_c] Session context not yet supported: " + poppedContext);
								}
								
								poppedContext.setImplemented(sjname, implemented);
                                // So that the returned context contains the right session type,
                                // used in SJSessionTypeChecker.leaveCall   
							}				
						}
                        else if (current instanceof SJTypecaseContext) {
                            implemented = null; // TODO: hack, to avoid advance session
                            // as long as we've not exited the typecase
                        }

                        advanceSessionWithImplementedPart(sjname, implemented);
                    }
					else // Session from popped context is not active in current context - the session must have been completed.  
					{						
						SJSessionType expected = poppedContext.getActive(sjname);
						/*SJSessionType overflow = currentContext().getImplementedOverflow(sjname);
						
						while (expected != null && overflow != null)
						{
							expected = expected.child();
							overflow = overflow.child();
						}												
						
						if (overflow != null)
						{
							throw new SJRuntimeException("[SJTypeBuildingContext_c] Shouldn't get here: " + overflow);
						}
						
						currentContext().removeImplementedOverflow(sjname);*/ 
						
						if (expected != null)
						{
							// No need to check for delegated types here because delegation within a branch must be terminal (and delegateSession clears the active type).
                            throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [5], expected: " + expected);
                        }
					}
				}			
			}		
		}
		
		return poppedContext;
	}

    private boolean isNotTypecaseSet(SJContextElement poppedContext, SJSessionType expected) {
        return !(poppedContext instanceof SJTypecaseContext && expected instanceof SJSetType);
    }

    private void pullUpBranchContext(SJContextElement ce, SJContextElement current) throws SemanticException {
        for (String sjname : ce.activeSessions())
        {
            if (!current.sessionActive(sjname)) // Session was opened within the branch. // FIXME: make SJSessionTryContext a SJSessionContext.
            {
                SJSessionType expected = ce.getActive(sjname);

                if (expected != null)
                {
                    throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [1], expected: " + expected);
                }
            }

            if (ce instanceof SJSessionContext && ((SJSessionContext) ce).targets().contains(sjname))
            {
                SJSessionType expected = ce.getActive(sjname);

                if (expected != null)
                {
                    throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [2], expected: " + expected);
                }
            }

            if (ce instanceof SJSessionTryContext && ((SJSessionTryContext) ce).getSessions().contains(sjname)) // Can be factored out if session-try contexts are unified with regular session contexts.
            {
                SJSessionType expected = ce.getActive(sjname);

                if (expected != null)
                {
                    throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [3], expected: " + expected);
                }
            }
        }

        ((SJBranchContext) current).addBranch(ce);
    }

    private void advanceSessionWithImplementedPart(String sjname, SJSessionType implemented) throws SemanticException {
        while (implemented != null) {
            /*if (expectedSessionOperation(sjname) == null) // Hacky? Early session completion (e.g. delegation, method passing) will give the full remaining type as implemented.
            {
                SJSessionType foo = sessionImplemented(sjname);

                setSessionImplemented(sjname, (foo == null) ? implemented : foo.append(implemented)); // Doesn't work: see delegateSession.

                break;
            }
            else*/
            advanceSession(sjname, implemented.nodeClone()); // Already type checked within the inner block.
            implemented = implemented.child();
        }
    }

    private SJSessionType handleOutbranchContext(SJContextElement ce, SJSessionType implemented) {
        SJSessionType implementedOrig = implemented;

        implemented = sjts.SJOutbranchType().branchCase(((SJOutbranchContext) ce).label(), implemented);

        if (implementedOrig != null && implementedOrig.getLeaf() instanceof SJDelegatedType)
        {
            implemented = sjts.SJDelegatedType(implemented); // Strictly speaking, that is not the type we are delegating - but it is the type implemented. Needs to be this way for branch subsumption.
        }
        return implemented;
    }

    private SJSessionType handleSessionLoopContext(String sjname, SJSessionType implemented, SJCompoundOperation co) {
        if (co instanceof SJWhile) // FIXME: should really differentiate the contexts instead (as for SJOutbranch).
        {
            if (co instanceof SJOutwhile)
            {
                implemented = sjts.SJOutwhileType(implemented);
            }
            else if (co instanceof SJOutInwhile) // FIXME: hacky.
            {
                if (((SJVariable) ((SJOutInwhile) co).targets().get(0)).sjname().equals(sjname))
                {
                    implemented = sjts.SJInwhileType(implemented);
                }
                else
                {
                    implemented = sjts.SJOutwhileType(implemented);
                }
            }
            else
            {
                implemented = sjts.SJInwhileType(implemented);
            }
        }
        else //if (co instanceof SJRecursion)
        {
            implemented = sjts.SJRecursionType(((SJRecursion) co).label()).body(implemented);
        }
        return implemented;
    }

    private void handleSessionTryContext(SJContextElement ce, String sjname) throws SemanticException {
        if (((SJSessionTryContext) ce).getSessions().contains(sjname))
        {
            SJSessionType expected = ce.getActive(sjname);

            if (expected != null) // Maybe can be better factored out with above check for branch contexts and below check.
            {
                //if (!(implemented != null && implemented.getLeaf() instanceof SJDelegatedType)) // Not needed now because delegation within a branch must be terminal (and delegateSession clears the active type).
                {
                    throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [6], expected: " + expected);
                }
            }
        }
    }

    private void popBranchContext(SJContextElement ce) throws SemanticException {
        if (ce instanceof SJBranchContext) // Merge branches. (Does not include SJOutbranch, but does include If.)
        {
            List<SJContextElement> ces = ((SJBranchContext) ce).branches();

            if (ces.isEmpty())
            // Hacky? This means we're popping an inline if-statement. Inline loop statements are checked OK
            // because of their session context properties - no operations on outer sessions allowed by typing.
            {
                // Nothing to do. ce should not contain any session implementations -
                // should be prevented by advanceSession (maybe not the best place to do that).
            }
            else
            {
                Map<String, SJSessionType> inbranches = new HashMap<String, SJSessionType>();
                Set<String> delegated = null;

                if (ce instanceof SJSessionBranchContext)
                {
                    delegated = checkBranches(ce, ces, inbranches);
                }

                for (String m : delegated)
                {
                    inbranches.put(m, sjts.SJDelegatedType(inbranches.get(m)));
                }

                SJContextElement firstBranch = ces.remove(0); // Going to use the first one to hold subsumption results.

                boolean hasSessionImplementations = false;

                for (String sjname : firstBranch.activeSessions())
                {
                    SJSessionType orig = ce.getImplemented(sjname);
                    SJSessionType inner = firstBranch.getImplemented(sjname);

                    if (orig == null)
                    {
                        if (inner != null) // Both can be null for e.g. if-statements that contains no session code.
                        {
                            hasSessionImplementations = true;
                        }
                    }
                    else
                    {
                        if (inner == null) {
                            throw new RuntimeException("[SJTypeBuildingContext_c] Shouldn't get in here.");
                        }

                        if (!orig.typeEquals(inner))
                        {
                            hasSessionImplementations = true;
                        }

                        break;
                    }
                }

                for (SJContextElement branchContext : ces)
                {
                    Set<String> fset = firstBranch.activeSessions();
                    Set<String> bset = branchContext.activeSessions();

                    for (String sjname : fset)
                    {
                        if (bset.contains(sjname))
                        {
                            boolean res = subsumeBranchSession(firstBranch, branchContext, sjname);

                            if (!hasSessionImplementations)
                            {
                                hasSessionImplementations = res;
                            }
                        }
                    }
                }

                for (String sjname : firstBranch.activeSessions())
                {
                    SJSessionType st = firstBranch.getImplemented(sjname);

                    while (st != null) {
                        SJSessionType branchBodyType = st.nodeClone();

                        SJSessionType active = ce.getActive(sjname);
                        SJSessionType implemented = ce.getImplemented(sjname);

                        ce.setActive(sjname, active == null ? null : active.child());
                        ce.setImplemented(sjname, implemented == null ? branchBodyType : implemented.append(branchBodyType));
                        st = st.child();
                    }
                }

                if (ce instanceof SJSessionBranchContext)
                {
                    for (String sjname : inbranches.keySet()) // Should be a single target.
                    {
                        SJSessionType active = ce.getActive(sjname);
                        SJSessionType implemented = ce.getImplemented(sjname);

                        SJSessionType st = inbranches.get(sjname); // Should be a single SJInbranchType node.

                        ce.setActive(sjname, active == null ? null : active.child());
                        ce.setImplemented(sjname, implemented == null ? st : implemented.append(st));
                    }
                }

                ((SJBranchContext) ce).setHasSessionImplementations(hasSessionImplementations); // If ces is empty, then session implementations would not be possible, so this is correctly false by default.
            }
        }
    }

    private Set<String> checkBranches(SJContextElement ce, List<SJContextElement> ces, Map<String, SJSessionType> inbranches) throws SemanticException {
        Set<String> delegated = new HashSet<String>();
        for (SJContextElement branchContext : ces)
        {
            for (String sjname : ((SJSessionContext) ce).targets()) // Should only be a single target.
            {
                SJLabel lab = ((SJBranchCaseContext) branchContext).label();
                SJSessionType implemented = branchContext.getImplemented(sjname);

                if (implemented instanceof SJDelegatedType) // For outbranch, this is taken care of by branch case subsumption.
                {
                    delegated.add(sjname);
                }

                SJInbranchType ibt;

                if (inbranches.containsKey(sjname))
                {
                    ibt = (SJInbranchType) inbranches.get(sjname);

                    if (ibt.hasCase(lab)) // Is this already checked earlier? If not, should it be checked here?
                    {
                        throw new SemanticException("[SJTypeBuildingContext_c] repeated session branch case: " + lab);
                    }
                    else
                    {
                        ibt = ibt.branchCase(lab, implemented);
                    }
                }
                else
                {
                    ibt = sjts.SJInbranchType().branchCase(lab, implemented);
                }

                inbranches.put(sjname, ibt);
                branchContext.removeSession(sjname); // For inbranch branches, we need to merge the cases - cannot use subsume (the opposite).
            }
        }
        return delegated;
    }

    public void checkSessionsCompleted() throws SemanticException // Maybe can be factored out with the routine in popContextElement. // Currently unused.
	{
		SJContextElement ce = currentContext();
		
		for (String sjname : ce.activeSessions()) // Duplicated from popContextElement.
		{
			SJSessionType expected = ce.getActive(sjname);
			
			if (expected != null)
			{
				throw new SemanticException("[SJTypeBuildingContext_c] Session " + sjname + " incomplete [5], expected: " + expected);
			}
		}
	}
	
	public boolean inSJBranchCaseContext()
	{
		for (SJContextElement ce : contexts()) // Does it matter which order we go through the context stack?
		{
			if (ce instanceof SJBranchCaseContext)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean inSJSessionLoopContext()
	{
		for (SJContextElement ce : contexts())
		{
			if (ce instanceof SJSessionLoopContext)
			{
				return true;
			}
		}
		
		return false;
	}
}
