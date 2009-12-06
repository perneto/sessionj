package sessionj.benchmark.SJE;

import java.util.concurrent.*;
import java.util.Random;

class ClientRunner implements Runnable {
    private static int port;
    private static String domain;
    private static int repetitions;
    private static long[][] times;
    private static String[][] results;
    private static int client;
    private int threadNum;
    private static ExecutorService exec = null;
    private static Random generator = new Random(System.currentTimeMillis());
    
    private static void setClient(String c) {
        if (c.equals("Simple"))
            client = 1;
        else
                if (c.equals("Request")) client = 2;
                else if (c.equals("Type")) client = 3;
    }
    
    private static Client nextClient() {
        int x;
        switch (client) {
            case 1:
                return new SimpleClient();
            case 2:
                return new RequestClient();
            case 3:
                switch ((x = generator.nextInt() % 3) > 0 ? x : -x) {
                    case 0:
                        return new IntClient();
                    case 1:
                        return new StringClient();
                    case 2:
                        return new ObjectClient();
                }
        }
        return null;
    }
    
    public ClientRunner(int threadNum) {
        super();
        this.threadNum = threadNum;
    }
    
    public void run() {
        int i;
        for (i = 0; i < repetitions; i++) {
            times[threadNum][i] = System.nanoTime();
            results[threadNum][i] = ClientRunner.nextClient().client(domain,
                                                                     port);
            times[threadNum][i] = System.nanoTime() - times[threadNum][i];
        }
        for (i = 0; i < repetitions; i++) {
            System.out.println("Thread number: " + threadNum +
                               ". Client Number: " + i + ". Result: " +
                               results[threadNum][i] + ". Time: " +
                               times[threadNum][i]);
        }
    }
    
