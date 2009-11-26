package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.ValueLatch;
import sessionj.runtime.util.NamedThreadFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.concurrent.*;

class SJSelectorAllTransports implements SJSelector {
    private final Collection<SJSelectorInternal> transportSelectors;
    private static final String UNSUPPORTED = "None of the transports support non-blocking mode";
    private final NamedThreadFactory fact = new NamedThreadFactory();
    private static final Logger log = Logger.getLogger(SJSelectorAllTransports.class.getName());

    SJSelectorAllTransports(Iterable<SJTransport> transports) {
        transportSelectors = new LinkedList<SJSelectorInternal>();
        
        for (SJTransport t : transports) {
            SJSelectorInternal trSel = t.transportSelector();
            if (trSel != null) transportSelectors.add(trSel);
        }
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public void registerAccept(SJServerSocket ss) throws SJIOException {
        Collection<Boolean> results = new HashSet<Boolean>();
      	
        for (SJSelectorInternal sel : transportSelectors)
            try {
                results.add(sel.registerAccept(ss));
            } catch (Exception e) {
                throw new SJIOException(e);
            }
        checkResults(results);
    }

    public void registerInput(SJSocket s) throws SJIOException {
        Collection<Boolean> results = new HashSet<Boolean>();
        for (SJSelectorInternal sel : transportSelectors)
            try {
                results.add(sel.registerInput(s));
            } catch (Exception e) {
                throw new SJIOException(e);
            }
        checkResults(results);
    }

    private void checkResults(Collection<Boolean> results) throws SJIOException {
        if (!results.contains(true)) throw new SJIOException(UNSUPPORTED);
    }

    public SJSocket select() throws SJIOException {
        ExecutorService executor = Executors.newFixedThreadPool(transportSelectors.size(), fact);
        final ValueLatch<SJSocket> latch = new ValueLatch<SJSocket>();
        
        for (final SJSelectorInternal sel : transportSelectors) {
            fact.setName("Consolidated selector calling " + sel);
            executor.submit(new Callable<Object>() {
                public Object call() throws SJIOException, SJIncompatibleSessionException {
                    latch.submitValue(sel.select());
                	return null;
                }
            });
        }
        
        SJSocket selected;
        try {
            selected = latch.awaitValue();
        } catch (InterruptedException e) {
            throw new SJIOException(e);
        } finally {
            executor.shutdownNow();
        }

        assert selected != null;
        log.finer("Selected socket: " + selected);
        return selected;
    }

    public void close() throws SJIOException {
        for (SJSelectorInternal sel : transportSelectors) sel.close();
    }
}
