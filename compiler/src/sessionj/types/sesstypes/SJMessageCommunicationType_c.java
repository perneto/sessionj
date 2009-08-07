package sessionj.types.sesstypes;

import polyglot.types.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

abstract public class SJMessageCommunicationType_c extends SJSessionType_c implements SJMessageCommunicationType
{
	private Type messageType; 

	//private long svuid = -1; // Factor out constant.

	protected SJMessageCommunicationType_c(TypeSystem ts)
	{
		super(ts);
	}
	
	public SJMessageCommunicationType_c(TypeSystem ts, Type messageType) throws SemanticException
	{
		super(ts);

		setMessageType(messageType); 
	}
	
	public Type messageType()
	{
		Type t = getMessageType();
		
		return (t instanceof SJSessionType) ? ((SJSessionType) t).copy() : t;
	}

	public SJSessionType messageType(Type messageType) throws SemanticException
	{
		SJMessageCommunicationType mct = skeleton();
		
		if (messageType instanceof SJSessionType)
		{
			messageType = ((SJSessionType) messageType).copy(); // Only session type constructors cloned - pointer equality maintained for ordinary types.
		}		
		
		((SJMessageCommunicationType_c) mct).setMessageType(messageType);
		
		return mct;
	}

	public boolean nodeWellFormed()
	{
		Type type = getMessageType();
		
		if (type instanceof SJSessionType)
		{
			if (type instanceof SJBeginType)
			{
				return ((SJBeginType) type).wellFormed();
			}
			
			return ((SJSessionType) type).treeWellFormed();
		}
		else
		{
			return true;
		}
	}

	public SJSessionType nodeClone()
	{
		SJMessageCommunicationType mct = skeleton();
		
		try
		{				
			return mct.messageType(getMessageType()); // Higher-order message types are copied by the setter method.
		}
		catch (SemanticException se) // Not possible - any problems would have been raised when this object was orig. created.
		{
			throw new RuntimeException("[SJMessageCommunicationType_c] Shouldn't get in here.");
		}
	}
	
	public String nodeToString()
	{
		String message = getMessageType().toString(); // toString enough for messageType? or need to manually get full name?

		return messageCommunicationOpen() + message + messageCommunicationClose();
	}
	
	protected Type getMessageType()
	{
		return messageType;
	}
	
	protected void setMessageType(Type messageType)
	{		
		this.messageType = messageType;
	}	
	
	abstract protected SJMessageCommunicationType skeleton(); 
	
	abstract protected String messageCommunicationOpen();
	abstract protected String messageCommunicationClose();
}
