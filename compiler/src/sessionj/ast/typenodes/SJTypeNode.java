package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;

import sessionj.types.sesstypes.*;

/**
 * A SJTypeNode is the internal compiler representation of a parsed session
 * type element. The SJTypeNodes are correspond to their SJType equivalents, but are less developed.
 * 
 * FIXME: rename to SJSessionTypeNode (to correspond with the type objects)?
 * 
 */
public interface SJTypeNode extends TypeNode
{
	public SJTypeNode child();
	public SJTypeNode child(SJTypeNode child);

	//public SJTypeNode leaf();
	
	public SJSessionType type();

	public String nodeToString(); // Could be protected?
	public String treeToString();
}
