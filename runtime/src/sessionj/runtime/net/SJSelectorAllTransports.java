package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.transport.SJTransport;
import sessionj.runtime.util.ValueLatch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SJSelectorAllTransports implements SJSelector {
    private final Collection<SJSelector> transportSelectors;

    SJSelectorAllTransports(Iterable<SJTransport> transports) {
        transportSelectors = new LinkedList<SJSelector>();
        for (SJTransport t : transports) {
            SJSelector trSel = t.transportSelector();
            if (trSel != null) transportSelectors.add(trSel);
        }
    }

    public void registerAccept(SJServerSocket ss) {
        for (SJSelector sel : transportSelectors) sel.registerAccept(ss);
    }
    public void registerSend(SJSocket s) {
        for (SJSelector sel : transportSelectors) sel.registerSend(s);
    }
    public void registerReceive(SJSocket s) {
        for (SJSelector sel : transportSelectors) sel.registerReceive(s);
    }

    public SJSocket select(final int mask) throws SJIOException {
        ExecutorService executor = Executors.newFixedThreadPool(transportSelectors.size());
        final ValueLatch<SJSocket> latch = new ValueLatch<SJSocket>();
        
        for (final SJSelector sel : transportSelectors) {
            executor.submit(new Callable<Object>() {
                public Object call() throws SJIOException {
                    latch.submitValue(sel.select(mask));
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

}

