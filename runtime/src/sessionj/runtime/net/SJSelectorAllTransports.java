package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.SJRuntimeException;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.SJRuntimeUtils;
import sessionj.runtime.util.ValueLatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class SJSelectorAllTransports implements SJSelector {
    private final Collection<TransportSelector> transportSelectors;
    private static final String UNSUPPORTED = "None of the transports support non-blocking mode";
    private static final Logger log = SJRuntimeUtils.getLogger(SJSelectorAllTransports.class);
    private final ExecutorService executor;

    SJSelectorAllTransports(Iterable<SJTransport> transports) {
        transportSelectors = new LinkedList<TransportSelector>();
        
        for (SJTransport t : transports) {
            TransportSelector trSel = t.transportSelector();
            if (trSel != null) transportSelectors.add(trSel);
        }
        if (transportSelectors.size() < 1) throw new SJRuntimeException
                ("None of the transports support non-blocking mode. Transports: " + transports);
        executor = Executors.newFixedThreadPool(transportSelectors.size());
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public void registerAccept(SJServerSocket ss) throws SJIOException {
        Collection<Boolean> results = new HashSet<Boolean>();
      	
        for (TransportSelector sel : transportSelectors)
            try {
                results.add(sel.registerAccept(ss));
            } catch (Exception e) {
                throw new SJIOException(e);
            }
        checkResults(results);
    }

    public void registerInput(SJSocket s) throws SJIOException {
        Collection<Boolean> results = new HashSet<Boolean>();
        for (TransportSelector sel : transportSelectors)
            try {
                results.add(sel.registerInput(s));
            } catch (Exception e) {
                throw new SJIOException(e);
            }
        checkResults(results);
        s.registerInputCallback();
    }

    private void checkResults(Collection<Boolean> results) throws SJIOException {
        if (!results.contains(true)) throw new SJIOException(UNSUPPORTED);
    }

    public SJSocket select() throws SJIOException {
        final ValueLatch<SJSocket> latch = new ValueLatch<SJSocket>();
        
        for (final TransportSelector sel : transportSelectors) {
            executor.submit(new Runnable() {
                public void run() {
                    try {
                        latch.submitValue(sel.select(true));
                    } catch (Throwable t) {
                        log.log(Level.SEVERE, "Error calling select on: " + sel, t);
                    }
                }
            });
        }
        
        SJSocket selected;
        try {
            selected = latch.awaitValue();
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        }

        assert selected != null;
        if (log.isLoggable(Level.FINER)) log.finer("Selected socket: " + selected);
        return selected;
    }

    public void close() throws SJIOException {
        for (TransportSelector sel : transportSelectors) sel.close();
        executor.shutdownNow();
    }
}
