package sessionj.runtime.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import polyglot.types.SemanticException;
import polyglot.types.Type;

import sessionj.types.*;
import sessionj.types.contexts.SJTypeBuildingContext_c;
import sessionj.types.sesstypes.*;
import sessionj.util.*;

import sessionj.runtime.*;
import sessionj.runtime.session.contexts.*;


/**
 * FIXME: rework context management for in/outwhile and recursion using unfold routine of SJLoopType.
 */
public class SJStateManager_c implements SJStateManager // Analogous to SJContext. But only tracks the session types for a single session, as each session has an individual underlying socket, with its own socket state object. // We currently record the type of the session completed so far. An alternative would be to track the concrete operation history: basically, loops unrolled. This might make the lost message resending algorithm simpler. 
{
	private static final boolean DEBUG = false; // TODO: centralise debugging routines.
	//private static final boolean DEBUG = true; 
	
	//private final SJRSocket rsocket; // HACK: only socket state knows when we're leaving a recursion scope.
	private final SJTypeSystem sjts;
	private final SJSessionType protocolType; // The "original" type of the session.

	private final Stack<SJRuntimeContextElement> contexts = new Stack<SJRuntimeContextElement>();
	//private final SJSessionType lastResendState = null;

	public SJStateManager_c(SJTypeSystem sjts, SJSessionType protocolType)
	{
		//this.rsocket = null;
		this.sjts = sjts;
		this.protocolType = protocolType;//.treeClone();
		
		debugPrintln("Protocol type: " + protocolType.toString());
	}

	/*public SJStateManager_c(SJRSocket rsocket, SJSessionType protocolType)
	{
		//this.rsocket = rsocket;
		this.sjts = rsocket.sjTypeSystem();
		this.protocolType = protocolType;//.treeClone();
	}*/

	public final SJSessionType protocolType()
	{
		return protocolType;
	}

	public final SJRuntimeContextElement currentContext()
	{
		return contexts.peek();
	}

	private SJSessionType activeType()
	{
		SJSessionType st = (contexts.isEmpty()) ? null : currentContext().activeType();
		
		if (st != null) // HACK: because recurse types are unrolled lazily.
		{
			st = SJTypeBuildingContext_c.substituteTypeVariables(st, getRecursions()); 
		}
		
		return st;
	}

	private Map<SJLabel, SJRecursionType> getRecursions() // FIXME: should instead build this information as we go along (i.e. scopes entered/left).  
	{
		Map<SJLabel, SJRecursionType> map = new HashMap<SJLabel, SJRecursionType>(); 

		for (int i = contexts.size() - 1; i >= 0; i--)
		{
			SJRuntimeContextElement rce = contexts.get(i);
			
			if (rce instanceof SJRecursionContext)
			{
				SJRecursionContext rc = (SJRecursionContext) rce;
				
				SJLabel lab = rc.label();
				SJRecursionType rt = rc.originalType();
								
				map.put(lab, rt); // Will overwrite types from outer scopes if necessary.
			}
		}
		
		return map;
	}
	
	private SJSessionType implementedType()
	{
		return currentContext().implementedType();
	}

	public final boolean inIterationContext()
	{
		for (int i = contexts.size() - 1; i >= 1; i--)
		{
			if (contexts.get(i) instanceof SJLoopContext)
			{
				return true;
			}
		}

		return false;
	}

	public final SJSessionType currentState() // i.e. the session type performed so far.
	{
		return currentState(0); // 0th is the top-level.
	}

