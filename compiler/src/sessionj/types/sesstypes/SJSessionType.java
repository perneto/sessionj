package sessionj.types.sesstypes;

import polyglot.types.*;

import sessionj.types.*;
import sessionj.types.typeobjects.*;

/**
 * A SJSessionType holds the information of a session type element.
 */
public interface SJSessionType extends SJType 
{	
	public SJSessionType child();
	public SJSessionType child(SJSessionType child);

	public SJSessionType append(SJSessionType st);

	// Take Type argument to safely override behaviour for session types. 
	//public boolean isSubtype(Type t); // Reuse inherited behaviour (check typeEquals and descendsFrom), no need to override. 
	public boolean isDualtype(Type t);
	public SJSessionType subsume(SJSessionType t) throws SemanticException;
	public boolean wellFormed();

	public boolean treeEquals(SJSessionType tree); // Used by typeEquals.
	public boolean treeSubtype(SJSessionType tree);
	public boolean treeDualtype(SJSessionType tree);
	public SJSessionType treeSubsume(SJSessionType tree) throws SemanticException;
	public boolean treeWellFormed();

	public boolean nodeEquals(SJSessionType st);
	public boolean nodeSubtype(SJSessionType st); // this is a subtype of st. // Should check message SVUID values?
	public boolean nodeDualtype(SJSessionType st); // Should check message SVUID values?
	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException;
	public boolean nodeWellFormed();
	
	public SJSessionType treeClone();
	public SJSessionType nodeClone();

	public SJSessionType clone();
	public SJSessionType copy();
	
	public String treeToString();
	public String nodeToString();
	
	public SJSessionType getLeaf(); // Doesn't return a defensive copy.
}
