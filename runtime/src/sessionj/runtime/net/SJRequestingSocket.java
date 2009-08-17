package sessionj.runtime.net;

import java.util.*;

import sessionj.types.sesstypes.SJSessionType;

import sessionj.runtime.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.util.*;

public class SJRequestingSocket extends SJAbstractSocket
{
	private SJService service;
	
	/*protected SJRequestingSocket(SJService service) throws SJIOException
	{
		this(service, SJSessionParameters.DEFAULT_PARAMETERS);
	}*/

	protected SJRequestingSocket(SJService service, SJSessionParameters params) throws SJIOException
	{
		super(service.getProtocol(), params); // Could override getProtocol but no point.
		
		this.service = service;
		
		//setParameters(params);
	}
	
	public SJRequestingSocket(SJProtocol p, SJSessionParameters params) throws SJIOException // For session-receive.
	{
		super(p, params); // FIXME: null service OK? Probably OK for received sessions.
	}
	
	public SJService getService()
	{
		return service;
	}
}
