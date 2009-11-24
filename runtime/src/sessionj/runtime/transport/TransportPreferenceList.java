package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;

import java.util.*;

public final class TransportPreferenceList {
    private final Map<Character, SJTransport> backingStore;
    private final List<SJTransport> preferenceList;
    private final String defaultTransportsCodes;

    public TransportPreferenceList(Map<Character, SJTransport> backingStore, String defaultTransportsCodes) throws SJIOException {
        // We do want other instances to change the same map - that's the whole point.
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.backingStore = backingStore;
        this.defaultTransportsCodes = defaultTransportsCodes;
        preferenceList = new LinkedList<SJTransport>();
        loadTransports(defaultTransportsCodes);
    }
    
    public List<SJTransport> defaultTransports() {
        List<SJTransport> defaults = new LinkedList<SJTransport>();
        for (char c : defaultTransportsCodes.toCharArray()) {
            defaults.add(backingStore.get(c));
        }
        return defaults;
    }
    
    public List<SJTransport> loadTransports(String transportLetterCodes) throws SJIOException {
        List<SJTransport> ts = new LinkedList<SJTransport>();
        for (char c : transportLetterCodes.toCharArray()) {
            SJTransport t = backingStore.get(c);
            if (t == null) { // If the system has not already loaded a transport component requested by a session, load it now. However, I think this affects the "default" value for transports across the system. Need to factor out an orthogonal defaults value again.
                t = SJTransportManager_c.createTransport(c);
                backingStore.put(c, t);
            }
            if (!preferenceList.contains(t)) {
                preferenceList.add(t);
            }
            ts.add(t);
        }
        return ts;
    }

    public List<SJTransport> getActive() {
        synchronized (preferenceList)
        {
            return Collections.unmodifiableList(preferenceList);
        }
    }
}
