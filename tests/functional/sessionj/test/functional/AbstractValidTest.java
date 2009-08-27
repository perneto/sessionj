package sessionj.test.functional;

import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.TransportUtils;

import java.util.concurrent.*;

import org.testng.annotations.Test;

/**
 *
 */
public abstract class AbstractValidTest {
    private static final int CLIENT_SLEEP = 500;
    private static final int TEST_PORT = 1234;

    protected abstract void server(int port, SJSessionParameters params) throws Exception;
    protected abstract void client(int port, SJSessionParameters params) throws Exception;
    
    @Test
    public void run() throws Exception {
        // "f" : shared-memory unbounded FIFO
        final SJSessionParameters params = TransportUtils.createSJSessionParameters("f", "f");

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<?> b = es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                server(TEST_PORT, params);
                return null;
            }
        });
        Future<?> a = es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                // The client needs to be started after the server
                Thread.sleep(CLIENT_SLEEP);
                client(TEST_PORT, params);
                return null;
            }
        });

        a.get();
        b.get();
    }
}
