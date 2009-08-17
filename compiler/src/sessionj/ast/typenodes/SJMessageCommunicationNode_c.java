package sessionj.ast.typenodes;

import polyglot.ast.TypeNode;
import polyglot.util.Position;

abstract public class SJMessageCommunicationNode_c extends SJTypeNode_c implements SJMessageCommunicationNode
{
	private TypeNode messageType;

	public SJMessageCommunicationNode_c(Position pos, TypeNode messageType)
	{
		super(pos);

		this.messageType = messageType;
	}

	public TypeNode messageType()
	{
		return messageType;
	}

	public SJMessageCommunicationNode messageType(TypeNode messageType)
	{
		this.messageType = messageType; // Unlike polyglot type nodes, no defensive copy (objects are mutable).

		return this;
	}
}
