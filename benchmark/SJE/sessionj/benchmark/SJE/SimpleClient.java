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
      ("H4sIAAAAAAAAAJVYa2wc1RW++/D6tWCvcULkR2Jsl8QKWfMobcBQMKs8HDbE" +
       "eE0AI3DGM3fX17nz\nYObOehIhBFQigapIKAlpaYE/IFTEDwhq+6MVrVTeUF" +
       "UKUuAP/AEBEg81f0qEKOXce2dmZ2fXS7E0\nd2fuPefc8/jOuef6+S9Rm2Oj" +
       "vIMdh5jGSp4dsrAjRnNpBavMyZf2zCq2g7UCVRxnHhYW1QPfkf0v\n5h7YnU" +
       "SJBdRnmNOUKM78sm26leX5ZeJ4NhqxTHqoQk3mS2yQcfXYt6vvHNkzmEI9C6" +
       "iHGCWmMKIW\nTINhjy2grI71JWw705qGtQWUMzDWStgmCiWHgdA0YGOHVAyF" +
       "uTZ25rBj0ion7HNcC9tiz2CyiLKq\naTjMdlVm2g5DvcUVpapMuozQySJx2F" +
       "QRZcoEU825G92LkkXUVqZKBQjXFwMrJoXEyZ18Hsi7CKhp\nlxUVByzpg8TQ" +
       "GNoU5wgtHr8RCIC1Xcds2Qy3ShsKTKA+qRJVjMpkidnEqABpm+nCLgwNrCkU" +
       "iDos\nRT2oVPAiQxvidLNyCag6hVs4C0Pr4mRCEsRsIBazSLT2ZbL/fXj26x" +
       "GIOOisYZVy/TPAtDHGNIfL\n2MaGiiXjOTd/fOZ2dyiJEBCvixFLmumf/PmW" +
       "4md/2yRpBpvQ7BNYXFS//dnQ8OnpjztTXI0Oy3QI\nh0Kd5SKqs/7KlGcBut" +
       "eHEvliPlj8+9xrt9/3HP48iTpmUEY1qasbM6gTG1rBf2+H9yIxsJzdVy47\n" +
       "mM2gNBVTGVN8gzvKhGLujjZ4txS2LN49CyHUDk8Cno1I/nXwgaGeEtEtiguU" +
       "YIPlnRXOkPP4eP5q\nIgEaD8WzhwLUdptUw/ai+uxHb92z48aHjspYcPz4Gz" +
       "I0FubxEoRgWVfsg5C/O/LR/VAiIfa4gCNO\nemTatpVDPBO8+08P//Z15Qnw" +
       "L9jpkMNYmJFYTfMRmC5vWSgKtTSbgTcFULCo9t/32cDj7/7h1SRK\nNS0WxX" +
       "Byp2nrCuXxDrKjz98uvgIwGY+DtdneXz2896Uzb3+wpQZbhsYbsqmRk2fDaD" +
       "wGtqliDapN\nTfzJb3b/+1jbVX9MojSkGBQZpgA0IGM3xveoy4qpoMJwW1JF" +
       "1F1uMLyLgYNWIwbzMcuHXokTiEV/\nTEFRnM79MnPpe3/tflVYHNSxnkjBK2" +
       "EmsyJXi/+8jTHMf/Cb2WMnvjxyRwry0LJkzBnKWO4SJaoH\nLBfWJxJXT+Ow" +
       "+eLUVO8j25w/iRB3El13mbJEMRRehVJzFWuLTFSeXKTKieICnsguQZGCerdI" +
       "QZC0\n1UpUAZ5NykB+Q//xxyZ+/x4vFJZwzDqeUEJT5ImJsQQfNzcs8u8hju" +
       "D+mtmQ2gelAdmJ0p17Dhwd\nFZZHxW31P7xQYFLMJEHST1vmwk5+oABmmAmV" +
       "pQaZA796/+a9t906KWvdZS1F3ASprQk5Nf4TM8Z1\nL687YyQ5SjLOSpj+Rd" +
       "CJMbSlGIj0kcc/5VtpT0kuBfX+kh82oLYxuec/95/614ksbLyA2omzkxgK\n" +
       "5dF2bpLZ2+QsiIk4/PItT577J/tQYLOWLlz9Qa+x6O1XIpm8/Uw1l3nhKT2J" +
       "2hdQrzjPFYPtV6jL\nkbsAJ7JT8CeL6Ly69frTVR4lU2E5GIqnamTbeKKmI9" +
       "5Oc2r+3iFzU9D0/E/+fccfDhz+IWt+X8HU\nLTgo7JFdGIxWGNYsL5GwONvP" +
       "BfOwGEfCxGsrcxd7kIEVOHYMJpb7GBoMw2a7BiM65i2aDzRRGxIi\nB64FiG" +
       "6JRTgEA7DMYRWTqn9M++dTQsLyyhZse+EdegqwRncNoopeTMrYdGbDE13jW7" +
       "eL8Hbrkm4+\n9FJrbSLQjGrDi5i6TKjGZVwGMsZaYlayP9peevbWyvGnk37e" +
       "d1ui4MXROWsTHRqBwAWPbnz6k5c+\nmuuXZ4Vs58YaOqooj2zpgi1gh4ta7S" +
       "CoX9l60fP3zn24JP3cV19PdxiufuVT7+OJ67Nqk8M9RXwM\nTPJhewCeYhPw" +
       "8PdrADldqjjyS0QTYn4Rgcc+0Hdzi4AUbsAV0hAPrvXFLbgiTFfTk68/M3v2" +
       "aBCF\nGyxZRK+HaPDfXd4aio/6lRzVKvlWvwHxM2B4jTZHNjg8EsNrtbIiCk" +
       "duO5t9UHnlzkC1GYY6mWlt\no7iKaZNKv60l6PaKhr5WqlKl6yY2X9L9KXQF" +
       "azQ8OX9yDsPtxQhTRHRCyo/uhDbFbI3rk6sO3pxa\nJm8kRQmTVa/hnlLPNF" +
       "Vf67rsekWF0wZkxQMXdcIzCc95Pk7Er+hpI40tH+abHtGBn/3g9ooDmtfq\n" +
       "vLwJRcDS6nRXoFBKuPMvLChxBD1JMRERtsb0XaFdvfBMwDPo2zXYzK5GEAuJ" +
       "o6CNI660zTWvtdTx\nW5doxmUKnUqNn03+Zf246C/TS4oT4KT+utp4G627ZA" +
       "o9u0KrBuAZgafNt6otuJNgu4rtOdeA08m/\nk1S9mhOjSvPvK/jgegkkkrlJ" +
       "Lgs3XCMTGpxBsVGRV6NdfGCNHZUf/wtq8S9Q08C8kwzWZPNGzHz4\nrwBY9J" +
       "rqtyj1E3utjb3/u3d8AM5jlavTBKaypfB+WAgkXlqHnkcs7vDr4W6YrJpEE3" +
       "y/jpa7iJ/q\nZsCd2eiNjh9uGxr+3yL/K6COnj6w5R9W7k2JoeDm3g7X57JL" +
       "abShibxnLBuXidC5XbY3lvg5ztD6\n5rUXjicYha7HJO1JFhatCC1U2vA9Sv" +
       "04Qx0BNf/+nYDVUS/xPT24XfmiEgAA");
}
