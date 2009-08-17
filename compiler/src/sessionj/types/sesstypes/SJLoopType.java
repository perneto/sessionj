package sessionj.types.sesstypes;

public interface SJLoopType extends SJSessionType
{
	public SJSessionType body();
	public SJLoopType body(SJSessionType body);
}
