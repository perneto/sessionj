package sessionj.types.sesstypes;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.subsumeReceiveTypes;

public class SJReceiveType_c extends SJMessageCommunicationType_c implements SJReceiveType
{
	private static final long serialVersionUID = SJ_VERSION;

	protected SJReceiveType_c(TypeSystem ts)
	{
		super(ts);
	}
	
	public SJReceiveType_c(TypeSystem ts, Type messageType) throws SemanticException
	{
		super(ts, messageType);
	}

	protected boolean eligibleForEquals(SJSessionType st)
	{
		return st instanceof SJReceiveType;
	}
	
	protected boolean eligibleForSubtype(SJSessionType st)
	{
		return st instanceof SJReceiveType;
	}
	
	protected boolean eligibleForDualtype(SJSessionType st)
	{
		return st instanceof SJSendType;
	}
	
	protected boolean compareNode(NodeComparison op, SJSessionType st)
	{
		switch (op)
		{
            // TODO: why equals() and not typeEquals() ?
			case EQUALS: return messageType().equals(((SJReceiveType) st).messageType()); 
			case SUBTYPE: return messageType().isSubtype(((SJReceiveType) st).messageType());  
			case DUALTYPE: return ((SJSendType) st).messageType().isSubtype(messageType());
		}
		
		throw new RuntimeException("[SJCBeginType_c] Shouldn't get here: " + op);
	}

	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException
	{
		if (!(st instanceof SJReceiveType))
		{
			throw new SemanticException("[SJReceiveType_c] Not subsumable: " + this + ", " + st);
		}

		return typeSystem().SJReceiveType(subsumeReceiveTypes(messageType(), ((SJReceiveType) st).messageType()));
	}

	protected SJReceiveType skeleton()
	{
		return new SJReceiveType_c(typeSystem());
	}
	
	protected String messageCommunicationOpen()
	{
		return SJ_STRING_RECEIVE_OPEN;
	}
	
	protected String messageCommunicationClose()
	{
		return SJ_STRING_RECEIVE_CLOSE;
	}

    public SJSessionType nodeDual() throws SemanticException {
        return typeSystem().SJSendType(messageType());
    }
}
