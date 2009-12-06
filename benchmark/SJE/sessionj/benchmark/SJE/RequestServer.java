package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

public class RequestServer implements Server {
    final SJProtocol reqRep =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBozi1uDgzPy9Lr6SyILVYD8SFsIK9glKTUzPL" +
       "UkOA3PhkBghgZGJg\nqChiMMWjzRfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLp" +
       "eKtgUTA6MPA3cuRB1IqoRB1KcgP6cyPSe/\nRB9sjj5I2LoCvwODIVLoDgQa" +
       "zpqckZmTUsKg4QPTDjUWrl0fRTvIJmU0m0BkflJWanIJyC6IJRPZ\ng5eHp0" +
       "9ZAgkFUZiboTogahxVt4f6vNgvD1EjjUWNP9jU+OQ/ZjKylxyfcDKDnMxRkF" +
       "+cCQqrEgYJ\nRGCUlmTm6AdAZawrCgoqgJGmijdM8lIwY6yQoY6BsaCiAKhZ" +
       "Ds1BAUWZuUDTYfE8UW7Js82Pg0TA\nwciSnZkHDEVl9MhB0aPiDVRkDbaCDe" +
       "w8RXw2gFUf0VZcUxd0LwkSREJw9SDf6rnmleaazr+RqunA\nkwxxRV5iLjCJ" +
       "CPlkJZYl6uck5qXrB5cUZealA8OjhIE5M68EaKkMuqU5icmpHvk5KalF8cnL" +
       "H5+q\ncfXu7EAYB3KtIEi7AMhQPZChehBDAcpokJYZAwAA"));
    final SJProtocol serverSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBvTi1uDgzPy9Lr6SyILVYD8SFsIK9gp1S0zPz" +
       "QoC8+GQGCGBkYmCo\nKGJQw6MLSZNVzvTjSwM+dkA0aeCzCiKFbhejDwNrck" +
       "ZmTkoJg4YPTLs+WJM+XLs+inZroE3KaDaB\nyPykrNTkEpBdEEsmsgcvD0+f" +
       "sgTiNtGC/JzK9Jz8EqgOiBpH1e2hPi/2y0PUSGNR4w82NT75j5mM\n7CXHJ5" +
       "zMICdzFOQXZ5YA7S9hkPCBadIvLcnM0Q+AylhXFBRUFOMPk6DU5NTMslRs4W" +
       "+KR5svkJ2Y\nnuqcn5tbmpeZnFgCD1f5axJzuVS0LcDhyp0LUQeSKmEQRbgT" +
       "EqaQoCxkqGNgAjtUFW/k5aVguhKk\nl6ugogCoWQ4t5AKKMnOBwQDz20S5Jc" +
       "82Pw4SAbuLJTszDxjdyugOQtGj4g1UBHEeK9h5ivhsAKs+\noq24pi7oXhIk" +
       "AIXg6kHRoueaV5prOv9GqqYDTzLEFXmJucBgEfLJSixL1M9JzEvXDy4pysxL" +
       "B0Zc\nCQNzZl4J0FIZdEtzEpNTPfJzUlKL4pOXPz5V4+rd2YEwDuRaEZB2AZ" +
       "CheiBD9SCGAgBsqMXOjAMA\nAA=="));
    private int throughput = 0;
    private long start;
    private long end;
    
    private int parse(String str) {
        int numEnd = str.indexOf(' ', 7);
        String strNum = str.substring(7, numEnd);
        return Integer.parseInt(strNum);
    }
    
    public void server(int port, int numClients) {
        final SJSelector sel = SJRuntime.selectorFor(reqRep);
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
                        s = sel.select();
                        String str = (String) SJRuntime.receive(s);
                        int x = this.parse(str);
                        SJRuntime.send(x, s);
                        throughput++;
                    }
                    catch (Exception e) {  }
                    finally {
                        SJRuntime.close(s);
                    }
                }
            }
            catch (Exception e) {  }
            finally {
                if (sel != null) sel.close();
            }
        }
        catch (Exception e) {  }
        finally {
            { if (ss != null) ss.close(); }
        }
    }
    
    public static void main(String[] args) throws Exception {
        RequestServer s = new RequestServer();
        s.start = System.nanoTime();
        s.server(1234, 1000);
        s.end = System.nanoTime();
    }
    
    public RequestServer() { super(); }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259080243000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAKVYa2wcRx2fu7Pv/LjGPtcxkR+x67jEkZtzebQqcaEYkzR2" +
       "z4l75yapK8vZ2527\nG2dvd7M7e95EVdSCIGmQkCBJH4hSPhQqUD+0KVAkUE" +
       "GipTyFFKS0X9ovRVAJisgXiFAp/Gdmd29v\n9+4C4qSdm535z8z/8fs/Zp9/" +
       "D3VaJspa2LKIrm1m6SkDW7zVi5tYpla2sLQimRZWFlTJslZhYkM+\n/gE58m" +
       "LmcwfjKLaGBjR9XiWStVoxdbtcWa0QyzHRhKGrp8qqTt0dI3vs2/X+1m/OLo" +
       "0kUN8a6iNa\ngUqUyAu6RrFD11C6iqtFbFrzioKVNZTRMFYK2CSSSk4Doa7B" +
       "wRYpaxK1TWzlsaWrNUY4YNkGNvmZ\n3mAOpWVds6hpy1Q3LYr6c5tSTZq1KV" +
       "Fnc8SiczmULBGsKtZJdAbFc6izpEplIBzKeVLM8h1nD7Bx\nIO8hwKZZkmTs" +
       "Lek4QTSFovHwCl/iqfuAAJamqphWdP+oDk2CATQgWFIlrTxboCbRykDaqdtw" +
       "CkXD\nLTcFoi5Dkk9IZbxB0Y4w3YqYAqpurha2hKLtYTK+E9hsOGSzgLUOJ9" +
       "P/Or/yjwmwOPCsYFll/Cdh\n0c7QojwuYRNrMhYLr9vZi4sP2qNxhIB4e4hY" +
       "0Mzf+vIDuXd/Mi5oRprQHOZY3JDfv3N07Mr8H7oT\njI0uQ7cIg0KD5NyqK+" +
       "7MnGMAuof8Hdlk1pv8af7nDz7yXfznOOpaRElZV+2qtoi6saYsuP0U9HNE\n" +
       "w2L0cKlkYbqIOlQ+lNT5O6ijRFTM1NEJfUOiFd53DIRQCp4YPB9D4sfeEQAw" +
       "j0/a2KIA6Bo2s9Ym\nW5FxWLttKxYDlkfD7qMC1g7qqoLNDfm5d3718P77Hj" +
       "snjMEA5J5I0ZTvyEWwQaUqmSfAgfdnGw5E\nsRg/5GaGOaGTedOUTjFfcB69" +
       "MvbU69LToGGQ1CKnMRckttXBWlj00bahYqHuaIvQkwAHG/LgI+8O\nf+3333" +
       "ktjhJNw0XOHzygm1VJZRb3/GPAPS48A0CZCsO12dl/Pb/80tVfvzVdBy4oKe" +
       "JP0ZXMHybD\nRjB1GSsQb+rbP/HPg3+70PmJ78dRBzgZhBkqATjAZ3eGz2jw" +
       "izkvxjBZEjnUW4oI3kNBQVsBgVmb\nZk2/AArYYjDEIA9P1z+fvP2NH/e+xi" +
       "X2IllfIOQVMBV+kanbf9XEGMbfenLlwqX3zj6UAE80DGFz\nipKGXVSJ7MCS" +
       "DzW6EmNPYbD5y+W5/i/vtX7ATdxNqlWbSkUVQ+iVVFXfwsoG5bEnE4hzPLyA" +
       "JtJF\nCFMQ8TZU2EjIasRqAM8mgSC7Y/Di43u+/gYLFQZXzHbmUpxT5PCBXT" +
       "HW7o5MsvdRhuDButjg3CeE\nAOk9hfWl4+cmueTB7WbcF8ffsJOPsLR5W1tf" +
       "OMBSSh0q5OG/P3r5d5fScRRfQyliHSCapDJlWYcE\n+JsE09AWp1954BvXf0" +
       "vf5qato40xNuJEg8YRKeAId12tZZIvPFONo9Qa6ucJUdLoEUm1meHXIKVZ\n" +
       "C+5gDt3UMN+YnkQsnvO9aTSM9MCxYZzXgxX0GTXrdwloc5o+0GwPPGPwpN2g" +
       "yf95hBRhMhYzWOfj\nfMEYbyd8rKYMk9QkVkMID4IQY9jUiqp2xSRVSAM1N0" +
       "99Zeezf3zpnfygiBMime+K5NPgGpHQOe+9\nBvONW9qdwKlfnbnl+TP5t4si" +
       "0Q00+tJ+za7e8cybeM+n03KTyJ6AkoP7PURh9j/boK9heLa5+trW\nXF/zrL" +
       "mTAnCpZFK+ep8hUP1JCklN18r/1/73uvsnIFWy7me93Uww1Q39BGIr1SEH1w" +
       "F7/Etv3r98\n7OisUNZH2m5xCDSlhJzl0qJ2zyvbr2pxFk2T1mYAenGLUjSd" +
       "87Z0rcteRa+wVBBTojIKqOPf4vcB\ne5ga2IvI6QMLetWAQsCcuBeDTwIEFc" +
       "NTzXJTqHaWWAQAoCZNfDKPRTgboGjEF9W0NUqqmJXgrnq4\nTmM8whVAsdMh" +
       "rfgiwJI8lrEPb9d0MaHMO9osW4Y+1IwgTdXWiMxrbbHH+NUdT/dMzdzFwdlb" +
       "FXSr\nvhO35yag0CA3LEXJFaJyxORgj11t7ez6aqrw3NHyxWfjblQX7ndr29" +
       "M1JaoItvao4XAT3e1pv59n\nBxbpsqIQ5xNLrDnk2XOjiT1ZP8+ijsVrrAJR" +
       "uF5WAxYrApe723H5GVwmERUxg324zarAon3qE69/\na+XaOU+4Y65/H3H/H/" +
       "KEZc2600KKSTePonoenXHLP1dHYy2qTFFeMmOMtbpK8Dh49ti19BelV9c9\n" +
       "Pj9FUTfVjb0qrmHVz7MJfmgCdtvbFhTL/EJVd/xE4Z49u2/r/RPUZC3KzYw7" +
       "mMdwe9R8CPM6VPqf\n69DxkKxhfjK1kfsTFfKLOM+AImlG7omNi+YaU2WP2c" +
       "goV9pwQ4Aeh6ffBU1/KEBzCPLo3LRAitVN\nv37jEsqGwGWwyzwHUqzuOzOR" +
       "baIjps9zNzxZeIZcnoda8VxpylGcbx33wXz3jRk/A4FW+GYk99V0\novCVXw" +
       "jwHdm8xXBdJqb3aXhmXJlmmskUdTq+4yTjjn8CaS5J/QIWvqXzq5vw/8uJqW" +
       "vxHw1N8dtI\nR1GyPFw3ft6Ifr1o+CjB+ezxpWKpf4IVvK5UnSLf9Qlnz9ua" +
       "5l9hL/Kl6xGm2fvtrLngxBCPQI+3\nUENeBCBQhoq1srhKc2V/1Qnb3gtGN9" +
       "cD9gIUMZjdO7w5UeoTPet/OoJJpyl/luCPn9Uad//1TeOb\n4CYyY6dJXhEV" +
       "tNPe0FHp9jsyNlg65gecB9xWofznsI2ANxbQWcMIqPamho8B7CK4I/KxTnxS" +
       "kiev\nHJ/+mZH5pQCU99knlUNdJVtVg8V8oJ80TFwinMmUKO0N/vciRUPNEw" +
       "cUjdByZl8QtN+jfsQN0EKa\n8PtB6pcp6vKo2fsPOca+7cT+A1PoN+bfFAAA");
}
