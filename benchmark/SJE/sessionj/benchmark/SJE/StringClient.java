package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import java.util.Random;

public class StringClient implements Client {
    final SJProtocol reqRep =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBtTi1uDgzPy9Lr6SyILVYD8SFsIK9glPzUkKA" +
       "7PhkBghgZGJgqChi\nMMWjxxfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLpeKtg" +
       "UTA6MPA3cuRB1IqoRB1KcgP6cyPSe/RB9s\njj5I2BpokwZe14Gl0B0INJw1" +
       "OSMzJ6WEQcMHph1qLFy7Pop2kE3KaDaByPykrNTkEpBdEEsmsgcv\nD0+fsg" +
       "QSCqIwN0N1QNQ4qm4P9XmxXx6iRhqLGn+wqfHJf8xkZC85PuFkBjmZoyC/OB" +
       "MUViUMEojA\nKC3JzNEPgMpYVxQUVBTjD5Og1OTUzLJUzEgrZKhjYCyoKADq" +
       "l0FzU0BOYnKqR35OSmpRfPLyx6dq\nXL07O8DhyJKXmAuMHSGfrMSyRP2cxL" +
       "x0/eCSosy8dKBTShgEQKJ6IFE9iCjIDj4AeMRHflcCAAA="));
    final SJProtocol clientSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBvTi1uDgzPy9Lr6SyILVYD8SFsIK9nJ1S0zPz" +
       "QoC8+GQGCGBkYmCo\nKGJQw6MLSZNVzvTjSwM+dkA0aeDRFAyRQreL0YeBNT" +
       "kjMyelhEHDB6ZdH6xJH65dH0W7NdAmZTSb\nQGR+UlZqcgnILoglE9mDl4en" +
       "T1kCcZtoQX5OZXpOfglUB0SNo+r2UJ8X++UhaqSxqPEHmxqf/MdM\nRvaS4x" +
       "NOZpCTOQryizNLgPaXMEj4wDTpl5Zk5ugHQGWsKwoKKoDBr4o3TPJSsAW+KR" +
       "49vkB2Ynqq\nc35ubmleZnJiCTxQ5a9JzOVS0bYAByp3LkQdSKqEQRThSEiA" +
       "QsKxkKGOgQnsSnwxF5SanJpZlorp\nUJB2roKKAqB+GbSQC8hJTE71yM9JSS" +
       "2KT17++FSNq3dnB9hhLHmJuUAXCflkJZYl6uck5qXrB5cU\nZealAwOshEEA" +
       "JKoHEtWDiILsEAQAx8CvtsoCAAA="));
    private static Random generator = new Random(System.currentTimeMillis());
    private String requestString;
    
    public StringClient() {
        super();
        requestString = new String("Number " + generator.nextInt() % 1024 +
                                   " is beeing send");
    }
    
    public String client(String domain, int port) {
        final SJService serv = SJService.create(clientSide, domain, port);
        String x = null;
        SJSocket s = null;
        try {
            s = serv.request();
            SJRuntime.send(requestString, s);
            x = (String) SJRuntime.receive(s);
        }
        catch (SJIOException e) {  }
        catch (SJIncompatibleSessionException ee) {  }
        catch (ClassNotFoundException cnf) {  }
        finally {
            SJRuntime.close(s);
        }
        return (String) x;
    }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259273924000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYa2xURRSe3ba7fSy2WwsSWiiPqhDKVo0YoIlYN1QKWyi7" +
       "FbHElNt7Z7dT7os7\nc9uFIBFfICYmRvAVH380RuMPH1H/GDXx/YoJJugf+Y" +
       "NREsXoHyXG15mZu3fv3t0uscnMzp05Z+Y8\nvnPmTF++gJqog1IUU0oscybF" +
       "DtqYit6amsEqo6nctjHFoVhL6wql47Awqe77h+x+NXn31iiKTKBO\n0xrSiU" +
       "LHpx3LLUyPTxNadNBy29IPFnSLeTtW7bFp1V9zXxzb1t2A2idQOzFzTGFETV" +
       "smw0U2gRIG\nNqawQ4c0DWsTKGlirOWwQxSdHAJCy4SDKSmYCnMdTLOYWvos" +
       "J+ykro0dcWZpMoMSqmVS5rgqsxzK\nUEdmRplVBlxG9IEMoWwwg2J5gnWNHk" +
       "BHUDSDmvK6UgDCRZmSFgNix4FhPg/krQTEdPKKikssjfuJ\nqTHUG+bwNe7b" +
       "DgTAGjcwm7b8oxpNBSZQpxRJV8zCQI45xCwAaZPlwikMLZl3UyBqthV1v1LA" +
       "kwwt\nDtONySWgahFm4SwMLQyTiZ3AZ0tCPgt4a2cs8feJsT+Wg8dBZg2rOp" +
       "c/BkzLQkxZnMcONlUsGS+6\nqZMjt7s9UYSAeGGIWNIMXfnWrZnz7/ZKmu4a" +
       "NDsFFifVv27oWXp66PuWBi5Gs21RwqFQobnw6pi3\nMli0Ad2L/B35Yqq0+F" +
       "72o9vvegn/FEXNIyimWrprmCOoBZta2hvHYZwhJpazO/N5itkIatTFVMwS\n" +
       "32COPNExN0cTjG2FTYtx0UYIxaFFoPUh+dfKO4bapYvTOsEmS9EZzpAs8v6y" +
       "uUgEJO4JR48OUNtq\n6Rp2JtUXzn12eMv2B45LX3D8eAcytMqP4ylwwbShOP" +
       "shfrekguehSESccTlHnLTIkOMoB3kkFI+e\nXvrEx8rTYF/Qk5JDWKgRmWvk" +
       "PTBdVzdRpMthNgIjBVAwqXbddX7Jk1+/+GEUNdRMFhl/cthyDEXn\n/i5FR6" +
       "d3XHgFYNIXBmuts385Mfr6mc+/W12GLUN9VdFUzcmjYWXYB46lYg2yTXn7x/" +
       "7c+usjTRvf\niKJGCDFIMkwBaEDELgufUREVg6UMw3VpyKC2fJXirQwMNBdQ" +
       "mPcJMW4HV7RAa4DW5eGqk3cCRQEo\ngbu6QjqI/HXxntg137zd9qEwSinVtQ" +
       "dyYg4zGTjJMkTGHYxh/rvHxx45deHYXji8aNsSFgzFbHdK\nJ2oRWK6ojDWu" +
       "gcaR9fNrgx0PraNvChS0EMNwmTKlY8jNiq5bc1ibZCI5JQOJUOQfMFZiCvIY" +
       "4HdS\nh42kOezILCC4RqZILe46+eiap77hucQW9lrIbSMkRUUx0Rfh/eqqRf" +
       "7dw0HeVVYbon+/VCCxJnfH\ntn3HVwrNg9v1ex9Ff8NGMdMIO/XXDZdhfueU" +
       "0UQO/370ta9OJaIoOoHihA4TU9G5segOGR81sm1o\ni0Pv3PrMxS/ZWeHaMi" +
       "C5YN3F6rSyWwnEyoYzs8nYK88aURSfQB3ixlRMtlvRXe74CbjzaNqbzKAF\n" +
       "FeuV95dM1oN+wPWEgyFwbDgUyukMxpyaj5tD6E9C2w6t3UN/ewj9kYjNB+sF" +
       "wzLRr5AuizIUtx0y\nqzCQOUZFyVFkqKWAwUoKrw44cSeUCGUIZBVTswwRUQ" +
       "iJ9Wt8SXg+74XW4UnSUVuSG6slERjcCGcv\ncPABF1MmU3Tl+dycXu4une+g" +
       "6y+NKUhVzIILrezcfQ9+u2t0z20D8oq9tu4WO8AFWghYp0bMze8s\nPGNGeX" +
       "KK0ZmAm6KUMbQ6U9rS8zH/lKPctpxckmVGwID/yr9/eOOG4x/yguxMW4YNt6" +
       "qz/BbpGazZ\nJWNurWFMBiji0QL2jIE9s9guGbLbV9VxTUYMzOtZzzzCphHh" +
       "iR1g2CtDVvFVSHEVTE0WK56nI9KS\n6+vwjMIYqi9QxXBNooqqVe7Re2bx06" +
       "19azeIMG0zJN24j3YHklM9UXxrBqXhuVydJrrG97gF9lhV\n18mS/eF47oXb" +
       "Ciefi3rpr83mObze6VmsYjKLq23B2bN2UbgoLTrhgi28Gyn5bs88gTAKjmtV" +
       "RXmS\nI5oww86Ad/aCUFfXESp9My6QKotw/1xVhyvAtEl/7OPnx347XlIkZ8" +
       "tsvsv73V2hmCBZLu8SVL5L\n+r0qyUPe0nlqMVmFcTsvna/eFm+FY3t+S9yv" +
       "fHCHVKSz8l7dYrrG+me/xWtuSqg16sAWZtnrdDyL\ndf9SKtdw6+oCY1Q8T8" +
       "qR35DbvObq/rYfocaZp3xLepNZDG8x04exqOuU/13X9YaMEpYnOdu9q2Ga\n" +
       "fBIV14W8YapeXZVMg5X3SqtTKahw55KK2moA2iIPSYtq1VYBBISqiai8aGQE" +
       "VF/YYw4x4PVRiqCH\nlz33w+vnsl3Sh/INuarqGRfkke/IQLSuqHeCoP5g7Y" +
       "qXj2TPTpXQnWeogXAIXqoaMiCdyqgU6kTK\nYd0f0JJ3tDjPNAR2Ivj64GXl" +
       "4qr/DcgXrLry9L7V79vJT0U97b8y4/DUy7u6HiwNAuOY7eA8EfLG\nZaFgi5" +
       "8j8H6vHYKgPvRC4Dsl7VHmQzJAC3Hkj4PU9zLUXKLm3/fZNa5tWQUV0X9rj5" +
       "cGYhEAAA==");
}
