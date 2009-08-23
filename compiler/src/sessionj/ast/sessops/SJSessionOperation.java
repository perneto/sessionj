package sessionj.ast.sessops;

import java.util.List;

import sessionj.ast.SJTypeable;

public interface SJSessionOperation extends SJTypeable
{
	/**
     * First (ambiguous) Receivers and later SJSocketVariables.
     * Aliases the actual AST nodes.
     */
    List targets();
	SJSessionOperation targets(List target);
}