	private SJSessionType currentState(int i)
	{
		SJRuntimeContextElement sjsc = contexts.get(i);
		SJSessionType current = sjsc.implementedType();

		if (current == null)
		{
			if (i < contexts.size() - 1)
			{
				current = currentState(i + 1);
			}
		}
		else
		{
			current = current.treeClone();

			if (i < contexts.size() - 1)
			{
				current = current.append(currentState(i + 1));
			}
		}

		if (sjsc instanceof SJOutwhileContext) // Not possible during a delegation.
		{
			current = sjts.SJOutwhileType().body(current);
		}
		else if (sjsc instanceof SJInwhileContext)
		{
			current = sjts.SJInwhileType().body(current);
		}
		else if (sjsc instanceof SJOutbranchContext)
		{
			current = sjts.SJOutbranchType().branchCase(((SJOutbranchContext) sjsc).label(), current);
		}
		else if (sjsc instanceof SJInbranchContext)
		{
			current = sjts.SJInbranchType().branchCase(((SJInbranchContext) sjsc).label(), current);
		}
		else if (sjsc instanceof SJRecursionContext)
		{
			current = sjts.SJRecursionType(((SJRecursionContext) sjsc).label()).body(current);
		}

		return current;
	}

	public final SJSessionType expectedType()
	{
		return activeType();
	}

	public final SJSessionType accept() throws SJIOException
	{
		SJBeginType sjbt = begin(sjts.SJSBeginType());
				
		debugPrintln("accept: " + sjbt);
		
		return sjbt; 
	}

	public final SJSessionType request() throws SJIOException
	{
		SJBeginType sjbt = begin(sjts.SJCBeginType());	
		
		debugPrintln("request: " + sjbt);		
		
		return sjbt;
	}

	private SJBeginType begin(SJBeginType sjbt) throws SJIOException
	{
		SJSessionType protocol = protocolType();

		if (!(protocol instanceof SJBeginType))
		{
			throw new SJIOException("Protool should start with '" + sjbt + "', not : " + protocol);
		}

		pushTopLevel(protocol);//.treeClone()); // 'child' returns a cloned subtree.		
		advanceContext(sjbt);
		
		return sjbt;//.nodeClone()); // (Node)clone is essentially equivalent to instantiation.
	}

	public final void open() // To receive delegated sessions.
	{
		pushTopLevel(protocolType());//.treeClone());
	}

	public final SJSessionType send(Object obj) throws SJIOException  
	{
		Type mt;

		try
		{
			mt = parseClassName(sjts, fullClassName(obj));
		}
		catch (SemanticException se) // Not possible.
		{
			throw new SJIOException(se);
		}
		
		return sendAux(mt);
	}
	
	public final SJSessionType sendBoolean(boolean v) throws SJIOException
	{
		return sendAux(sjts.Boolean());
	}

	public final SJSessionType sendInt(int v) throws SJIOException
	{
		return sendAux(sjts.Int());
	}

	public final SJSessionType sendByte(byte v) throws SJIOException
	{
		return sendAux(sjts.Byte());
	}

	public final SJSessionType sendDouble(double d) throws SJIOException
	{
		return sendAux(sjts.Double());
	}
	
	public final SJSessionType sendSession(SJSessionType sjtype) throws SJIOException
	{
		return sendAux(sjtype); // Subtyping direction is the same for ordinary and session message types.
	}

	public final SJSessionType sendChannel(SJSessionType sjtype) throws SJIOException
	{
		/*SJSessionType active = activeType().nodeClone();
		
		if (!(sjtype instanceof SJBeginType)) // sendAux checks subtype direction.
		{
			throw new SJIOException("Expected '" + active + "' but implemented: " + sjst);
		}*/
		
		return sendAux(sjtype);
	}	
	
	private SJSendType sendAux(Type mt) throws SJIOException
	{
		SJSendType sjst = null;

		try
		{
			sjst = sjts.SJSendType(mt);
		}
		catch (SemanticException se)
		{
			throw new SJIOException(se);
		}

		/*SJSessionType active = activeType().nodeClone(); // We trust ourselves to send correctly.

		if (!active.isSubtype(sjst))
		{
			throw new SJIOException("Expected '" + active + "' but implemented: " + sjst);
		}*/

		advanceContext(sjst);

		debugPrintln("send: " + sjst);		
		
		return sjst;//.nodeClone());
	}

	public final SJSessionType receive(Object obj) throws SJIOException
	{
		Type mt;

		try
		{
			mt = parseClassName(sjts, fullClassName(obj));
		}
		catch (SemanticException se) // Not possible.
		{
			throw new SJIOException(se);
		}

		return receiveAux(mt);
	}

