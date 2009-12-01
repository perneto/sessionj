package sessionj.benchmark.SJthread;

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
    final public static long jlc$SourceLastModified$jl = 1259319548000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYbWwcxRme+/D566h9xkkj20mMY0iskHNpS5tiWjCnfDhc" +
       "iPGZAK6oM96duxtn\n9qO7s/YmQoi0UpPSqhJNAgXx8YcKFfGjDaL90YpWKp" +
       "R+CilIgT/wh6ogUarmTxtVlPadmd29vb3z\npbW0e7Mz877zvvM+7zPv+MWP" +
       "UJfroKJLXJda5mqRn7CJK9/WyirRuFusHJrHjkv0EsOuuwgDy9qx\nT+jRnx" +
       "S+cTCNUktoyLRmGcXuYt2xvFp9sU5d30HjtsVO1JjFA40tOm7Z8fH6H08fGs" +
       "2ggSU0QM0K\nx5xqJcvkxOdLKG8QY4U47qyuE30JFUxC9ApxKGb0JEy0TFjY" +
       "pTUTc88h7gJxLbYmJg65nk0cuWbY\nWUZ5zTJd7ngatxyXo8HyKl7D0x6nbL" +
       "pMXT5TRrkqJUx3v44eQuky6qoyXIOJm8uhF9NS4/R+0Q/T\n+yiY6VSxRkKR" +
       "7HFq6hxtT0pEHk/eCRNAtNsgvG5FS2VNDB1oSJnEsFmbrnCHmjWY2mV5sApH" +
       "Ixsq\nhUk9NtaO4xpZ5mhLct68GoJZvXJbhAhHm5LTpCaI2UgiZrFoHcnl//" +
       "3I/D/HIeJgs040JuzPgdC2\nhNACqRKHmBpRgle84rm5+72xNEIweVNispoz" +
       "e/3P7il/8Mvtas5omzlHJBaXtY+/MLb14uyfezPC\njB7bcqmAQpPnMqrzwc" +
       "iMbwO6N0caxWAxHPzVwm/uf/gF8mEa9cyhnGYxzzDnUC8x9VLQ7oZ2mZpE\n" +
       "9R6pVl3C51CWya6cJb9hO6qUEbEdXdC2Ma/Ltm8jhLrhScGzDam/HvHiaKBC" +
       "DZuREqPE5EV3VQgU\nfPH+1HoqBRaPJbOHAdQOWkwnzrL2/Hu/f3Dfnd8+o2" +
       "Ih8BMsyNGuKI9XIAR1AzvHIX953SFYL8YX\nRamUXOhaATu1LbOOg0+IdPBP" +
       "Xdz6xOv4adhkcNalJ4n0JbWeFW8Q+mxHtig1cm0OWhigsKwNP/zB\nyJNv/u" +
       "i1NMq0ZYxy1LnfcgzMRNDDFBkKlkuOAFYmk4htt/bfHjn80qU/vLOrgV2OJl" +
       "tSqlVSpMRE\nMhCOpREdKKeh/vF/Hfz72a4vvZxGWcgzYBqOAR+QttuSazSl" +
       "xkxIM8KXTBn1V1sc74PAWesxh8U7\nL16DCiwQi+GEgZKhrnwz95m3ftH/mv" +
       "Q4JLOBGOtVCFepUWjEf9EhBPrf+cH82fMfnf5qBpLRtlXM\nOcrZ3gqjmg8i" +
       "n27OJmGeLmDz1wszg9/b4/5UhriXGobH8QojwL6YMWud6Mtc0k8hRnWSYWAn" +
       "8ivA\nVEB6ywwUKV/t1BrAsw0XFLcMn3ts6qm3BFvYcmM2iaySliJfduxIif" +
       "fOlkHxPSYQPNxwG/L7uHIg\nP1V54NCxMxPS87i63cGHHylMy540aPp8x1zY" +
       "L04VwAy3gF4akDn2nbfvPnzfvdOK8G7qqOIuyG9d\n6mnIn58zb3tl0yUzLV" +
       "CSc1cjDiiDTRxooByqDJAnPlWrcqiihkLSv/HqDjQWpg/+49SFN87nYeEl\n" +
       "1E3d/dTETETbvUtlb5sDIaHi5Cv3PHPlT/xdic1GugjzR/1W5juKY5m899Ja" +
       "IffjZ4006l5Cg/JQ\nxyY/ipknkLsEx7JbCjrL6Jqm8eYjVp0nMxEdjCVTNb" +
       "ZsMlGzsd3Oitmi3aNyU84Z+I/6+0Q8Ajji\nQxH/UMkybDgtnPEDBJzGnOi2" +
       "n0rZQuyLUnirfI9HiddVFVvsQwbW4OwxuRwe4mg0CpvjmZwaRNRp\nAdAkN6" +
       "RkDnwZILorEeEIDCCyQDRC14KzOjikUgqWN3cQOwxtKCzAG8MzqSYLMqVj+6" +
       "UtT/dN7t4r\nw9tvqHmL0S51tiYGzbg1gsS0OmW60HET6NjREbNK/NHuyvP3" +
       "1s49lw7yvt+WhJdE57xDDagGwi14\ndNtzf3npvYVhdVaomm5HS1kVl1F1Xb" +
       "gErHBdpxXk7Fd3X/fiQwvvrqh9Hmrm032mZ9z87Ntk6va8\n1uaEz9AAA9Pi" +
       "tTcET7kNeET7VkBOnyaP/ArVpZqvxOBxBOzd2SEgpTtIjbbEQ1h9QwepmNAt" +
       "7PHX\nfzh/+UwYhTtsRaK3QzTE7wF/A8MnAiZHDSbfHRQgQQZMdKp1VJUjwr" +
       "F1o6JWhuL0fZfz38KvPhDa\nN8dRL7fsPYysEdaG7vd0RN5hWdo3+CpTuW1q" +
       "543970NpsEHVUwg6FwjcY8woT2Q5hP/vcmh7wtek\nPYW10bszdfrbtOQxRX" +
       "0tN5ZmoZlmwutzmg2VmzaiaA+2qBeeaXiuCcAif2V1GytxxWux7Tkd7nMQ\n" +
       "4UF5SgvCLqo7UQwxnY54DGypMC++iJxJYhBKy46Ysg26vxb5NQjPFDyjgV+j" +
       "7fxqRbLUOAHWuPJy\n297yRl2dvH/Jilzl0YXM5OX0zzdPyiIzu4LdECfNF9" +
       "fWe2nTdVPa2Rd5NQLP+FW8Ii12iu/PiZfn\np5BM4vUNPL9VJTL4z4hZU/ei" +
       "A+LFWyupIOTXNkJeYpZJRAUZjqmijVrF6P8AMOi3tW9Z2SfX2hhu\n/3PNeA" +
       "rOYU2Y0waZqpTwr64Eci1rQK0jB/cFPHgQOtcsqku578ZpLrZPTT2wnfn4TU" +
       "4calta/tmi\n/iWgTVw8tuvXduF3Cjbhtb0b7s5Vj7F4IRNr52yHVKm0uVuV" +
       "Nbb8ORuvOlo5l6OesCmt/r6SeoxH\njBWTApqN2vHZT4CWcLb4flIC7LSf+i" +
       "+cva3sqRIAAA==");
}
