import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

public class Client {
    final SJProtocol clientSide =
      new SJProtocol(
      ("H4sIAAAAAAAAAI1UPWzTQBS+JI2ahg6QEohEaVXaQqFSPBUhMtA2pGpTRw1x" +
       "K1CXcLZPjlP/YV+K\nI6HOZayExISQUFGXbgyMqAIJiQ0GhBhYihALA1sHBu" +
       "58jkPSYLDk01nvfd97973vvP8DxB0bXHKQ\n46imUc/ipoWcLP1kO6GYn0eK" +
       "aqySr6oE2BOJAuDa4GII6g/Qde3x2+fln9sMNBUCEliou1aEB3Gp\npmoyBl" +
       "N8C855IC6Acx3wHKk03lWJrqZYRxKmtViRnX5h77byaJf1lrZMraloJvYRLG" +
       "du8uUa//3V\nKMs51yNnxWOtSr+uDp//MPd1IEZbTlimo2JSH4MM3wJxDaxq" +
       "XNmP5FzLcon8V0I0qSCpYf9FlZgG\nRQzOtjXx2IUiD0WkUQkmQ4h507QY50" +
       "bx2fpB8Yj3OPtEU27eA1sg5tI1arnWPzpcaWDRhoZU6+WR\nMGfN90RF10FK" +
       "dW4iCxkyMrDWpAkytQAkYAyG+DrchOyoi9CplaCVC1oFpNdTNJ6l8awfj/d/" +
       "Pnh9\n5u77GIgugKRmQnkBSti0l8AArtnIqZma7Fo3Zr0WBu8nyHqSvFFClg" +
       "769wh9bXtMIcXa0qChcMwP\nZLoYxCqFvBM+CIEctJd0MyGYEtlDBeVNXW8Y" +
       "qgRxYJDRj5knyYnpa15rJ3SWR0MYpNs2ZFeG3RRf\nOyf8bhIfInUTHW+Uwj" +
       "P/Ayc2Pgb35aMcybbf6GYIg8gdQjrcdd/KGpTQIhkYsqvS3uG7B4Xlh9vM\n" +
       "uQbUUecgBGyrhuINIlFqsrEQzpFuTlvVyYVsnW5nZPfbi8PKaca6oRrkxzPe" +
       "rV0HZmKZJDEl454U\nY2EVvOw302P7W5UvIpt1Ksj3bFYwGvrM00/o8uyg1D" +
       "4bpb/gmUo1cEukvltrS6ukZqDZbwqyFCfT\nBQAA"));
    private boolean timing;
    private int iterations;
    public static boolean beginTiming = false;
    public static boolean killLoad = false;
    
    public Client() {
        super();
        this.timing = false;
        this.iterations = 1;
    }
    
    public Client(int iterations) {
        super();
        this.timing = true;
        this.iterations = iterations;
    }
    
