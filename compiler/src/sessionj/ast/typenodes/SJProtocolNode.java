package sessionj.ast.typenodes;

import polyglot.ast.*;

/**
 * SJProtocolNode is a meta type object that refers to (the session type of) another protocol.
 */
public interface SJProtocolNode extends SJTypeNode
{
	public Receiver target();
	public SJProtocolNode target(Receiver target);
}
