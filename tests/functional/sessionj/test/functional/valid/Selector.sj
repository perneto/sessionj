//DISABLED
package sessionj.test.functional;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

/*
 * New feature: selector
 * (required by events implementation)
 */
public class Selector extends AbstractValidTest3Peers {
    protocol from2 ?(int)
    protocol from3 ?(boolean)
    protocol pSel { @from2, @from3 }

    public void peer1(int port) throws Exception {
        final noalias SJSelector sel = SJRuntime.selectorFor(pSel);
        noalias SJServerSocket ss;
        noalias SJSocket s;
        try (ss) {
            ss = SJServerSocket.create(sbegin.@pSel, port);
            try (sel) {
                sel.registerAccept(ss);
                int i = 0; boolean b; int j;
                while (i < 2) {
                    try (s) {
                        s = sel.select(SJSelector.ACCEPT | SJSelector.INPUT);
                        typecase (s) {
                            when (@from2) j = s.receiveInt();
                            when (@from3) b = s.receiveBoolean();
                        }
                    } finally {}
                    i++;
                }
            } finally {}
        } finally {}
    }
    
    public void peer2(int port) throws Exception {
        final noalias SJService serv = SJService.create(cbegin.!<int>, "", port);
        noalias SJSocket s;
        try (s) {
            s = serv.request();
            s.send(42);
        } finally {}
    }

    public void peer3(int port) throws Exception {
        final noalias SJService serv = SJService.create(cbegin.!<boolean>, "", port);
        noalias SJSocket s;
        try (s) {
            s = serv.request();
            s.send(false);
        } finally {}
    }
}
