package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.ValueLatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SJSelectorAllTransports implements SJSelector {
    private final Collection<SJSelectorInternal> transportSelectors;
    private static final String UNSUPPORTED = "None of the transports support non-blocking mode";

    SJSelectorAllTransports(Iterable<SJTransport> transports) {
        transportSelectors = new LinkedList<SJSelectorInternal>();
        for (SJTransport t : transports) {
            SJSelectorInternal trSel = t.transportSelector();
            if (trSel != null) transportSelectors.add(trSel);
        }
    }

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

    public void registerOutput(SJSocket s) throws SJIOException {
        Collection<Boolean> results = new HashSet<Boolean>();
        for (SJSelectorInternal sel : transportSelectors)
            try {
            	//RAY
                // results.add(sel.registerOutput(s)); // Temporarily commented out to be build-able. Seemed less trouble than addind a dummy method to the target interface.
              //YAR
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

    public SJSocket select(final int mask) throws SJIOException {
        ExecutorService executor = Executors.newFixedThreadPool(transportSelectors.size());
        final ValueLatch<SJSocket> latch = new ValueLatch<SJSocket>();
        
        for (final SJSelectorInternal sel : transportSelectors) {
            executor.submit(new Callable<Object>() {
                public Object call() throws SJIOException {
                	try 
                	{
                    latch.submitValue(sel.select(mask));
                	}
                	catch (SJIncompatibleSessionException ise) // Ray: temporary fix to get around build error.
                	{
                		throw new SJIOException(ise);
                	}
                   
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
        return selected;
    }
    
    public void close() throws SJIOException {
        for (SJSelectorInternal sel : transportSelectors) sel.close();
    }
}
