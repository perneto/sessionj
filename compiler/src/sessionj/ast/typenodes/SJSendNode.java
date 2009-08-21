package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;

public interface SJSendNode extends SJMessageCommunicationNode
{
	public SJSendNode messageTypeNode(TypeNode messageTypeNode);
}
