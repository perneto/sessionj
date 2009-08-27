package sessionj.test.functional;

import org.testng.annotations.Test;

import java.util.concurrent.*;

/**
 *
 */
public abstract class AbstractValidTest {
    private static final int CLIENT_SLEEP = 500;
    private static final int TEST_PORT = 1234;

    protected abstract void server(int port) throws Exception;
    protected abstract void client(int port) throws Exception;
    
    @Test
    public void run() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<?> b = es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                server(TEST_PORT);
                return null;
            }
        });
        Future<?> a = es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                // The client needs to be started after the server
                Thread.sleep(CLIENT_SLEEP);
                client(TEST_PORT);
                return null;
            }
        });

        a.get();
        b.get();
    }
}
