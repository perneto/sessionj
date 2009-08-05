package sessionj.types.sesstypes;

import polyglot.types.*;

import static sessionj.SJConstants.*;

public class SJInwhileType_c extends SJLoopType_c implements SJInwhileType
{
	public static final long serialVersionUID = SJ_VERSION;

	public SJInwhileType_c(TypeSystem ts)
	{
		super(ts);
	}

	public SJInwhileType body(SJSessionType body)
	{
		return (SJInwhileType) super.body(body);
	}
	
	protected boolean eligibleForEquals(SJSessionType st)
	{
		return st instanceof SJInwhileType;
	}
	
	protected boolean eligibleForSubtype(SJSessionType st)
	{
		return st instanceof SJInwhileType;
	}
	
	protected boolean eligibleForDualtype(SJSessionType st)
	{
		return st instanceof SJOutwhileType;
	}
	
	protected SJInwhileType skeleton()
	{
		return typeSystem().SJInwhileType();
	}
	
	protected boolean eligibleForSubsume(SJSessionType st)
	{
		return st instanceof SJInwhileType;
	}
	
	protected String loopConstructorOpen()
	{
		return SJ_STRING_INWHILE_OPEN;
	}
	
	protected String loopConstructorClose()
	{
		return SJ_STRING_INWHILE_CLOSE;
	}	
}