	public final SJSessionType receiveBoolean(boolean v) throws SJIOException
	{
		return receiveAux(sjts.Boolean());
	}

	public final SJSessionType receiveInt(int v) throws SJIOException
	{
		return receiveAux(sjts.Int());
	}

	public final SJSessionType receiveByte(byte b) throws SJIOException
	{
		return receiveAux(sjts.Byte());
	}

	public final SJSessionType receiveDouble(double d) throws SJIOException
	{
		return receiveAux(sjts.Double());
	}
	
	public final SJSessionType receiveSession(SJSessionType sjtype) throws SJIOException 
	{
		return receiveAux(sjtype); // Subtyping direction is the same for ordinary and session message types.
	}

	public final SJSessionType receiveChannel(SJSessionType sjtype) throws SJIOException
	{
		if (!(sjtype instanceof SJCBeginType)) // receiveAux checks subtyping direction. // Unlike for send where we trust ourselves, we don't necessary trust our peer on receives.
		{
			throw new SJIOException("Received channel type must start with cbegin, not: " + sjtype);
		}
		
		return receiveAux(sjtype);
	}

	private SJSessionType receiveAux(Type mt) throws SJIOException
	{
		SJReceiveType sjrt = null;

		try
		{
			sjrt = sjts.SJReceiveType(mt);
		}
		catch (SemanticException se) // Not possible.
		{
			throw new SJIOException(se);
		}

		SJSessionType active = activeType();//.nodeClone(); // Unlike for send where we trust ourselves, we don't necessary trust our peer on receives.

		if (!sjrt.nodeSubtype(active)) // Opposite direction to send.
		{
			SJReceiveType foo = (SJReceiveType) active;
			
			System.out.println("a: " + sjrt.messageType() + ", " + foo.messageType() + ", " + sjrt.messageType().equals(foo.messageType()));
		
			throw new SJIOException("Expected '" + active.nodeClone() + "' but received: " + sjrt);
		}

		advanceContext(sjrt);

		debugPrintln("receive: " + sjrt);
		
		return sjrt;//.nodeClone());
	}
	
	public final void outbranch(SJLabel lab) throws SJIOException
	{
		SJSessionType active = activeType();//.nodeClone(); // Clone only needed to get expected type.

		/*if (!(active instanceof SJInbranchType)) // No point to check own actions.
		{
			throw new SJIOException("Expected outbranch, but implemented: " + active);
		}*/

		pushOutbranch(lab, ((SJOutbranchType) active).branchCase(lab));
		
		debugPrintln("Pushed outbranch: " + lab);
	}

	public final void inbranch(SJLabel lab) throws SJIOException // Expected message was a String, already implicitly checked (casting) by socket implementation. Move check into here?
	{
		SJSessionType active = activeType();//.nodeClone();

		/*if (!(active instanceof SJInbranchType))
		{
			throw new SJIOException("Expected inbranch, but implemented: " + active);
		}*/

		SJInbranchType ibt = (SJInbranchType) active;
		
		if (!ibt.labelSet().contains(lab))
		{
			throw new SJIOException("Unexpected branch label: " + lab);
		}
		
		pushInbranch(lab, ibt.branchCase(lab));
		
		debugPrintln("Pushed inbranch: " + lab);
	}

	public final void outwhile(boolean bool)  throws SJIOException // Could return performed type on false.
	{
		SJSessionType active = activeType();//.nodeClone();

		if (active instanceof SJUnknownType) // In the innermost iteration, and it has an empty body...
		{
			popContext(); //...so we have to pop here, because there was no basic action to pop it.

			active = activeType();
		}
		/*else if (!(active instanceof SJOutwhileType)) // No point to check own actions.
		{
			throw new SJIOException("Expected outwhile, but implemented: " + active);
		}*/

		active = ((SJOutwhileType) active).body();

		if (bool)
		{
			pushOutwhile(active); // FIXME: rework in/outwhile and recursion contexts to use unfold routine in SJLoopType.
			
			debugPrintln("Entered outwhile for: " + active);
		}
		else
		{
			SJOutwhileType owt = sjts.SJOutwhileType().body(active); // Hacky? (The actual implemented type was lost when the context was popped by the last operation in the iteration, so have to use the expected type.)

			advanceContext(owt);
			
			debugPrintln("Finished outwhile: " + owt);
		}
	}

