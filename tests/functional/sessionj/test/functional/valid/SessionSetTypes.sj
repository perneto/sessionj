package sessionj.test.functional;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

/*
 * New feature: session set types
 * (required by events implementation)
 */
public class SessionSetTypes extends AbstractValidTest {
    public void client(int port) throws Exception {
        protocol foo { cbegin.!<int>, cbegin.!<boolean> }
        final noalias SJService chan = SJService.create(foo, "", port);
        final noalias SJSocket s;
        try (s) {
            s = chan.request();
            s.send(42);
        } finally {}
    }

    public void server(int port) throws Exception {
        protocol pserver { sbegin.?(int) }

        final noalias SJServerSocket ss;
        final noalias SJSocket s;
        try (ss) {
            ss = SJServerSocket.create(pserver, port);
            try (s) {
                s = ss.accept();
                int i = s.receiveInt();
                assert i == 42;
            } finally {}
        } finally {}
    }
}
