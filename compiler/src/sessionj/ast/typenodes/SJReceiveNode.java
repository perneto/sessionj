package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;

public interface SJReceiveNode extends SJMessageCommunicationNode
{
	public SJReceiveNode messageType(TypeNode messageType);
}
