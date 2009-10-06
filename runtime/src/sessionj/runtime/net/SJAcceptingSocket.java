package sessionj.runtime.net;

import java.util.*;

import sessionj.types.sesstypes.SJSessionType;

import sessionj.runtime.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.*;

public class SJAcceptingSocket extends SJAbstractSocket
{
    /**
     * For accept(): user-level session types cannot be set types.
     */
    SJAcceptingSocket(SJProtocol protocol, SJSessionParameters params) throws SJIOException
	{
		super(protocol, params);
	}

    /**
     * For delegation: type can be a set type, need to know actual runtime type for typecase
     */
    public SJAcceptingSocket(SJProtocol protocol, SJSessionParameters params, SJSessionType actualType) throws SJIOException {
        super(protocol, params, actualType);
    }
}
