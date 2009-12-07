package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import java.util.Random;

public class IntClient implements Client {
    final SJProtocol reqRep =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBtTi1uDgzPy9Lr6SyILVYD8SFsIK9glPzUkKA" +
       "7PhkBghgZGJgqChi\nMMWjxxfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLpeKtg" +
       "UTA6MPA3cuRB1IqoRB1KcgP6cyPSe/RB9s\njj5I2BpokwZe14Gl0B0INJw1" +
       "OSMzJ6WEQcMHph1qLFy7Pop2kE3KaDaByPykrNTkEpBdEEsmsgcv\nD0+fsg" +
       "QSCqIwN0N1QNQ4qm4P9XmxXx6iRhqLGn+wqfHJf8xkZC85PuFkBjmZoyC/OB" +
       "MUViUMEojA\nKC3JzNEPgMpYVxQUVBTjD5Og1OTUzLJUzEgrZKhjYCyoKADq" +
       "l0NzU0BRZi7QApiuiXJLnm1+HCQC\nDkmW7Mw8YEAqo8cPih4Vb6Aia7AVbG" +
       "AXKuKzAaz6iLbimrqge0mQUBKCqwd5WM81rzTXdP6NVE0H\nnmSIK/ISc4Gp" +
       "RMgnK7EsUT8nMS9dP7ikKDMvHRgkJQzMmXklQEtl0C3NSUxO9cjPSUktik9e" +
       "/vhU\njat3ZwfCOJBrBUHaBUCG6oEM1YMYCgDIBMmkGQMAAA=="));
    final SJProtocol clientSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBvTi1uDgzPy9Lr6SyILVYD8SFsIK9nJ1S0zPz" +
       "QoC8+GQGCGBkYmCo\nKGJQw6MLSZNVzvTjSwM+dkA0aeDRFAyRQreL0YeBNT" +
       "kjMyelhEHDB6ZdH6xJH65dH0W7NdAmZTSb\nQGR+UlZqcgnILoglE9mDl4en" +
       "T1kCcZtoQX5OZXpOfglUB0SNo+r2UJ8X++UhaqSxqPEHmxqf/MdM\nRvaS4x" +
       "NOZpCTOQryizNLgPaXMEj4wDTpl5Zk5ugHQGWsKwoKKoDBr4o3TPJSsAW+KR" +
       "49vkB2Ynqq\nc35ubmleZnJiCTxQ5a9JzOVS0bYAByp3LkQdSKqEQRThSEiA" +
       "QsKxkKGOgQnsSnwxF5SanJpZlorp\nUJB2roKKAqB+ObSQCyjKzAUGA0zXRL" +
       "klzzY/DhIBO40lOzMPGN3K6G5C0aPiDVQEcSEr2IWK+GwA\nqz6irbimLuhe" +
       "EiQMheDqQdGi55pXmms6/0aqpgNPMsQVeYm5wJAR8slKLEvUz0nMS9cPLinK" +
       "zEsH\nRlwJA3NmXgnQUhl0S3MSk1M98nNSUovik5c/PlXj6t3ZgTAO5FoRkH" +
       "YBkKF6IEP1IIYCAIovC9WM\nAwAA"));
    private static Random generator = new Random(System.currentTimeMillis());
    private String requestString;
    
    public IntClient() {
        super();
        requestString = new String("Number " + generator.nextInt() % 1024 +
                                   " is beeing send");
    }
    
    public String client(String domain, int port) {
        final SJService serv = SJService.create(clientSide, domain, port);
        int x = 0;
        SJSocket s = null;
        try {
            s = serv.request();
            SJRuntime.send(requestString, s);
            x = SJRuntime.receiveInt(s);
        }
        catch (SJIOException e) {  }
        catch (SJIncompatibleSessionException ee) {  }
        finally {
            SJRuntime.close(s);
        }
        return "" + x;
    }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259273909000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYaWwbRRQeO4mdwyVxSEKUo0nbAKlIHUAUAZEowWpoWqdN" +
       "7RDAUCXr3bEzyXp3\n2Z1N3AoqytVSJCREyyWOPyAE4ge0Av4gQKLlRkhFav" +
       "un/AEBEofgD1SI683Mer1eO0ZE2vHszHtv\n3rz3vWPz6k+owTJRzMKWRXRt" +
       "MUb3Gtjio55ZxDK1Yqnt05JpYSWuSpY1Axtz8vzfZPb16L3bgiiQ\nRu2aPq" +
       "4SyZpZMHU7tzCzQKyCiQYNXd2bU3XqSKyQcd2GP1c+O7i9tw61plEr0VJUok" +
       "SO6xrFBZpG\nkTzOZ7BpjSsKVtIoqmGspLBJJJXsA0Jdg4MtktMkapvYSmJL" +
       "V5cZYbtlG9jkZxYXEygi65pFTVum\numlR1JZYlJalUZsSdTRBLDqWQKEswa" +
       "pi3Yn2o2ACNWRVKQeEXYniLUa5xNEJtg7kzQTUNLOSjIss\n9UtEUyga8HO4" +
       "Nx7aAQTAGs5juqC7R9VrEiygdqGSKmm50RQ1iZYD0gbdhlMo6llVKBA1GpK8" +
       "JOXw\nHEXdfrppsQVUTdwsjIWiTj8ZlwQ+6/H5zOOtXaHIX4enfx8Ej4POCp" +
       "ZVpn8ImNb6mJI4i02syVgw\nnrdjRyZvs/uCCAFxp49Y0Ixf/NbNie/fHRA0" +
       "vVVodnEszsl/Xt3Xf2r8m6Y6pkajoVuEQaHs5tyr\n087OWMEAdHe5EtlmrL" +
       "j5XvKD2+55Bf8QRI2TKCTrqp3XJlET1pS4Mw/DPEE0LFZ3ZbMWppOoXuVL\n" +
       "IZ2/gzmyRMXMHA0wNyS6wOcFAyEUhicAzwYk/prZQFFkUqNxlWCNxqxFRh0t" +
       "sPGClUAA1O3zh44K\nONumqwo25+SXvv7krq07HjokHMHA45xG0aAbxBmw/0" +
       "JeMpcgeLfG3MNQIMAPuJBhTdhi3DSlvSwG\nCgdO9T/1ofQsWBZuaJF9mF8g" +
       "sFLPRmC6smaKiJcCbBJmEvh/Tu645/uep798+WQQ1VVNEwl3cUI3\n85LKPF" +
       "2Mi3bnOP8OAGTID9NqZ/98eOr46U/PDZcAS9FQRRxVcrI4WO93gKnLWIE8Ux" +
       "L/xB/bfnms\n4do3gqgeggvSC5UAFBCra/1nlMXDWDG3sLvUJVBLtuLizRQM" +
       "tOK5MBsjfN4KrmiCpw6eDgdR7Wzg\nEPLgCNzV4bsDz1zn7wtdfubtlpPcKM" +
       "Uk1+rJhilMRchESxCZMTGG9XNPTj929KeDt8PhBcMQsKAo\nZNgZlcgFYLmo" +
       "PMrYDRSGrB+PjbU9ssl6k6OgieTzNpUyKoasLKmqvoKVOcrTUtSTAnnmAWNF" +
       "MpDB\nIBnOqSBImMMILAOCq+SIWHfHkcc3PnOGZRGD26uT2YZrigp8YSjAxu" +
       "GKTfbex0DeUbo2xP2SuEBk\nY2rP9vlD6/nNveJGnJeCK7Cer9SDpJGa4TLB" +
       "qk0JTeSu3w4c++JoJIiCaRQm1gTRJJUZy9op4qNK\nnvWJ2PfOzc+d/5x+xV" +
       "1bAiRTrLdQmVNmJU+sXHN6ORp67fl8EIXTqI3XSkmjs5JqM8enodpZcWcx\n" +
       "gdaU7ZdXLpGmx9yA6/MHg+dYfyiUchnMGTWbN/rQH4VnBzytDvpbfegPBAw2" +
       "2cwZ1vJxnXBZkKKw\nYZJliYLOIYs3GwWKmnIYrCSxvoARt0NzUIJAUtIUPc" +
       "8jCiG+f7mrCcvkA/C0OZq0Vdfk+kpNOAav\nhbPXmPhOG1tU1Pvy85k5Y2Kj" +
       "eL6JrvpvTEGqojqUspJz5x8+u3vq1ltGRXG9oqaIneACxQeso5Pa\nlnc6T2" +
       "tBlpxC1qLHTUGLUjScKIp0fMxexSy1PSW2RIPhMeA/4u9v9jDDsRdRGtvjet" +
       "6AemoO3iQ8\ngxWjaMxtVYxJAUUsWsCeIbBnEhtFQ/a6VzVtjZI8Zp2sYx5u" +
       "0wD3xE4w7MU+q7hXiLEraIpoUxxP\nB4QlN9fgmYI59F1wlbytEZn3q0LGwO" +
       "nuZ5uHLruGh2lLXtDNuGg3ITnVUsW1plcblsvlBaIqTMZN\nIGNDTScL9kfD" +
       "qZduyR15IeikvxaD5fBapyexjMkyrrQFY08avGz4k9S0SfLQaxW5Hl37wrfH" +
       "v052\niKIsOuYNFU2rl0d0zR4N19U6gVOfuGzdq/uTX2WEm9rLq9JWzc5vfv" +
       "4s3nhDRK7SQtVBX89e4hxE\nW9kwWUTf3CqhPAXQa5Z5g5UiCpe1y4OvDCh9" +
       "aQ2zxm/EOVLhU6b6JTW4PEzXqU98+OL0r4eKrkgZ\noh7tdn5nwTXsN80vxk" +
       "kGRTVEpWo44vR5Tuz0r9JKij6S+aF/tW8F7oODt/4aeVA6saeo0x2Qaalu\n" +
       "bFLxMlbdallqLjfVROwU/2IqpaS61JaNl460fAfN1yp9ZdRZTGL4PNTc+OIN" +
       "p/S/G84B3139+kSX\ne3fXLZCPgryOidJX8SFYzjRWXvCazXJFudF6ypq+UX" +
       "i6HIB0VWv6PI71tTlBUQG5/zkS/rsbgr4w\nJDDNuQKloBipLqxymVVX99OD" +
       "JYfuin8JiA9Xef2p+eH3jejHvJl2Py7D8IWXtVXV2xd45iHDxFnC\nlQ2LLs" +
       "HgP/vhs706eiG8YeTa3i1oD1DX7R5a0Nude6nvp6ixSM3eHzCq1GzRAhXQvx" +
       "TaVPFZEQAA\n");
}
