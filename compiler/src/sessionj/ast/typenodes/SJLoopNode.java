package sessionj.ast.typenodes;

public interface SJLoopNode extends SJTypeNode
{
	public SJTypeNode body();
	public SJLoopNode body(SJTypeNode body);
}
