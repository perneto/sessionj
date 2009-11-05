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
    protocol pClient2 cbegin.@(from2)
    protocol from3 ?(boolean)
    protocol pClient3 cbegin.@(from3)
    protocol pSel { @(from2), @(from3) }
    protocol pServer sbegin.@(pSel)

    public void peer1(int port) throws Exception {
        final noalias SJSelector sel = SJRuntime.selectorFor(pSel);
        noalias SJServerSocket ss;
        noalias SJSocket s;
        try (ss) {
            ss = SJServerSocket.create(pServer, port);
            try (sel) {
                sel.registerAccept(ss);
                int i = 0; boolean b; int j;
                while (i < 2) {
                    try (s) {
                        s = sel.select(SJSelector.ACCEPT | SJSelector.INPUT);
                        typecase (s) {
                            when (@(from2)) j = s.receiveInt();
                            when (@(from3)) b = s.receiveBoolean();
                        }
                    } finally {}
                    i++;
                }
            } finally {}
        } finally {}
    }
    
    public void peer2(int port) throws Exception {
        final noalias SJService serv = SJService.create(pClient2, "", port);
        noalias SJSocket s;
        try (s) {
            s = serv.request();
            s.send(42);
        } finally {}
    }

    public void peer3(int port) throws Exception {
        final noalias SJService serv = SJService.create(pClient3, "", port);
        noalias SJSocket s;
        try (s) {
            s = serv.request();
            s.send(false);
        } finally {}
    }
}