	public final void inwhile(boolean bool) throws SJIOException
	{
		SJSessionType active = activeType();//.nodeClone();

		if (active instanceof SJUnknownType) // The innermost iteration has an empty body.
		{
			popContext();

			active = activeType();
		}
		/*else if (!(active instanceof SJInwhileType))
		{
			throw new SJIOException("Expected inwhile, but implemented: " + active);
		}*/

		active = ((SJInwhileType) active).body();

		if (bool)
		{
			pushInwhile(active);
			
			debugPrintln("Entered inwhile for: " + active);
		}
		else
		{
			SJInwhileType iwt = sjts.SJInwhileType().body(active);

			advanceContext(iwt);
			
			debugPrintln("Finished inwhile: " + iwt);
		}
	}
	
	private SJRecursionType findRecursionBinder(SJLabel lab) // HACK: to try and get runtime monitoring working when recursive sessions are done through recursive method calls. But this is not working in general at all. 
	{		
		//for (SJRuntimeContextElement rce : contexts) // Starts from innermost (oldest).
		for (int i = contexts.size() - 1; i >= 0; i--)	
		{
			SJRuntimeContextElement rce = contexts.get(i);
			
			if (rce instanceof SJRecursionContext)
			{
				SJRecursionContext rc = (SJRecursionContext) rce;
				
				if (rc.label().equals(lab))
				{
					return rc.originalType(); // Gets a defensive copy.
				}
			}
		}
		
		throw new SJRuntimeException("[SJStateManager_c] Shouldn't get in here: " + lab);
	}
	
	// This is called by recursionEnter; currently, nothing is done on recursionExit (this seems to be convenient for e.g. registration of sessions with a SJSelector - and delegation within recursion scopes in general?).
	// FIXME: a better way to do this is to unfold the recursive type every time we come here. That would be "eager" compared to the "lazy" unfolding we do now.
	public final void recursion(SJLabel lab) throws SJIOException // Recursion is "local" (so is checked by compiler), no dynamic check needed (no point to check own actions).
	{
		//RAY: to handle recursive sessions done through method calls. 
		SJRecursionType rt;
		
		if (activeType() instanceof SJRecurseType) // This may subsume the need to do SJRuntime.recurse. But it seems better to as much explicit as possible.
		{
			rt = findRecursionBinder(lab);
			
			advanceContext(sjts.SJRecurseType(lab)); // As done by recurse. Is this safe?	Need to use the well-formed check to prevent e.g. hacked protocol having two recurse types consecutively.		
		}
		else
		{
			rt = (SJRecursionType) activeType().nodeClone(); // Create a defensive copy like body does. 			
		}
		//YAR

		//pushRecursion(lab, ((SJRecursionType) activeType()).body()); // body returns a defensive copy.
		pushRecursion(rt); // Changed (now different to e.g. in/outwhile routines) because we want to keep the whole type as information.
		
		debugPrintln("Entered recursion for: " + lab);
	}

	public final SJSessionType recurse(SJLabel lab) throws SJIOException // Recursion is "local" (so is checked by compiler), no dynamic check needed (no point to check own actions).
	{
		SJRecurseType sjrt = sjts.SJRecurseType(lab);

		advanceContext(sjrt);

		debugPrintln("recurse: " + sjrt);
		
		return sjrt;//.nodeClone());
	}
	
	public final void close() 
	{
		//debugPrintln("Closing: " + currentState()); // Still called for failed sessions, so using currentState here is not always safe.	
		
		reset();
		
		//debugPrintln("close: end");	// Factor out constant?	
	}