    public void client(String domain, int port, long[][][] time, int i, int j) {
        final SJService serv = SJService.create(clientSide, domain, port);
        SJSocket s = null;
        int k = 0;
        MyObject o = null;
        try {
            s = serv.request();
            {
                for (boolean _sjrecursion_s_X = true; _sjrecursion_s_X; ) {
                    _sjrecursion_s_X = SJRuntime.recursionEnter("X", s);
                    if (k < iterations) {
                        if (beginTiming && timing) {
                            time[i][j][k++] = System.nanoTime();
                        } else if (killLoad) { k = iterations; }
                        {
                            SJRuntime.outlabel("REC", s);
                            SJRuntime.send(k, s);
                            o = (MyObject) SJRuntime.receive(s);
                            _sjrecursion_s_X = SJRuntime.recurse("X", s);
                        }
                    } else {
                        { SJRuntime.outlabel("QUIT", s); }
                    }
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            SJRuntime.close(s);
        }
    }
    
    public static void main(String[] args) {
        long[][][] timing = new long[0][0][0];
        new Client().client("", 2000, timing, 0, 0);
    }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1260365356000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAK1YfWwcRxWfu/Od7fMlsR07CbaTOI1LbZrYfLmUWlXiuDGx" +
       "e24cn5OmRsHZ2x3f\nrT23u+zOnc9RVbUgkVCkoqox+RAlUhWoiio+UkH/AA" +
       "qFlqICqijCjZDSf4qgEhSRfyBCpfBmZm9v\nb3e9jlEszXhu5r15X79582af" +
       "fw/FLRP1W9iyVF2b76dLBrZ4r2fnsUyt/sz4pGRaWBkhkmVNw8Ks\nfPID9d" +
       "j3W75wKIoiM6hV04eJKlnTeVMv5vLTedUqm6jb0MlSjujU3tG3xz2731/8ze" +
       "nxzhjaNIM2\nqVqGSlSVR3SN4jKdQakCLmSxaQ0rClZmUIuGsZLBpioR9RQQ" +
       "6hoIttScJtGiia0pbOmkxAhbraKB\nTS6zMplGKVnXLGoWZaqbFkXN6XmpJA" +
       "0UqUoG0qpFh9IoMadiolifR4+gaBrF54iUA8It6YoVA3zH\ngVE2D+RJFdQ0" +
       "5yQZV1jqFlRNoWinl8OxuOd+IADW+gKmed0RVadJMIFahUpE0nIDGWqqWg5I" +
       "43oR\npFDUseqmQNRgSPKClMOzFG3z0k2KJaBq5G5hLBS1e8n4ThCzDk/MXN" +
       "E6nEj95/HJf3VDxEFnBcuE\n6Z8Aph0epik8h02syVgw3ij2nx17qNgVRQiI" +
       "2z3Egmb49hePpt/96U5B0xlAc5hjcVZ+/66u7W8O\n/6kxxtRoMHRLZVCosZ" +
       "xHddJeGSobgO4tzo5ssb+y+LOpXz706LfxX6OoYQwlZJ0UC9oYasSaMmKP\n" +
       "62GcVjUsZg/PzVmYjqE6wqcSOv8N7phTCWbuiMPYkGiej8sGQqgeWgTaAST+" +
       "4qyjqHGEqFij/dY8\nI20ps37jYiQCunZ5zw0BkB3SiYLNWfnZd15/+OD9Xz" +
       "4josCQY4uiKCG2RJEI32Yzg5Mwd9g0pSUG\n8/Jjb26/8Jr0NDgPjLDUU5jr" +
       "GF2sYz0wfTw0C4xUz9AYjCQI8azc9ui7HRd//9yrURQLzARpZ3JU\nNwsSYc" +
       "GsQL/VFuddAQz0eJEYJPvvj0+8sPLra71VTFLU4zsqfk4G9du8bjZ1GSuQSq" +
       "rbn/v3oX88\nFf/0D6KoDs4PZBAqQdzhOO7wyqiB/FAlfTBbYmnUNOczPEnB" +
       "QYsug1mf4uNNEIpGaDFo7TZoWlnH\ngeJCC4SrzWMDT043vpj46Fs/bnqVO6" +
       "WSxza5El4GU3EqWqoQmTYxhvlr5yefWn7v9GdBeNkwOCwi\ngCyjmCWqXAaW" +
       "rbUHiVmgMGT97cpQ8xN7rR9yFDSqhUKRSlmCIfFKhOiLWJmlPPO0uLIcTy7g" +
       "rFQW\nkhTku1kCGwl3GJESIDggDfRvazv7tb6vv8UShcH9xX3ENUVlPtETYX" +
       "2vb5H97mIno61qNhztBWFA\nqi9zYvzkmdu45e7t9rh/bPWFqNMO0bagELGu" +
       "O1CpiPCt5c+ek6ZagORUsrPnkzsu//mFd6baBMTF\nFbPbl+XdPOKa4Qo0GS" +
       "xmu8IkcOpX7tz1/CNTb2dF+m2tjfFBrVgYvHQV9+1PyQFpJwYX4dqOH4i4\n" +
       "nMnpBsv+mbLDG+fTrDDZE5qSRtmlXT2x6sP/fOzKG8upKIrOoHrVGlU1iTBA" +
       "Wg+IHBRwXXm2OPXS\n0W/c+C19m5taPfRMsc6yPzsfk1z56O6VUkvie5cKUV" +
       "Q/g5p5ySFp9JhEiuxwzUDRYI3Yk2m0oWa9\ntgAQt92Qk9S6vBF3ifWmm2p4" +
       "YMyo2bjBk2GS0D5kwxhV/rvgG4kYbHAfZ9jB+11OPqg3TLUksSoN\nJShgSR" +
       "OyP2GI+N0FFFldJ1jS+ClAIvb7fMKTtvBksPAJ1o1S8BrUQLzi4zsMBm/aDG" +
       "0PtA32phuC\nN530WxRl491gi8ULULCqKYtzqjbNTWOL46uL7IO20Ra5MVjk" +
       "sdVEsm4axDUsqISkdUmpkWWiT64N\nfbi1qA6FSxWDJ79y9cjE8QcHxFn+WO" +
       "gWDwBSFA/+l8e0fS+1r2hRdk8lrHkXmqIWpag3XdnShiL7\nKUaZ8YxYEuWk" +
       "y1n/FX8fsMacxH6IQqh1RC8YUD2Z3Z/BGgszVoyK4z4XiL74HDvU4LakzAue" +
       "jKqI\nW7SVok7HXLOoATQxe7vYLuJ+jfBMo4Bz7/B4xjGDFToHePx5ArZjGx" +
       "H+/HAIl4vpHnLutW9OXj8j\nmHpDmFwec8tiV7ecVwmHxAnYY3doIO27oj7z" +
       "7IO5s5ejdtIV6f8jIdKnsFw0V5EfI1KWoq3VcIvi\nYTwtZTFhFdTtIRundd" +
       "0Qey6MPzPz8viNtLg6srqyJExivWrwOiRMw8NFmjUBmfmgaITF8EAgF1wL\n" +
       "rap1HzagmgfskCVRwYCzJWCmaLOrTjokWfkJyRhyVEW1NZO9Hq//48u/2HLy" +
       "dzEUHUVJAsd4VOLF\nJmqEKg9beSjdy8a+/VyF1GKDnThYud3u6M83tH3riw" +
       "KTf4DftVMHR6xwx2fAsCBXDYbwTMAYHolw\nEgtFTZV5qhV77FzZ9nSy5867" +
       "uSpNBUE3XXOnqBxkYRAHkGGnnnFrxdhP3Qw7YNTH7vJNoQomNijB\nG+h4JS" +
       "M0TCyJy5RfHZX1uiNHx6bt64qTl12lGqqWMU7xZ6Ltqz2NeQV1+vj11JekV0" +
       "5UzPoUPPGo\nbuwluISJU9VUH1p7Q4/zBP9AUM3Jscy+vjv2NP0FHiKrvLFa" +
       "7MkpTIum5kSIP76kdT++dnps9erT\nUuo8Esurv4ryekOUKL7vHrVMQ7WFSd" +
       "KsVZQ7raOmuk5D227He/v6qutK9WhjoJmfWVZY9YsvKwIL\n/s8d/KEsgHYl" +
       "1nM9+qMtPfztV5eF7GA7tPY7kf8zUM3XHa5o0gbaOc9/V71UR3S7iHI/Dtjv" +
       "/XAb\nIn4bXlqljJB5LcFqMYK1nPj2wGuki2Uf8GyHbK46ZAQkY/ZUq6yJ15" +
       "Gq9zvf2mCxHKjbVw1u38XV\nA3HTD7Pn4F6XmSoBMRPnN1iFqnuuhLuH9c84" +
       "rrngcw37eZl13wq19cItsPVF1n2Hdd9dy6ifrMeo\n8/+vUedvgVE/dxnlaD" +
       "S4Nt+TgFtRx/nORElXFc75uisXx9lwuWqzV9bNUT1RU8P3QrvXzjT3BmWa\n" +
       "8AI+9IHPD7wjrQNa9xrSloMxwbqVCi6urgcXf1g/LoQwznwLsHHNhY21qd+A" +
       "0Bfgzc0j74t/xGVR\nzUzZ+QJqGEJU5H8jRYTU2xgAAA==");
}
