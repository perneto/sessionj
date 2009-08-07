package sessionj.ast.sessvars;

import polyglot.ast.NamedVariable;

import sessionj.ast.SJNamed;

public interface SJVariable extends NamedVariable//, SJNamed // SJTypeable component unused. // Actually, session type information is never built for SJVariables (extension object is not attached).
{
	public String sjname(); // This should match the SJ name parsed from the variable declarations.
}
