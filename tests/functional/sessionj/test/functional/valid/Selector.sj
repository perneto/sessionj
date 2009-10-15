//DISABLED
package sessionj.test.functional;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

/*
 * New feature: selector
 * (required by events implementation)
 */
public class Selector extends AbstractValidTest {
    protocol startServer  sbegin.?(int)
    protocol middleClient cbegin.!<int>

    public void server(int port) throws Exception {
    }
    
    public void client(int port) throws Exception {
    }

}
