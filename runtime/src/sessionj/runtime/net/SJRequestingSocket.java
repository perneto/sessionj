package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.SJProtocol;

public class SJRequestingSocket extends SJAbstractSocket
{
	private SJService service;
	
	SJRequestingSocket(SJService service, SJSessionParameters params) throws SJIOException
	{
		super(service.getProtocol(), params); // Could override getProtocol but no point.
		
		this.service = service;
		
		//setParameters(params);
	}
	
	public SJRequestingSocket(SJProtocol p, SJSessionParameters params) throws SJIOException // For session-receive.
	{
		super(p, params); // FIXME: null service OK? Probably OK for received sessions.
	}
	
	public SJServerIdentifier getServerIdentifier()
	{
		return service.getServerIdentifier();
	}
}
