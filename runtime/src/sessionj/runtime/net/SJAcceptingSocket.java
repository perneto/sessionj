package sessionj.runtime.net;

import java.util.*;

import sessionj.types.sesstypes.SJSessionType;

import sessionj.runtime.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.*;

public class SJAcceptingSocket extends SJAbstractSocket
{
	public SJAcceptingSocket(SJProtocol protocol, SJSessionParameters params) throws SJIOException
	{
		super(protocol, params);
	}
}
