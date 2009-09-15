//DISABLED
package sessionj.test.functional;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

/*
 * New feature: allow SJServerSockets to be non-final
 * (required by events implementation)
 */
public class NonFinalServerSocket extends AbstractValidTest {
    static final noalias protocol foo { !<int> } 
    public void client(int port) throws Exception {
        final noalias protocol pA { cbegin.!<int> }


        final noalias SJService c = SJService.create(pA, "", port);
        noalias SJSocket s;

        try (s)
		{
			s = c.request();
		    s.send(42);
		} finally {
		}
    }

    public void server(int port) throws Exception {
        final noalias protocol pB { sbegin.?(int) }
		noalias SJServerSocket ss;

		try (ss)
		{
            ss = SJServerSocket.create(pB, port);
            noalias SJSocket s;
            try (s) {
                s = doAccept(ss);
                int i = s.receiveInt();
                assert i == 42;
			} finally {
			}
		} finally {
		}
    }

    private SJSocket doAccept(noalias sbegin.?(int) ss) {
        try (ss) {
            return ss.accept();
        } finally {}
    }
}
