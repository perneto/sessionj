/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;

/**
 * @author Raymond
 *
 */
public interface SJContextElement
{
	public SJNamedInstance getChannel(String sjname);
	public SJNamedInstance getSocket(String sjname);
	public SJNamedInstance getServer(String sjname);
	
	public void setChannel(SJNamedInstance ni);
	public void setSocket(SJNamedInstance ni);
	public void setServer(SJNamedInstance ni);
	
	public SJSessionType getService(String sjname);	
	public SJSessionType getSession(String sjname);
	public SJSessionType getActive(String sjname);
	public SJSessionType getImplemented(String sjname);
	
	public void setService(String sjname, SJSessionType st);
	public void setSession(String sjname, SJSessionType st); // Seems to be the "original" session type when entering a session-try. // This is also used to record the sessions in scope, i.e. which sessions operations can be performed on.
	public void setActive(String sjname, SJSessionType st); // The remaining session type to be implemented. // Used to check, in conjunction with the sessions in scope, whether sessions have been completed. // For noalias (not na-final) method parameters, sessions are initially active, but not yet in scope.
	public void setImplemented(String sjname, SJSessionType st); // The type of the session implemented so far.
	
	public Set<String> channelSet();
	public Set<String> socketSet();
	public Set<String> serverSet();
	public Set<String> servicesInScope();
	public Set<String> sessionsInScope();	
	public Set<String> activeSessions();
	
	public boolean hasChannel(String sjname);
	public boolean hasSocket(String sjname);
	public boolean hasServer(String sjname);
	
	public boolean serviceInScope(String sjname);
	public boolean serviceOpen(String sjname);
	public boolean sessionInScope(String sjname);
	public boolean sessionActive(String sjname);	
	
	public void clearChannels();	
	public void clearSockets();
	public void clearServices();
	public void clearSessions();
	public void clearServers();
	
	public void removeSession(String sjname);
	
	/*public SJSessionType getImplementedOverflow(String sjname); // Hack for fixing delegation from within branch cases.
	public void setImplementedOverflow(String sjname, SJSessionType st);
	public void removeImplementedOverflow(String sjname);*/
	
	//public boolean canDelegate(String sjname);
}
