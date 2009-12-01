package sessionj.benchmark.SJthread;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import java.util.concurrent.*;

public class SimpleServer implements Server {
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
    final public static SJProtocol sendInt =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBtTi1uDgzPy9Lr6SyILVYD8SFsIK9glPzUkKA" +
       "7PhkBghgZGJgqChi\nMMWjxxfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLpeKtg" +
       "UTA6MPA3cuRB1IqoRB1KcgP6cyPSe/RB9s\njj5I2BpokwZe14Gl0B0INJw1" +
       "OSMzJ6WEQcMHph1qLFy7Pop2kE3KaDaByPykrNTkEpBdEEsmsgcv\nD0+fsg" +
       "QSCqIwN0N1QNQ4qm4P9XmxXx6iRhqLGn+wqfHJf8xkZC85PuFkBjmZoyC/OB" +
       "MUViUMEojA\nKC3JzNEPgMpYVxQUVBQAo0wOzdCAosxcoIqyVKgr5ZY82/w4" +
       "SAQcFCzZmXnAkFBGD2AUPSreQEXW\nFYUMdQxsBRVAGxTx2QBWfURbcU1d0L" +
       "0kiDeF4OpBLtZzzSvNNZ1/I1XTgScZ4oq8xFxgNAv5ZCWW\nJernJOal6weX" +
       "FGXmpQP9VMLAnJlXAgBZBif7jgIAAA=="));
    final SJProtocol serverSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBvTi1uDgzPy9Lr6SyILVYD8SFsIK9gp1S0zPz" +
       "QoC8+GQGCGBkYmCo\nKGJQw6MLSZNVzvTjSwM+dkA0aeCzCiKFbhejDwNrck" +
       "ZmTkoJg4YPTLs+WJM+XLs+inZroE3KaDaB\nyPykrNTkEpBdEEsmsgcvD0+f" +
       "sgTiNtGC/JzK9Jz8EqgOiBpH1e2hPi/2y0PUSGNR4w82NT75j5mM\n7CXHJ5" +
       "zMICdzFOQXZ5YA7S9hkPCBadIvLcnM0Q+AylhXFBRUAINfFW+Y5KVgC3xTPH" +
       "p8gezE9FTn\n/Nzc0rzM5MQSeKDKX5OYy6WibQEOVO5ciDqQVAmDKMKRkACF" +
       "hGMhQx0DU0FFAdCZcmheDyjKzAX6\noywVGpZyS55tfhwkAjabJTszDxhfyu" +
       "iGouhR8QYqgljBCg4IRXw2gFUf0VZcUxd0LwkSCEJw9aBw\n1XPNK801nX8j" +
       "VdOBJxniirzEXKDXhHyyEssS9XMS89L1g0uKMvPSgSFfwsCcmVcCAORIiZsB" +
       "AwAA\n"));
    private int throughput = 0;
    private long start;
    private long end;
    static class ServerThread extends SJThread {
        
        public void srun(SJSocket s) {
            try {
                SJRuntime.send(5, s);
            }
            catch (Exception x) {  }
            finally {
                SJRuntime.close(s);
            }
        }
        
        public ServerThread() { super(); }
        
        public SJThread spawn(final SJSocket s) {
            new Thread() {
                
                public void run() { ServerThread.this.srun(s); }
            }.start();
            return this;
        }
        
        final public static String jlc$CompilerVersion$jl = "2.3.0";
        final public static long jlc$SourceLastModified$jl = 1259319692000L;
        final public static String jlc$ClassType$jl =
          ("H4sIAAAAAAAAAJVXW2wUVRg+u9tubxvKrgVrL7SUoi3IrjHirYnSNCAtWynd" +
           "olKCZXbm7O5pZ2fG\nmbMX0CBqItgHEwVBieiDJETDixL1xUQTwPsTJuALvm" +
           "CURDH6YCAG0f+cM7M7O1sW3WTPnst/Pf/3\n/+ffE5dRvWWiqIUti+jabJTu" +
           "MrDFRz05i2VqRRNjE5JpYWVElSxrCg5m5J3XyWPvh5/f6Ee+aRTR\n9GGVSN" +
           "ZUxtRz6cxUhlhFE/UaurorrerUllgl48EV1wrf7hvrDKDWadRKtASVKJFHdI" +
           "3iIp1GoSzO\nJrFpDSsKVqZRWMNYSWCTSCrZDYS6BootktYkmjOxNYktXc0z" +
           "woiVM7DJdTqbcRSSdc2iZk6mumlR\ntDg+K+WlWI4SNRYnFh2Ko2CKYFWxnk" +
           "J7kD+O6lOqlAbCpXHHixiXGNvA9oG8mYCZZkqSscNSN0c0\nhaIeL0fJ4/5N" +
           "QACsDVlMM3pJVZ0mwQaKCJNUSUvHEtQkWhpI6/UcaKGo44ZCgajRkOQ5KY1n" +
           "KGr3\n0k2II6Bq4tfCWCha4iXjkiBmHZ6YuaK1ORj6e37iSi9EHGxWsKwy+4" +
           "PAtMzDNIlT2MSajAXj1Vz0\n4Oi2XJcfISBe4iEWNMMrP94av/Rpj6DpXIBm" +
           "M8fijHzt3q7us8M/NgWYGY2GbhEGhQrPeVQn7JOh\nogHoXlqSyA6jzuFnk5" +
           "9v2/se/sWPGkdRUNbVXFYbRU1YU0bseQPM40TDYndzKmVhOorqVL4V1Pka\n" +
           "riNFVMyuox7mhkQzfF40EEJN8AWvUBcSnyVsoKg1QbKGigHPeWxGrVnGEC6y" +
           "cVHB5wOLu7zZowLU\nNuqqgs0Z+fjFr59Zv+ml/SIWDD+2QorWlvI4CSHIZC" +
           "VzDvKXZkwsKVG30n7xM8VPkM/Htd7CMCju\naNg0pV0sN4rPne1+4wvpKNw4" +
           "eG6R3Zg75ivUsRGY7q5ZOkbKiTcKMwlwMSO37b3UceS7d8/4UWDB\n8hEvbW" +
           "7QzaykMgQ4+RKx1XlPADj9XvgupPu3+fGT5765MFAGMkX9VflVzcnyo88bFV" +
           "OXsQL1pyz+\n8F8bfz9Q/8CHflQHSQdlh0oAFsjhZV4dFXky5NQc5ksgjlpS" +
           "VY43QxT1gsthNobYsFggB2LR5jGQ\nl6urLwTvOv9JyxnusVPZWl0lMIGpyJ" +
           "NwOf5TJgak0AuvTxx47fK+7QHITMPgMUdFoLy1MqOYVQpD\ny68fDC1+eY31" +
           "EY9sE8lmc1RKqhgqsKSqegErM5SXoLCr3PEqAxcQSkK1gsI3o4Ig4aLhywMq" +
           "F6gH\n0fa2g4cG3zzPKobB74NnlmMg2+jzsfH2qkO27mLAbSt7Czk+JxwIDS" +
           "Z2jO3c38cddotbZS9s4Shf\nnaQb2BPiQCGbfPrPU28195YtXOUW183HXqHC" +
           "R1HQ4s+fI71s+qrypXffqDzzp2XfE3+EXpRO7xBF\nNFIZoPVaLrv27e/x4L" +
           "qQvEDZCNqPrXNXfq7UDzrX1Mzucf6UldEfSDw8eMedLT8D+m+Q2GF7cxLD\n" +
           "u60x45nORp7x0v/O+B7PjXjtCec7twQy5Es/yyk72ate6EqmIffdQNaZlYZy" +
           "+HRwA1rt+j4A36V2\nfee/vJq7SvrCATdySbUUcA9WnerK1hEKvE4QzJxGSR" +
           "ZHNUzh+hO6PIfpzeH+EIXKDaxW9Ws9YZIs\nPIZ5+7V+Zdmxn05enGwTGBEt" +
           "zYqqrsLNI9oarqbFYChdXksDpz69evmJPZM/JP22efeBeXmdKNyD\nuAv1LE" +
           "vv8QDQhgl4/6j9RBBNUj1Nx5Uth2aXH9n7Ku9PG4igAbzEbibMI+edZxvn2y" +
           "PFw/w6/Cav\nSp3c5maD/6yvqA7M3pUeFWwpZhAvaCKEYBsuPpGsa2vwjMMc" +
           "WrgRHWqpRmTe+goZPefajzb3r76f\nG9eSFXRloJpooKYp/MhrDXsh5AxRIe" +
           "wDcYfdDnuJPVbBzh7GFTWrhA2thsTxx9MHjzlRB7SIujhm\niDscpygAbbVY" +
           "DIkU+0d8rrMvM5EtRAsVgRsxoO8yex/B8MBKFCsiJIvYsK5WWvHA3TxrnqTw" +
           "18iQ\nCpqThrdVpSH4xhsoLiVVgQQ232KDxFesOtoK3rPf7cUiRSF3M+ZoG/" +
           "ivrRyArr3qz5b4SyD3nd05\ncMoIf8XbkVLb3gC9cyqnqq465655QcPEKcJN" +
           "bxBdhvADHorOGkZR1OhMuQ+64IJmI1LNRVFTae6m\nzoMUh5qtC/yWUkXfv6" +
           "+PCwepDgAA");
    }
    
    
    public void server(int port, int numClient) {
        SJServerSocket ss = null;
        SJSocket s = null;
        try {
            ss = SJServerSocketImpl.create(serverSide, port);
            while (numClient-- != 0) {
                try {
                    s = ss.accept();
                    {
                        SJSocket _sjtmp_s = null;
                        _sjtmp_s = s;
                        s = null;
                        new ServerThread().spawn(_sjtmp_s);
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
                finally {
                    SJRuntime.close(s);
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            { if (ss != null) ss.close(); }
        }
    }
    
    public static void main(String[] args) throws Exception {
        SimpleServer s = new SimpleServer();
        s.start = System.nanoTime();
        s.server(1234, 1234);
        s.end = System.nanoTime();
    }
    
    public SimpleServer() { super(); }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259319692000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAKVYbWwcxRmeu7Pv/HGtfYeTRv6KcUzjKORMqwbRuC11TUJs" +
       "zonxmSQYUXtvd+5u\nnL3d7c7s+RKhCIrUhCBRVSSlIGj+UCEQlSAI+qMIkI" +
       "DST1VKpcAf+AMCJErV/GmjitK+M7O7t7d7\nvrbqSTs3OzPvzPvxvB+zz36K" +
       "OqmNchRTSkxjPcdOWJiK1iyuY5XRXGF+UbEp1mZ1hdJlmFhV1z4n\nR57PfP" +
       "9gHMVWUNYwZ3Si0OWKbTrlynKF0LqNxixTP1HWTebuGNlj347PNn5/en4ogf" +
       "pWUB8xCkxh\nRJ01DYbrbAWlq7haxDad0TSsraCMgbFWwDZRdHISFpoGHExJ" +
       "2VCYY2O6hKmp1/jCLHUsbIszvcE8\nSqumQZntqMy0KUP9+XWlpkw5jOhTeU" +
       "LZdB4lSwTrGv0eOoXiedRZ0pUyLNya96SYEjtOHeDjsLyH\nAJt2SVGxR9Jx" +
       "nBgaQ9vDFL7EE7fBAiBNVTGrmP5RHYYCAygrWdIVozxVYDYxyrC003TgFIYG" +
       "N90U\nFnVZinpcKeNVhraF1y3KKVjVLdTCSRjaEl4mdgKbDYZsFrDW4WT6n2" +
       "cX/z4GFgeeNazqnP8kEI2G\niJZwCdvYULEkvOrkzs3d6QzHEYLFW0KL5ZqZ" +
       "635xR/7jV7fLNUMt1hwWWFxVP7txeOTSzAfdCc5G\nl2VSwqHQJLmw6qI7M1" +
       "23AN1b/R35ZM6bfG3pV3fe+wz+JI665lBSNXWnasyhbmxos24/Bf08MbAc\n" +
       "PVwqUczmUIcuhpKmeAd1lIiOuTo6oW8prCL6dQshlIInBs9eJH89vGGor0Cq" +
       "lo4BzzVs5+g6J8jU\nefvFjVgMOB4Oe48OUDto6hq2V9Wn3v/tPftve+CMtA" +
       "XHj3sgQ5O+HxfBBJWqYh8H/2UVGytaLngo\nisXEQddw2Em1zNi2coK7Q/2+" +
       "SyOPvqU8AUoGYSk5iYUssY0O3gLRV9tGi9mGr81BTwEorKoD9348\n+Nifnn" +
       "4zjhItI0beHzxg2lVF50b3XCTrHheeAaxMhBHb6uy/nF144fLv3p1sYJehiY" +
       "hLRSm5S4yH\nDWGbKtYg5DS2f+QfB//6cOfXX4yjDvAziDRMAXyA246Gz2hy" +
       "jWkvzHBZEnnUW4oI3gOGMzcCAvM2\nzZt+CRawxUCIQRGhrt6fvOHtl3vfFB" +
       "J7wawvEPUKmEnXyDTsv2xjAAd79yeLD5//9PRdCXBGy5I2\nZyhpOUWdqHUg" +
       "+VKzN3H2NA6bP1+c7n9oD31JmLibVKsOU4o6huir6Lq5gbVVJsJPJhDqRIQB" +
       "TaSL\nEKkg6K3qsJGU1YrVAJ4tYkFu28C5H+96/G0eLSyhmC3cqwSnqC4Gds" +
       "R4uzMyyd+HOYIHGmKDfx+X\nAqR3Fe6eXzszLiQPbrfbfan7GybFSBJ2ur6t" +
       "LxzgWaUBFXLP3+67+Mfz6TiKr6AUoQeIoehcWfSQ\nBH+LeBra4uQrd/z06h" +
       "/Ye8K0DbRxxobq0cBxRAk4wk2Xa5nkcxeqcZRaQf0iJyoGO6LoDjf8CmQ1\n" +
       "OusO5tEXmuabM5QMx9O+Nw2HkR44NozzRsCCPl/N+10S2mJNnxsnR+Dpd+Om" +
       "+BdRUobKWMzina8J\nghHRjvlYTVk2qSm8jJAeBCHGchiNqnbRJlXIBDU3Vf" +
       "1o9MkPX3h/aUDGCZnPd0RSapBG5nTBe6/F\nfePadieI1W/svvbZU0vvFWWu" +
       "yzb70n7Dqe698A7e9e202iK6J6DqEH4PUZj/TzXpaxCerKuvbGt9\nzfDmRg" +
       "YlH1NsJqj3WRLV32SQ10yj/H/tf6u7fwKyJe/e4u1mg6n+o59AbGUmpOEGYN" +
       "cefOf2hWNH\np6SyvtJ2i0OgKS3kLOfnjJtf2XLZiPNomqTrAejFKYN0mfe2" +
       "dK3LX2WvMF+QU7I4CqjjX/L3OX+4\nGviLTOvZWbNqQS1gj92KwScBgprlqW" +
       "ahJVQ7SzwCAFCTZagsDGmSLENDvqi2YzBSxbwKd9UjdBoT\nEa4Aip0MacUX" +
       "AUiWsIp9eLumi0ll7m1DtgB9KBtBmqpjEFWU23KP7Ze3PdEzsfsmAc7eqly3" +
       "7Dtx\ne24CCg1yw1OUWiG6QEwe9tjR1s6ur6YKTx0tn3sy7kZ1cD+h5m8IDc" +
       "7z5pCn+ruiqk/w/hJvxkH3\nVFw9wAopCsCdk062HNDzKuj5uraSGVpUyZzy" +
       "aBu+ii0gIfjigYuKMq1ANBxmBgMzO9sx8x1cJhEt\nc5t/uQ1VgGif/shbP1" +
       "u8csaT4ZgbItY8WeqbMD7uZl/UyL673aLRxfV42/pUiMzD6MhmFxERQk8f\n" +
       "u5L+gfLG3R5/32Kom5nWHh3XsO6n6Lg4OQ677WmLpwVxHWvEjETh5l07r+/9" +
       "CMq5TSrVjDu4hOHu\nafjoFyWs8j+XsNtDsob5ydSGbk9UyK/jInnKfBu5ZT" +
       "YTTTdn2R67mVGhtEE/tnfDsweeURcso6HY\nLqDHm/WWtZWrZwGMADralWAb" +
       "3OeEsSMpqGYSTVCeCsAnsvkmw8yXidcLk/DkXJlyrWSKoljsOM6b\n77aWon" +
       "EHCt+Vxe1J+s/FxMSV+C+3TogLQUdRoR4+mj8yRL8hNH0aEDz2+BLx7DvWRi" +
       "LPw/pFZcur\ntJz8jhDhnb/fwJsH6zEkHPqHm2hCBkienXRslOW9Vuj7bD3i" +
       "Zu7x1zSOn4VyAvMbgDcni25i5vzv\nODBZb8mfI/kTZ20Ovf+65n8UMq3K2W" +
       "mhJVnL1tvbOyrd/rqKLZ4YxQH3A3SrUIgL5EbwGwvorGkE\nVJsO3sr5jWxb" +
       "5MOZ/Lyjjl9am3zdyvxGwsr7BJPKo66So+vBqjrQT1o2LhHBY0rW2Jb4eyZY" +
       "Y0Rj\nMUNdXldw/bSk+jnzI1mACsKv3w+ufg528Vbz9+cF2C7UY/8G47FPvn" +
       "UUAAA=");
}