	public final void reset()
	{
		contexts.removeAllElements();
	}

	private void advanceContext(SJSessionType sjtype)
	{
		SJSessionType implemented = sjtype;

		//for (boolean redo = true; redo; )
		while (true)
		{
			SJRuntimeContextElement sjsc = currentContext();
			SJSessionType completed = appendToImplemented(implemented);//.nodeClone()); // Implemented is the single operation just performed.
			SJSessionType next = sjsc.activeType().child(); // Maybe end types should be (implicitly) reintroduced.

			sjsc.activeType(next);

			//SJRuntimeUtils.debugPrintln("Socket state: " + activeType() + ", " + implementedType());

			if (next == null && !(sjsc instanceof SJTopLevelContext))
			{
				if (sjsc instanceof SJLoopContext)
				{
					if (sjsc instanceof SJOutwhileContext || sjsc instanceof SJInwhileContext)
					{
						popContext(); // sjsc popped. // The iteration body type actually implemented is lost.

						break;//redo = false;
					}
					else if (sjsc instanceof SJRecursionContext)
					{
						if (sjtype instanceof SJRecurseType) // Enacting the recursion.
						{
							SJLabel lab = ((SJRecurseType) sjtype).label();

							while (!((SJRecursionContext) currentContext()).label().equals(lab)) // Other contexts must have been completed (advanced and popped) to get to this point, so it can only be a series of recursion contexts?
							{
								popContext();
							}

							popContext(); // The recursion body type actually implemented is lost.

							break;//redo = false;
						}
						else // This recursion (including "nested mutual" scopes) has finished.
						{
							popContext(); // sjsc popped.

							SJLabel lab = ((SJRecursionContext) sjsc).label();

							implemented = sjts.SJRecursionType(lab).body(completed); // Only the branch type that quits the iteration is recorded.

							/*if (rsocket != null) // HACK: leaving recursion context.
							{
								if (!inIterationContext())
								{
									rsocket.pushSentMessage(SJRSocket.CACHE_MARKER);
								}
							}*/
						}
					}
				}
				else if (sjsc instanceof SJBranchContext)
				{
					popContext(); // sjsc popped.

					SJLabel lab = ((SJBranchContext) sjsc).label();

					if (sjsc instanceof SJOutbranchContext)
					{
						//advanceContext(sjts.SJOutbranchType().branchCase(lab, completed));
						implemented = sjts.SJOutbranchType().branchCase(lab, completed);
					}
					else //if (sjsc instanceof SJInbranchContext)
					{
						//advanceContext(sjts.SJInbranchType().branchCase(lab, completed));
						implemented = sjts.SJInbranchType().branchCase(lab, completed);
					}
				}
			}
			else
			{
				break;//redo = false;
			}
		}
	}

	private SJSessionType appendToImplemented(SJSessionType sjtype)
	{
		SJSessionType implemented = implementedType();

		if (implemented != null)
		{
			sjtype = implemented.append(sjtype);
		}

		currentContext().implementedType(sjtype);

		return sjtype;
	}

	public final void pushTopLevel(SJSessionType sjtype)
	{
		pushContext(new SJTopLevelContext(sjtype));
	}

	public final void pushOutbranch(SJLabel lab, SJSessionType sjtype)
	{
		if (sjtype == null) // Possible mismatch between program execution and session execution if an exception is raised within the (empty) branch. Does it matter? // So all possible branch cases are recorded, not the ones that were actually taken.
		{
			advanceContext(sjts.SJOutbranchType().branchCase(lab, null));
		}
		else
		{
			pushContext(new SJOutbranchContext(lab, sjtype));
		}
	}

	public final void pushInbranch(SJLabel lab, SJSessionType sjtype)
	{
		if (sjtype == null)
		{
			advanceContext(sjts.SJInbranchType().branchCase(lab, null));
		}
		else
		{
			pushContext(new SJInbranchContext(lab, sjtype));
		}
	}

