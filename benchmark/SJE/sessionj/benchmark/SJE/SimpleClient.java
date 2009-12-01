package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

public class SimpleClient implements Client {
    final SJProtocol getInt =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBozi1uDgzPy9Lr6SyILVYD8SFsIK9glKTUzPL" +
       "UkOA3PhkBghgZGJg\nqChiMMWjzRfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLp" +
       "eKtgUTA6MPA3cuRB1IqoRB1KcgP6cyPSe/\nRB9sjj5I2LoCvwODIVLoDgQa" +
       "zpqckZmTUsKg4QPTDjUWrl0fRTvIJmU0m0BkflJWanIJyC6IJRPZ\ng5eHp0" +
       "9ZAgkFUZiboTogahxVt4f6vNgvD1EjjUWNP9jU+OQ/ZjKylxyfcDKDnMxRkF" +
       "+cCQqrEgYJ\nRGCUlmTm6AdAZawrCgoqCoCxJodmaEBRZi5QBSyuJsotebb5" +
       "cZAIOChYsjPzgCGhjB7AKHpUvIGK\nrCsKGeoY2AoqgDYo4rMBrPqItuKauq" +
       "B7SRBvCsHVg1ys55pXmms6/0aqpgNPMsQVeYm5wGgW8slK\nLEvUz0nMS9cP" +
       "LinKzEsH+qmEgTkzrwQAJU+GqpECAAA="));
    final SJProtocol clientSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBvTi1uDgzPy9Lr6SyILVYD8SFsIK9nJ1S0zPz" +
       "QoC8+GQGCGBkYmCo\nKGJQw6MLSZNVzvTjSwM+dkA0aeDRFAyRQreL0YeBNT" +
       "kjMyelhEHDB6ZdH6xJH65dH0W7NdAmZTSb\nQGR+UlZqcgnILoglE9mDl4en" +
       "T1kCcZtoQX5OZXpOfglUB0SNo+r2UJ8X++UhaqSxqPEHmxqf/MdM\nRvaS4x" +
       "NOZpCTOQryizNLgPaXMEj4wDTpl5Zk5ugHQGWsKwoKKorxh0lQanJqZlkqtv" +
       "A3xaPNF8hO\nTE91zs/NLc3LTE4sgYer/DWJuVwq2hbgcOXOhagDSZUwiCLc" +
       "CQlTSFAWMtQxMBVUFABdKofm+4Ci\nzFygV2Dumyi35Nnmx0EiYLNZsjPzgF" +
       "GmjG4oih4Vb6AiiBWs4LBQxGcDWPURbcU1dUH3kiCBIARX\nDwpaPde80lzT" +
       "+TdSNR14kiGuyEvMBXpNyCcrsSxRPycxL10/uKQoMy8dGPglDMyZeSUA8LVA" +
       "QwQD\nAAA="));
    
    public String client(String domain, int port) {
        final SJService serv = SJService.create(clientSide, domain, port);
        int x = 0;
        SJSocket s = null;
        try {
            s = serv.request();
            x = SJRuntime.receiveInt(s);
            System.out.println("client:" + x);
        }
        catch (SJIOException e) { e.printStackTrace(); }
        catch (SJIncompatibleSessionException ee) { ee.printStackTrace(); }
        finally {
            SJRuntime.close(s);
        }
        return "" + x;
    }
    
    public static void main(String[] args) {
        new SimpleClient().client("", 1234);
    }
    
    public SimpleClient() { super(); }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259273898000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYa2wc1RW++/D6tWCvcUIU20mw3SZWyJo3KaaAWeXhsCGO" +
       "1wRwlTrjmbvr69x5\ndObOehIhxEMiKVUroSSEN3+CIip+QKK2P1rRSrxaaF" +
       "UplRL+wB+qFqmAyJ82QrzOvXdmdnZ2vVBL\nc3fm3nPOPY/vnHuuX/kUtTk2" +
       "yjvYcYhpLOXZIQs7YjQXlrDKnHxp17RiO1grUMVxZmFhXj3wNdn3\nWu6RnU" +
       "mUmEN9hjlJieLMLtqmW1mcXSSOZ6MNlkkPVajJfIkNMm4Z+XL5r0d2DaRQzx" +
       "zqIUaJKYyo\nBdNg2GNzKKtjfQHbzqSmYW0O5QyMtRK2iULJYSA0DdjYIRVD" +
       "Ya6NnRnsmLTKCfsc18K22DOYLKKs\nahoOs12VmbbDUG9xSakq4y4jdLxIHD" +
       "ZRRJkywVRzfoYeRMkiaitTpQKEq4uBFeNC4vh2Pg/kXQTU\ntMuKigOW9EFi" +
       "aAytj3OEFo/eBQTA2q5jtmiGW6UNBSZQn1SJKkZlvMRsYlSAtM10YReG1q4o" +
       "FIg6\nLEU9qFTwPENr4nTTcgmoOoVbOAtDq+JkQhLEbG0sZpFo7clkv3p8+n" +
       "8bIOKgs4ZVyvXPANO6GNMM\nLmMbGyqWjJfc/PGp+93BJEJAvCpGLGkmf/C7" +
       "e4of/3G9pBloQrNHYHFe/fKmwaFzk//sTHE1OizT\nIRwKdZaLqE77KxOeBe" +
       "heHUrki/lg8U8zb9//0K/xf5KoYwplVJO6ujGFOrGhFfz3dngvEgPL2T3l\n" +
       "soPZFEpTMZUxxTe4o0wo5u5og3dLYYvi3bMQQu3wJOBZh+RfBx8Y6ikR3aK4" +
       "QAk2WN5Z4gw5j4+X\nLycSoPFgPHsoQG2nSTVsz6unP3r3gW13/fyojAXHj7" +
       "8hQyNhHi9ACBZ1xT4I+bstH90PJRJijys4\n4qRHJm1bOcQzwXv43NDT7yjP" +
       "g3/BToccxsKMxHKaj8B0XctCUail2RS8KYCCebX/oY/XPvOPl99K\nolTTYl" +
       "EMJ7ebtq5QHu8gO/r87eIrAJPROFib7f3Z47vPnn/vg0012DI02pBNjZw8G4" +
       "bjMbBNFWtQ\nbWriT36x8/NjbT/6TRKlIcWgyDAFoAEZuy6+R11WTAQVhtuS" +
       "KqLucoPhXQwctBwxmI9ZPvRKnEAs\n+mMKiuJ06dHMNRf+0P2WsDioYz2Rgl" +
       "fCTGZFrhb/WRtjmP/gqeljJz498pMU5KFlyZgzlLHcBUpU\nD1iurE8krp7G" +
       "YfPJmYneX21xfitC3El03WXKAsVQeBVKzWWszTNReXKRKieKC3giuwBFCurd" +
       "PAVB\n0lYrUQV4NikD+TX9x58ce+4CLxSWcMwqnlBCU+SJiZEEHzc2LPLvQY" +
       "7g/prZkNoHpQHZsdL+XQeO\nDgvLo+I2+x9eKDApZpIg6YaWubCdHyiAGWZC" +
       "ZalB5sAv3t+7+757x2Wtu7aliLshtTUhp8Z/Ysq4\n/fVV540kR0nGWQrTvw" +
       "g6MYY2FQORPvL4p3wr7SrJpaDeX/3dBtQ2Jg/89+Ezfz+RhY3nUDtxthND\n" +
       "oTzazt0ye5ucBTERh1+/54VLf2MfCmzW0oWrP+A1Fr19SiSTt56v5jKvvqgn" +
       "Ufsc6hXnuWKwfQp1\nOXLn4ER2Cv5kEV1Wt15/usqjZCIsB4PxVI1sG0/UdM" +
       "TbaU7N3ztkbgqanm/k39f84cDhH7Lm9xVM\n3YKDwt6wA4PRCsOa5SUSFme7" +
       "WTAPiXFDmHhtZe5iDzKwAseOwcRyH0MDYdhs12BEx7xF84EmakNC\n5MCPAa" +
       "KbYhEOwQAsM1jFpOof0/75lJCwvLEF2254h54CrNFdg6iiF5My1p9f83zX6O" +
       "atIrzduqSb\nDb3UWpsINKPa8CKmLhKqcRnXgoyRlpiV7E+0l07fWzl+Kunn" +
       "fbclCl4cndM20aERCFzwxLpT/zr7\n0Uy/PCtkOzfS0FFFeWRLF2wBO1zVag" +
       "dB/ebmq155cObDBennvvp6us1w9RtffB+P3ZFVmxzuKeJj\nYJwPWwPwFJuA" +
       "h7/fCsjpUsWRXyKaEHNbBB57QN+NLQJSuBNXSEM8uNY/bMEVYbqFnnznpemL" +
       "R4Mo\n3GnJInoHRIP/7vBWUHzYr+SoVsk3+w2InwFDK7Q5ssHhkRhaqZUVUT" +
       "hy38XsY8qb+wPVphjqZKa1\nheIqpk0q/ZaWoNstGvpaqUqVbh/beHX3v6Er" +
       "WKHhyfmTMxhuL0aYIqITUv7vTmh9zNa4PrnqwN7U\nIvlzUpQwWfUa7in1TB" +
       "P1ta7LrldUOG2trHjgok54xuG5zMeJ+BU9baSx5cNs0yM68LMf3F5xQPNa\n" +
       "nZc3oQhYWp3uChRKCXf+hQUljqAnKSYiwlaY/mloVy88Y/AM+HYNNLOrEcRC" +
       "4jBo44grbXPNay11\n/NYlmnGZQmdSoxeTv189KvrL9ILiBDipv6423kbrLp" +
       "lCz67Qqhw8V8ajBT2iTJ4Z14DTyb+TVL2a\nE6NK8+/r+eB6CSSSuUkuCzfc" +
       "KhManEGxUZFXox18YI0dlR//K2rxL1DTwLyTDNZk80bMfPivAFj0\nmuo3L/" +
       "UTe62Mve/dOz4C57HK1WkCU9lSeN8tBBIvrUPPIxa3+fVwJ0xWTaIJvl9Gy1" +
       "3ET3Uz4M5s\n9EbHD7c1Df9vkf8VUIfPHdj0hpX7i8RQcHNvh+tz2aU02tBE" +
       "3jOWjctE6Nwu2xtL/BxnaHXz2gvH\nE4xC12OS9iQLi1aEFipt+B6lfoahjo" +
       "Cafz8rYHXUS3wLshwapaISAAA=");
}
