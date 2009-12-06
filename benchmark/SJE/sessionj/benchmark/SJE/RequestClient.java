package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import java.util.Random;

public class RequestClient implements Client {
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
    
    public RequestClient() {
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
    final public static long jlc$SourceLastModified$jl = 1259273905000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYa2wUVRS+u213+1hst7ZIaKE8qrahbNGIAZqIdUOhsIWy" +
       "W6tUSTudubu9ZXZm\nmLnTLgSJ+AIxMSGCr4j80RiNP3xE/WPUxPcrJpgU/s" +
       "AfjJIoRv4oMYiee+/s7OzsdolN5u6de885\n99xzvvOYvnkJ1VgmilnYsoiu" +
       "TcfoPgNbfNQnp7FMrVhq67BkWliJq5JljcDGuDxxjYy+HX1kSxAF\nxlCzpv" +
       "erRLJGpkzdzkyNTBErZ6Jlhq7uy6g6dSSWyNiw8ursd4e3tlWhxjHUSLQUlS" +
       "iR47pGcY6O\noUgWZyexafUrClbGUFTDWElhk0gq2Q+EugYHWySjSdQ2sZXE" +
       "lq7OMMJmyzawyc/MLyZQRNY1i5q2\nTHXToqgpMS3NSL02JWpvgli0L4FCaY" +
       "JVxdqLDqJgAtWkVSkDhAsT+Vv0com9A2wdyOsJqGmmJRnn\nWar3EE2hqMPP" +
       "4d64cxsQAGs4i+mU7h5VrUmwgJqFSqqkZXpT1CRaBkhrdBtOoWjxvEKBqNaQ" +
       "5D1S\nBo9TtMhPNyy2gKqOm4WxUNTqJ+OSwGeLfT7zeGtHKPLP0eG/loHHQW" +
       "cFyyrTPwRMS31MSZzGJtZk\nLBiv2LHjg7vs9iBCQNzqIxY0/Td/cG/i4scd" +
       "gqatDM0OjsVx+eqd7UtO9/9UV8XUqDV0izAoFN2c\ne3XY2enLGYDuha5Eth" +
       "nLb36S/GLXw2/gX4OodhCFZF21s9ogqsOaEnfmYZgniIbF6o502sJ0EFWr\n" +
       "fCmk83cwR5qomJmjBuaGRKf4PGcghMLwBOBZicRfPRsAgEm818YWjasEazRm" +
       "TTOOaI6NN8wGAqBy\nuz98VMDaFl1VsDkuv3bhmwObtj15RDiDAcg5kaJON5" +
       "AnwQdTWcncAwG8KVZ0IAoE+CE3MswJm/Sb\nprSPxULu0OklL3wpnQQLw00t" +
       "sh/ziwRmq9kITLdXTBXxQqANwkwCHIzLLQ9fXPzij69/HkRVZdNF\nwl0c0M" +
       "2spDKP5+Oj2TnOvwNA6fTDtdzZvx8denfu23NdBeCCkUriqZSTxcMKvxNMXc" +
       "YK5JuC+Of+\n3vLHMzXr3wuiaggySDNUAnBAzC71n1EUF335HMPuUpVADemS" +
       "i9dTMNCs58JsjPB5I7iiDp4qeFoc\nZDWzgcPIgyVwV4vvDjyDXXk0tObMhw" +
       "2fc6Pkk12jJyumMBWhEy1AZMTEGNbPPT/8zIlLhx+Aw3OG\nIWBBUciwJ1Ui" +
       "54DlpuJoYzdQGLJ+e6ev6enV1vscBXUkm7WpNKliyM6SquqzWBmnPD1FPamQ" +
       "ZyAw\nVmQSMhkkxXEVBAlzGIEZQHCZXBFb1HL82e6XzrBsYnB7tTLbcE1Rji" +
       "90BtjYVbLJ3tsZyFsK14b4\n3yMuEOlO7d46cWQFv7lXXI/zknMFVvOVapDU" +
       "UzFcBljVKaCJHPjz0Ds/nIgEUXAMhYk1QDRJZcay\ntov4KJNvfSL2f3Tvy1" +
       "e+p+e5awuAZIq15UrzyqjkiZV1czPR0FunskEUHkNNvGZKGh2VVJs5fgyq\n" +
       "nhV3FhNoQdF+cQUT6brPDbh2fzB4jvWHQiGfwZxRs3mtD/1ReLbB0+igv9GH" +
       "/kDAYJO1nGEpH5cL\nlwUpChsmmZEo6ByyeNORo6gug8FKEusPGHEz5OgCBJ" +
       "KSpuhZHlEI8f01riYso3fA0+Ro0lRek7tK\nNeEYXA9nLzBFchZ1v/h8Zs6Y" +
       "2Mifb6I7ro8pSFVUh5JWcO7EU2d3Dt1/X68osrdVFLEdXKD4gHVi\nUNv4Ue" +
       "ucFmTJKWRNe9wUtChFXYm8SMfH7FXMUltTYks0Gh4D/iv+rrGHGY69iBLZHN" +
       "ezBtRVc9lm\n4RmsGHljbiljTAooYtEC9gyBPZPYyBuyzb2qaWuUZDHraB3z" +
       "cJsGuCe2g2Fv9lnFvUKMXUFTRLvi\neDogLLm2As8QzKH/gqtkbY3IvG8VMj" +
       "rmFp2s71y1jodpQ1bQjbhoNyE5VVLFtaZXG5bL5SmiKkzG\nZpCxsqKTBfux" +
       "cOq1+zLHXwk66a/BYDm80ulJLGMyg0ttwdiTBi8b/iQ1bJIs9Fx5rmNLX/n5" +
       "3QvJ\nFlGURee8sqR59fKI7tmj4fJKJ3Dqz1Ytf/Ng8vykcFNzcVXapNnZta" +
       "fO4u67I3KZNqoK+nv2Eucg\n2sSGwTz6xucJ5SGAXr3MG6wUUbisHR58TYLS" +
       "t1Ywa/wenCElPmWq31KBy8O0QX3uy1eHLx/JuyJl\niHq00/kdBdew3zF+MU" +
       "6yTFRDVKiGPU6f58TOknnaSdFHMj8sme+bgfvg8P2XI09In+3O6/QgZFqq\n" +
       "G6tVPINVt1oWmsvVFRE7xL+cCimpKrWx+9aehl+g+Zqnr4w6i0kMn4maG1+8" +
       "4ZT+d8PZ4burX5/o\nTNvOqinyVZDXMVH6Sj4Ii5n6igtevVmsKDfa4qKmrx" +
       "eehQ5AFpZr+jyO9bU5QVEBuf85Eq7fDUFf\nGBKY5lyBQlD0lBdWuswqXNHn" +
       "B0sQi0r+PSA+YuUVpye6PjWiX/OG2v3QDMPXXtpWVW9v4JmHDBOn\nCVc4LD" +
       "oFg/8chE/48giGEIeRa/yQoD1EXdd7aAGv7txL/RhFtXlq9v64UaZuizYoh/" +
       "4Dod+MHWUR\nAAA=");
}
