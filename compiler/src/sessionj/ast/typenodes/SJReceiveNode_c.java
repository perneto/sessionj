package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;
import polyglot.util.Position;

import static sessionj.SJConstants.*;

public class SJReceiveNode_c extends SJMessageCommunicationNode_c implements SJReceiveNode
{
	public SJReceiveNode_c(Position pos, TypeNode messageType)
	{
		super(pos, messageType);
	}

	public SJReceiveNode messageType(TypeNode messageType)
	{
		return (SJReceiveNode) super.messageType(messageType);
	}

	public String nodeToString()
	{
		String message = messageType().toString(); // toString enough for messageType? or need to manually get full name?

		return SJ_STRING_RECEIVE_OPEN + message + SJ_STRING_RECEIVE_CLOSE;
	}
}
