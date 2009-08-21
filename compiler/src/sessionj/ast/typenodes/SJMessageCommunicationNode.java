package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;

public interface SJMessageCommunicationNode extends SJTypeNode
{
	public TypeNode messageType();
	public SJMessageCommunicationNode messageType(TypeNode messageType);
}