    public static void main(String[] args) {
        int i;
        if (args.length != 5) {
            System.out.println(
              ("Usage: java ClientRunner <server name> <port> <domain name> " +
               "<core number> <repetitions>"));
            return;
        }
        ClientRunner.setClient(args[0]);
        port = Integer.parseInt(args[1]);
        domain = args[2];
        int numCores = Integer.parseInt(args[3]);
        repetitions = Integer.parseInt(args[4]);
        exec = Executors.newFixedThreadPool(numCores);
        times = (new long[numCores][repetitions]);
        results = (new String[numCores][repetitions]);
        for (i = 0; i < numCores; i++) { exec.execute(new ClientRunner(i)); }
        exec.shutdown();
    }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259273474000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAK1YbWwcxRme+/DZZ19qn2OHyHESJ3bVGOJz2ygtxZUay0pk" +
       "JwcxtmMVI7iM9+bO\nm+ztLruz501AKAkVoVSqhPgo9Iv+aFUV5Ucb1PZHgS" +
       "Lx/VFVCgLaH/CjVC1SCy1I0AhB1Xdmdm8/\nfRdXPmlm52bfmXfeeZ/nnXf2" +
       "wnuozTRQwSSmKWvqiQI9pROT19ryCSJRszB/eBYbJilPKdg0F+BF\nSTr+X3" +
       "nxV/lz00mUWEK9qjapyNhcWDE0q7qysCKbtoGGdE05VVU06swYmeO64U9XXz" +
       "1/eFsKdS+h\nblmdp5jK0pSmUmLTJZSrkdoyMczJcpmUl1BeJaQ8TwwZK/Jp" +
       "ENRUUGzKVRVTyyDmHDE1pc4Ee01L\nJwbX6XYWUU7SVJMalkQ1w6Sop3gC1/" +
       "G4RWVlvCibdKKIMhWZKGXzNnQnShZRW0XBVRDcUnStGOcz\njh9i/SDeKcMy" +
       "jQqWiDskfVJWyxTtDI9oWDxyBARgaHuN0BWtoSqtYuhAvWJJClar4/PUkNUq" +
       "iLZp\nFmihaGDNSUGoQ8fSSVwlJYq2huVmxSuQyvJtYUMo6g+L8ZnAZwMhn/" +
       "m8dTST++ze2f8MgcdhzWUi\nKWz9GRi0IzRojlSIQVSJiIGXrcIDMzdZg0mE" +
       "QLg/JCxkJj//22PFd3+/U8hsi5E5yrFYkj79yuD2\nS5N/zabYMjp0zZQZFA" +
       "KWc6/OOm8mbB3QvaUxI3tZcF8+Pff8TWceI/9Ioo4ZlJE0xaqpMyhL1PKU\n" +
       "026HdlFWieg9WqmYhM6gtMK7Mhr/D9tRkRXCtqMN2jqmK7xt60j8ElBKTjvD" +
       "Koq6pxSZqHTOUlVi\nFMwTbEDeZvXnVhMJWPFgmD0KQG1aU8rEKEk/f+flOw" +
       "4e+fY9whcMP45CioYbPF4GF6zUsHES+Huw\n4NeHEgmuYzNDnNiRScPApxgT" +
       "7LOXtj/yAv4R7C/YacqnCTcjsZpmNQz6ctNAMeXRbAZaGFBQkvrO\nvDvw/d" +
       "d+8VwSpWKDRbHReUgzalhh/nbZ0euoC78BmIyEwRqn+/17r3/8jVfe2uPBlq" +
       "KRCJuiIxkb\ndod9YGgSKUO08ab/3ifT/76/7Wu/TqI0UAyCDMUADWDsjrCO" +
       "ACsm3AjDbEkVUVclYngnhQ1a9RnM\n6hxvd4MrslBSUPY5uPoSqziKfFACd/" +
       "WFbODx6/JdmS+++UTXc3xT3FDX7YuJ84QK4uQ9iCwYhED/\nWw/P3v/ge+dv" +
       "BuW2rgtYUJTRrWVFlmwYclWQa8yCMkPWPy9O9Hx3zPwNR0FWrtUsipcVArEZ" +
       "K4q2\nSsolyoNT3hcIefyBzcotQxyDkFhSYCKxHXqiDgiOiRSFrX0PPDT6wz" +
       "dZLNH5fvUHABwOV7OGXINo\nUHfC1X07fvq3x9+Z6xOAETF9OBJW/WNEXOc7" +
       "3qWzHdjVTAOXfvaaXRfunHt7WcS73uCOHVSt2v5H\n/0RGD+SkGIan4OSxuV" +
       "0jCVbvadiIuI2Ir2SQmdrneQ+C2Enhh9zo/C2Hj9+zmztQ7AobsFfMuc/f\n" +
       "YzemzvLuLMy5tyn/D7FD1KOHfMfHZy/+8cFcEiWXULtsHpJVrDDvmzcIwscc" +
       "H6EpTj917MeX/0Df\n5jvhMYwtbJsdjZOL2Ef+a9+o5zO/fLSWRO1LqIenAF" +
       "ili1ixGJKX4BA3p5zOItoUeB88kMXpM9GI\nIINhQPjUhrnteQ/aTJq1O0J0" +
       "zkMZgNLh0LkjROdEQmeNr/MBO3i9SzgvSVG7bsh1TGHNGZPnUDaF\nY0gzOF" +
       "P28kgAyGfPrwYU7nTiCHKfUYVTMQpZ+wCrJkFPpqzVwCFcoBcSK444tmcFkc" +
       "QI7ShG+y4o\nnY72znjth1tp7zIIhAx+lpstjB2CknPU5eLVHW2lro3KNUB/" +
       "NE/ix6eIHxdTIx8kf7dlhJ8I6WVs\nCn+HE8xo/hhICzk8OnVBwAXnebXzHA" +
       "P3KppajQQ39v9a3U4gbs7NsWBpqzAOsr5h5j6FqFWRr/C9\nW/QonxQjXM9u" +
       "9jw7BcoJi93uOxFnZK3QyM/hpR1ZnoF2hvbtem6yR9h8fduNqRX5xSQnjuBa" +
       "JKEO\nDpoIMqzTIHAfUBcCPBvQ+WORVUNNQ2fLuLoCOygx+2MQL6JE1O6gW2" +
       "5bA2W3Cp+wutTwx7GIP9hf\nzCopXlFF2HpsA2yts6rGKtVem8cjUDY5xNoU" +
       "T6zbWxGrHW5wlkJNAffA9Fc1mT4kPuDQvM0R509I\nbACVdWIEcu1vcYOm41" +
       "3Fqrtcd929HnedW7+7hDI+eANc9h2fy9Y27qxr3H3rMe7M/2fcWT7NmQ0w\n" +
       "7qErwuMglG4HAd3xePxBy1NN4telhunRI4WdWFsdjch9RjX9JKqJG3gAlGQh" +
       "wye4fINVa6KHzTsG\nZbOjZ3O8np+1sihNbCK5MWvEywsh6ZEsAy4mtHAQJC" +
       "y4AjG6yBJZe4ePQOl31tMfv54LrdaTrRJg\nI2afY5xF9XiLmsMqpBXOAuzo" +
       "VC5WkIeVfT7YwWS9XlRmvBfnkYG2r/V9g2fl57/5Qe5u/OwtSQdv\nBeYjTR" +
       "9TSJ0oDUSmuaY0zDbWNBUOn22p+W+MfmFv198hMVjjFpx3OueCRxi/HuN1Xo" +
       "85/QJeG3WC\nKXKfPq/xrW7uslh2JjxET7fm71Ownyah4lNEJKOpa3KZD34h" +
       "fBGZjlxNnJ4nIwfRkGPg0AYZeMWx\n6VWKOlVi+40DGG5v+jWGT/e6zzZkR/" +
       "94NmYd+u93bNwfZ+MGhNk/wx3TsFTuiohDmi+xB8oeKJPO\nEifX4YbhK8PZ" +
       "udYG/AXQxC4l8Rb4Zwn0QFTK+T+Tse8fWyMfscWnVmn3peN7ntHzL4k03/0c" +
       "2l5E\nHRVLUfxXPl87oxukIvM1tosLoEjW/kXRlnikgCug5mt9X8h+SBuk98" +
       "kCsxptv/RHFHW40uz/xzw6\nqzb6Hz59URr3FwAA");
}
