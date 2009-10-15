package sessionj.test.functional;

import org.testng.annotations.Test;

import java.util.concurrent.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;

public abstract class BaseValidTest {
    private static final int STARTING_DELAY = 400;
    protected static final int TEST_PORT = 1234;

    protected abstract List<Callable<?>> peers();
    private final ExecutorService es = Executors.newCachedThreadPool();

    @Test
    public void run() throws Exception {
        Collection<Future<?>> futures = new LinkedList<Future<?>>();
        List<Callable<?>> peers = peers();
        
        for (int i=0; i< peers.size(); ++i) {
            futures.add(es.submit(
                i == 0 ? peers.get(i) : wrapDelay(peers.get(i))
            ));
        }
        for (Future<?> f : futures) f.get();
        es.shutdown();
    }

    private Callable<?> wrapDelay(final Callable<?> peer) {
        return new Callable<Object>() {
            public Object call() throws Exception {
                Thread.sleep(STARTING_DELAY);
                return peer.call();
            }
        };
    }
}
