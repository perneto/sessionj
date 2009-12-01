package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

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
    final SJProtocol sendInt =
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
    private long start;
    private long end;
    
    public void server(int port, int numClients) {
        final SJSelector sel = SJRuntime.selectorFor(sendInt);
        SJServerSocket ss = null;
        SJSocket s = null;
        try {
            ss = SJServerSocketImpl.create(serverSide, port);
            try {
                {
                    SJServerSocket _sjtmp_ss = null;
                    _sjtmp_ss = ss;
                    ss = null;
                    sel.registerAccept(_sjtmp_ss);
                }
                while (numClients-- != 0) {
                    try {
                        System.out.println("miaou");
                        s = sel.select();
                        System.out.println("miaou2");
                        SJRuntime.send(5, s);
                        System.out.println("miaou3");
                    }
                    catch (Exception e) { System.out.println("1"); }
                    finally {
                        SJRuntime.close(s);
                    }
                }
            }
            catch (Exception e) { e.printStackTrace(); }
            finally {
                if (sel != null) sel.close();
            }
        }
        catch (Exception e) { System.out.println("3"); }
        finally {
            { if (ss != null) ss.close(); }
        }
    }
    
    public static void main(String[] args) {
        SimpleServer s = new SimpleServer();
        s.start = System.nanoTime();
        s.server(1234, 1234);
        s.end = System.nanoTime();
    }
    
    public SimpleServer() { super(); }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259171098000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYbWwcxRmeu7Pv/HGtfcZJI38kxjbEIeQMVUE0bktdNyYO" +
       "58S5c0IwBWe9O3ce\nZ2932Zk9byJEoZVIoCoSImnaqil/oIgqSBDU9kcRIA" +
       "Gl0FaVUinwB/6AWqSWqvnTRhWlfWdmd29v\n93yUk3ZuduadmffjeT9mz3+E" +
       "2qmN8hRTSkxjLc+OW5iK1lxZwyqj+dK+BcWmWJvRFUoXYWJZPfoJ\nOfx87j" +
       "t7kyixhPoMc1onCl1ctU2nsrq4SqhroxHL1I9XdJN5O8b22D328frvT+4bTK" +
       "GeJdRDjBJT\nGFFnTINhly2hbBVXV7BNpzUNa0soZ2CslbBNFJ2cAELTgIMp" +
       "qRgKc2xMi5iaeo0T9lHHwrY40x8s\noKxqGpTZjspMmzLUW1hTasqkw4g+WS" +
       "CUTRVQukywrtF70f0oWUDtZV2pAOHmgi/FpNhxcpaPA3kX\nATbtsqJif0nb" +
       "MWJoDG2LrggkHr8dCGBpporZqhkc1WYoMID6JEu6YlQmS8wmRgVI200HTmFo" +
       "YMNN\ngajDUtRjSgUvM7QlSrcgp4CqU6iFL2FoU5RM7AQ2G4jYLGStA+nsfx" +
       "5Z+NcIWBx41rCqc/7TsGhr\nZFERl7GNDRXLhVec/Om5O52hJEJAvClCLGmm" +
       "r/nVocKHL2+TNINNaA4ILC6rH988NHxx+oPOFGej\nwzIp4VBokFxYdcGbmX" +
       "ItQPfmYEc+mfcnXyn+5s4Hfo7/mkQdcyitmrpTNeZQJza0Ga+fgX6BGFiO\n" +
       "HiiXKWZzqE0XQ2lTvIM6ykTHXB3t0LcUtir6roUQysCTgOdGJH+dvGGop0Sq" +
       "lo4BzzVs5+kaX5Bz\nefv59UQCOB6Keo8OUNtr6hq2l9Wn33/rvj23P3xK2o" +
       "LjxzuQobHAj1fABKtVxT4G/rsnHz4PJRLi\njKs44qRGpm1bOc49wX3w4vCP" +
       "3lDOgX5BTkpOYCFGYr2Nt7Doiy0DxUzdzeagpwAKltX+Bz4c+PGf\nnnk9iV" +
       "JNg0UhGJw17aqic3v73tHnHRedAZiMR8Ha7Oy/PzL/wqXfvTtRhy1D4zFviq" +
       "/k3jAatYFt\nqliDaFPf/uy/9/7j8fYv/yKJ2sDFIMgwBaABHrs1ekaDV0z5" +
       "EYbLkiqg7nJM8C4GCloPCczbLG96\nJU7AFv0RBkVwuvLd9A1vv9j9upDYj2" +
       "M9oYBXwkx6Ra5u/0UbAzjYuz9cePzMRyfvSoEfWpa0OUNp\ny1nRierCki80" +
       "OhJnT+Ow+duFqd5Hd9FfChN3kmrVYcqKjiHwKrpurmNtmYnIkwtFORFcQBPZ" +
       "FQhS\nEO+WddhIymolagDPJmEgv6X/9A92/ORtHigsoZhN3KEEp8gVA2MJ3m" +
       "6PTfL3IY7g/rrY4NrHpADZ\nHaW79x09NSokD2+303txgw3bxQhPmte39IVZ" +
       "nlDqUCH3/fPBC388k02i5BLKEDpLDEXnyqL7Jfib\nhNLIFideOvTTK39g7w" +
       "nT1tHGGRt04zHjsBJyhFsu1XLp556oJlFmCfWKdKgY7LCiO9zwS5DQ6Iw3\n" +
       "WECfa5hvTE4yEk8F3jQURXro2CjO67EK+pya9zsktAVND2i2C54BeHq8kCn+" +
       "RYCUUTKRsHjnS2LB\nsGhHAqxmLJvUFF5BgIWYYjMa1+qCTaoQ/2tegnps65" +
       "N/fuH9Yr8METKLj8USaXiNzOSC7W6Lu8XV\nrU4Q1K/tvPr8/cX3VmSG62t0" +
       "oz2GU73piXfwjq9n1SYxvU03jYrweYjA/H/ys+pqmjc3M5SCJMa7\nX/F3s0" +
       "GNn4phiHvMhOxYB9PR771zcP7IHZNSmhtbbrEfRNEiQD4zZ9z60qZLRpJHuj" +
       "RdC8EiSRlD\nEwV/S0/9/FX2SvtKckrWLCF1/Ff+PuEPVwN/kdm2b8asWpCi" +
       "7ZHbMPgLwEOzfNXc1hRG7WXunQCi\ndAUSvsHEdB9Dg4GotmMwUsW8OPbUI3" +
       "SaENFnHhQ7EdFKIAIsKWIVB/jzTJeQyrypxbJ56EM1B9JU\nHYOoogqWe2y7" +
       "tOVc1/jOWwR6uquSbjFwsNbchBQa5oanD3WV6AIxs7DHWEs7e86UKT19R+X0" +
       "k0kv\n4oJ/yDi925IR9KuAQuIp9Ju82etb4kgTS/B+AcyQoYBcsAN/3x9S9F" +
       "2g6GtaimZocS3zlQeBMf5/\nqAkn97TgpIuKGqpENBxlRgFmtrdi5hu4QmJq" +
       "5ka/tsWq0KLd+tk3nlq4fMqXoejp9Fu+LO4GjI96\nqRHVU+NOr6LzgD28Ud" +
       "0opOUxbniju4GIbyePXM4+pLx2t8/a1xjqZKa1S8c1rAepMykOTcJuu1pi\n" +
       "aV7ckOrxIlW6dcf267v/AmXWBhVkzhssYrgOGgHyRWmpfObScltE1ig/udrg" +
       "wdQq+W1SJDWZB2MX\nv8ZFU43Zr8tuZFQobSCI6/yakIcn5+EkF4nrAnW8KT" +
       "eteTw9C0yEgNGqNIIiMS2hHXXXtppJNLHy\nRAg5sc03GLYCmXrhmYDnOk+m" +
       "65rJFAew2HGUcye+DzSXpH4/iV5hxc1Gus+F1Pjl5K83j4tivW1F\noT5GGu" +
       "/+8at9w41d8NkVSMWz7wivBz2p2v0LntBl0TGM4IL3sOt7W68oQXk5lZd3/Z" +
       "gg/P0G3pxy\nE0g49/c3UE1BOjgoSMdGRd49hQFOujG/846/qn78DNQWmJfq" +
       "/pysjomZD761wKTblL97JX/irI2x\n+H8X52ch7aqcnSZakkWn++mbfBvwWo" +
       "WqWMA1BtpESC8NI6C+bPiKzK9HW2IfsORnFnX04tGJV63c\nmxJH/qeQTAF1" +
       "lB1dD5e4oX7asnGZCB4zsuC1xN/PGNrcPPZCooRW8PqUpH2GBUErRAuRNuiH" +
       "qc8z\n1OFT8/dnBYzOuYn/AVqXgDjzEwAA");
}
