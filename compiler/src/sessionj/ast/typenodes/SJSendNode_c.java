package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;
import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJSendNode_c extends SJMessageCommunicationNode_c implements SJSendNode
{
	public SJSendNode_c(Position pos, TypeNode messageType)
	{
		super(pos, messageType);
	}

	public SJSendNode messageTypeNode(TypeNode messageType)
	{
		return (SJSendNode) super.messageType(messageType);
	}

	public String nodeToString()
	{
		String message = messageType().toString(); // toString enough for messageType? or need to manually get full name?

		return SJ_STRING_SEND_OPEN + message + SJ_STRING_SEND_CLOSE;
	}
}