	public final void pushOutwhile(SJSessionType sjtype)
	{
		if (sjtype == null)
		{
			pushContext(new SJOutwhileContext(sjts.SJUnknownType())); // Hacky? To represent empty iteration.
		}
		else
		{
			pushContext(new SJOutwhileContext(sjtype));
		}
	}

	public final void pushInwhile(SJSessionType sjtype)
	{
		if (sjtype == null)
		{
			pushContext(new SJInwhileContext(sjts.SJUnknownType()));
		}
		else
		{
			pushContext(new SJInwhileContext(sjtype));
		}
	}

	/*public final void pushRecursion(SJLabel lab, SJSessionType sjtype)
	{
		if (sjtype == null)
		{
			advanceContext(sjts.SJRecursionType(lab).body(null)); // Like branch: an empty recursion can't recurse because no recurse type in body.
		}
		else
		{
			pushContext(new SJRecursionContext(lab, sjtype));
		}
	}*/

	public final void pushRecursion(SJRecursionType rt)
	{
		if (rt.body() == null)
		{
			advanceContext(rt); // Like branch: an empty recursion can't recurse because no recurse type in body.
		}
		else
		{
			pushContext(new SJRecursionContext(rt));
		}
	}
	
	private void pushContext(SJRuntimeContextElement sjsc)
	{
		contexts.push(sjsc);

		//SJRuntimeUtils.debugPrintln("Pushed " + sjsc + ": " + activeType() + ", " + implementedType());
	}

	private void popContext()
	{
		SJRuntimeContextElement sjsc = contexts.pop();

		//SJRuntimeUtils.debugPrintln("Popped " + sjsc + " to: " + activeType() + ", " + implementedType());
	}

	private static String fullClassName(Object obj) // Move to SJRuntimeUtils?
	{
		String m = obj.getClass().toString().substring("class ".length());

		if (m.startsWith("["))
		{
			int depth = m.lastIndexOf('[') + 1;

			m = m.substring(m.lastIndexOf('[') + 1);

			if (m.equals("Z")) // See JLS:Arrays.
			{
				m = "boolean";
			}
			else if (m.equals("I"))
			{
				m = "int";
			}
			else if (m.equals("B"))
			{
				m = "byte";
			}
			else if (m.startsWith("L")) // Non-primitive type array.
			{
				m = m.substring("L".length());
				m = m.substring(0, m.length() - ";".length());
			}
			else
			{
				System.out.println("Unrecognised array class signature: " + m);
			}

			for ( ; depth > 0; depth--)
			{
				m = m + "[]";
			}
		}
		else
		{

		}

		return m;
	}
	
	private static Type parseClassName(SJTypeSystem sjts, String m) throws SemanticException // Move to SJRuntimeUtils?
	{
		Type mt; 
		
		if (m.contains("[]"))
		{
			String n = m.substring(0, m.indexOf('['));
	
			Type messageType = null;
	
			if (n.equals("boolean")) // Factor out constants. // But shouldn't get in here? (Primitive-typed operations have been separated?)
			{
				messageType = sjts.Boolean();
			}
			else if (n.equals("int"))
			{
				messageType = sjts.Int();
			}
			else if (n.equals("byte"))
			{
				messageType = sjts.Byte();
			}
			else if (n.equals("double"))
			{
				messageType = sjts.Double();
			}
			else
			{
				messageType = sjts.typeForName(n);
			}
	
			int dims = (m.length() - m.indexOf('[')) / "[]".length();
	
			mt = sjts.arrayOf(messageType, dims);
		}
		else
		{
			mt = sjts.typeForName(m);
		}
		
		return mt;
	}
	
	private /*static*/ void debugPrintln(String m)
	{
		if (DEBUG)
		{
			System.out.println("[SJStateManager_c] " + m);
			
			/*if (!contexts.isEmpty()) // May call debugPrintln outside of stable SJStateManager state, e.g. before fully intialised or on session failure. 
			{
				System.out.println("[SJStateManager_c] Current session type: " + currentState());
				System.out.println("[SJStateManager_c] Contexts: " + contexts);
			}*/
		}
	}

}
