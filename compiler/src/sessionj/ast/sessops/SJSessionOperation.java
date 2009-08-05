package sessionj.ast.sessops;

import java.util.List;

import sessionj.ast.SJTypeable;

public interface SJSessionOperation extends SJTypeable
{
	public List targets(); // First (ambiguous) Receivers and later SJSocketVariables. Aliases the actual AST nodes.
	public SJSessionOperation targets(List target);
}
