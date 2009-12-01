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
      ("H4sIAAAAAAAAAK1Yb2wcRxWf++Ozz75gn2OnkeMkTuyqMcRnIAoUjEQsK5Gd" +
       "XBtjOxZ1VS7jvbnz\nJnu7253Z8yZUVRIQafuBCjUFWpX2A0iIqh8gVeFDq4" +
       "JEw58CQkqlhi+tkIqgEg1qBYIIFcSbmb3b\nv76LhU+a2bnZN/Pmvfd7v5nZ" +
       "F26iDmqhAiWUqoZ+psDOmYSK2lg9QxRGC4vH57FFSXlGw5QuwYuS\ncvq/6v" +
       "KP8pdmkyixgvp1Y1pTMV1aswy7ura0plLHQiOmoZ2ragZzZ4zM8dnRD9d/e/" +
       "n4rhTqXUG9\nqr7IMFOVGUNnxGErKFcjtVVi0elymZRXUF4npLxILBVr6nkQ" +
       "NHRQTNWqjpltEbpAqKHVuWA/tU1i\nCZ2NziLKKYZOmWUrzLAoQ33FM7iOJ2" +
       "2mapNFlbKpIspUVKKV6YPoYZQsoo6KhqsguKPYsGJSzDh5\njPeDeLcKy7Qq" +
       "WCGNIemzql5maG94RNPisRMgAEM7a4StGU1VaR1DB+qXS9KwXp1cZJaqV0G0" +
       "w7BB\nC0NDG04KQl0mVs7iKikxtDMsNy9fgVRWuIUPYWgwLCZmgpgNhWLmi9" +
       "bJTO4/j83/awQiDmsuE0Xj\n68/AoD2hQQukQiyiK0QOvGUXrszdZw8nEQLh" +
       "wZCwlJm+8yeniu/+dK+U2RUjc1JgsaR8+Knh3den\n/5RN8WV0mQZVORQClo" +
       "uozrtvphwT0L2jOSN/WWi8/NnCL+678Dz5axJ1zaGMYmh2TZ9DWaKXZ9x2\n" +
       "J7SLqk5k78lKhRI2h9Ka6MoY4j+4o6JqhLujA9omZmui7ZhI/hJQSm47wyuG" +
       "emc0lehswdZ1YhXo\nGT4g7/D6I+uJBKx4OJw9GkBt1tDKxCop33/n9YeOnn" +
       "j0ERkLjh9XIUOjzTxehRCs1bB1FvL3aMGv\nDyUSQsd2jjjpkWnLwud4JjgX" +
       "r+9+6pf4O+BfsJOq54kwI7Ge5jUM+mRLopjx0mwOWhhQUFIGLrw7\n9PQbP7" +
       "iWRKlYsig2O48ZVg1rPN6N7Oh31YXfAEzGwmCN0/23x+558c3fvHXAgy1DY5" +
       "Fsio7k2bA/\nHAPLUEgZ2Mab/lv/nn3/iY7PvJREaUgxIBmGARqQsXvCOgJZ" +
       "MdVgGG5Lqoh6KhHDuxk4aN1nMK9z\not0LochCSUE55OLqE7wSKPJBCcI1EL" +
       "JB8Netr2Q+fuOVnmvCKQ2q6/Vx4iJhMnHyHkSWLEKg/61v\nzz/x5M3L94Ny" +
       "xzQlLBjKmPaqpioODLkjmGvcgjJH1ntXp/q+PkF/LFCQVWs1m+FVjQA3Y00z" +
       "1km5\nxAQ55X1EKPgHnJVbBR4DSixpMJF0h5moA4JjmKKwc+DKN8efucG5xB" +
       "T+GgwAOExX85ZaAzaou3T1\njT3f+/OL7ywMSMBITh+N0Kp/jOR14fEek3tg" +
       "XysNQvq1j+174eGFt1cl3/UHPXZUt2uHn/sDGT+S\nU2IyPAU7jyPsGkvw+k" +
       "DTRiRsRGIlw9zUAS96QGJnZRxy44sPHD/9yH4RQOkVPuCgnPOQv8dpTp0V\n" +
       "3VmY82DL/D/GN1EvPdSH/nnx6u+fzCVRcgV1qvSYqmONR5/eKxM+ZvsITXH+" +
       "1VPP3vode1t4wssw\nvrBdTpQnl7Ev+e9+s57P/PC5WhJ1rqA+cQTAOlvGms" +
       "2RvAKbOJ1xO4toW+B9cEOWu89Uk0GGw4Dw\nqQ3nthc9aHNp3u4KpXMeyhCU" +
       "Ljedu0LpnEiYvPE5MWCPqPfJ4CUZ6jQttY4ZrDlDxRnKYbANGZbI\nlIOCCQ" +
       "D5/PnpgMK9Lo+gxjOqcCZGIW8f4dU06MmUjRoERAj0w8FKII77rCAPMVI7it" +
       "G+D0q3q707\nXvvxdtp7LAKUIfZy2sbYESg5V10uXt3Jduo6mFoD9EfPSWL7" +
       "lPxxNTX2QfLlHWNiR0ivYirjHT5g\nRs+PgWOhgEe3KRNwyX1+1H1OQHg1Q6" +
       "9GyI3/v9t0EkiYc38sWDoqPAd53ygPn0b0qjyvCN8teymf\nlCMakd3uRXYG" +
       "lBPO3Y13kmdUo9A8n8NLJ7I8C+0N+e0eYbKXsPn6ri+k1tRfJUXiyFyLHKiD" +
       "g6aC\nGdZtEbgP6EuBPBsyxWOZVyMtqbMtr66BBxVufwziJUtE7Q6G5cENUP" +
       "YlGRNel5rxOBWJB/+LeaXE\nK6pIW09tga11XtV4pTsb5/EYlG1uYm2LT6wv" +
       "t0usTrjB2RqjEu6B6e9oMb0U59VFscDZeNdLAdf9\nX92M+y9t3v3eai5tQQ" +
       "ge9YXgNox7fDPGXfh/jLuwBcZduS18DUPpdQHQG4+vp9ruUoq4/jRNj24R\n" +
       "fAfa6WpEjWdU07NRTcLAI6AkCyd2gsv32rUWevi8E1C2u3q2x+v5bjuL0sQh" +
       "SoODxrxzHhxiFNuC\niwYrHAUJG640QMp1VSEbe/gElEF3PYPx63m+3XqyVQ" +
       "KnMsw/r7iL6vMWtYB1OCa4C3CiUzWwgjys\nHPLBDibr91iW32Hl/mKh3Rt9" +
       "rxCn7Mtf/CD3NfzaA0kXbwUeI8Oc0EidaE1EpoWmNMw20fJoG96r\nUoufH7" +
       "/rYM9fYKPf4FabdzsXgluSuO7iTV53RfoFojbukiNqPH1RE65uHbLY7Ex4iJ" +
       "5tn7+vgD8p\nYfLTQuSEUjfUshh8LXyxmI1cNdyelyMby4hr4MgWGXjb3PQ6" +
       "Q906cfzGAQx3t/y6IqZ7w2cbcqJ/\nPBuzbvofdm08HGfjFtDsDbgzWrYuQh" +
       "EJSOsl9kE5AGXaXeL0JsIwens4u9TegD8CmvglI94C/yyB\nHmClnP+zF/+e" +
       "sTPyUVp+OlX2Xz994Odm/tfy2N74vNlZRF0VW9P8VzhfO2NapKKKNXbKC508" +
       "fN1k\naEc8UiAUUIu1vidl32fNpPfJQmY1237pvzPU1ZDm//8h2Fl30P8A+I" +
       "c/ascXAAA=");
}
