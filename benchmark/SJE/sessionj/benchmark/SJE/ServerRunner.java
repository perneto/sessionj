package sessionj.benchmark.SJE;

public class ServerRunner {
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(
              "Usage: java ServerRunner <server name> <port> <client num>");
            return;
        }
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        int clientNum = Integer.parseInt(args[2]);
        Server s = null;
        if (server.equals("Simple")) {
            s = new SimpleServer();
        } else
                  if (server.equals("Request")) {
                      s = new RequestServer();
                  } else if (server.equals("Type")) { s = new TypeServer(); }
        long start = System.nanoTime();
        s.server(port, clientNum);
        long end = System.nanoTime();
        System.out.println(end - start);
    }
    
    public ServerRunner() { super(); }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1259274015000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVXW2xURRievXR7W223lEp6gdLWQINsDYnXGrVpqBSWtO4W" +
       "lBqynT07u3va2XMO\n58zZnqIhqIkgDyYGvEbhxcRoeFCI+qBRE8C7L5iAL/" +
       "CCURLFyIORGDT+M7OXs2eXEpuc2Tkz/z/z\nX77/O3+PX0ENlomiFrEsVdfm" +
       "o2zJIJYY9dQ8UZgVTWydxqZF0uMUW9YMbCSVuX/Vne9HntniR75Z\n1KHpY1" +
       "TF1kzO1O1sbianWo6J+g2dLmWpzoon1pxx/+D1xe8ObO0JoLZZ1KZqCYaZqo" +
       "zrGiMOm0Xh\nPMmniGmNpdMkPYsiGiHpBDFVTNW9IKhrcLGlZjXMbJNYcWLp" +
       "tMAFOyzbIKa4s7QYQ2FF1yxm2grT\nTYuh9tg8LuARm6l0JKZabDSGQhmV0L" +
       "S1B+1D/hhqyFCcBcGuWMmLEXHiyARfB/EWFcw0M1ghJZXg\ngqqlGVrj1Sh7" +
       "PLQNBEC1MU9YTi9fFdQwLKAOaRLFWnYkwUxVy4Jog27DLQx13/BQEGoysLKA" +
       "syTJ\n0Cqv3LTcAqlmERauwtBKr5g4CXLW7cmZK1tTofA/h6b/6oeMg81pol" +
       "BufwiUVnuU4iRDTKIpRCpe\ns6NHJnfZvX6EQHilR1jKjN3+0Y7Y5c/WSJme" +
       "OjJTAotJ5frdvX1nx35qDnAzmgzdUjkUqjwXWZ0u\n7ow6BqC7q3wi34yWNj" +
       "+Pf7Fr/7vkVz9qmkQhRad2XptEzURLjxfnjTCPqRqRq1OZjEXYJApSsRTS\n" +
       "xTuEI6NSwsPRAHMDs5yYOwZCqBEeHzxrkfwL8IGhNkBygZhxW9OIGbXmuULE" +
       "4eOtiz4fWNzrrR4K\nUNui0zQxk8rbl755avO25w/KXHD8FC9kaLBcxylIQS" +
       "6PzQWo381R933I5xN3rOCIkxEZM028xCvB\nefps32tf4jchvuCnpe4lwg3f" +
       "YpCPoLRpWaIYr5TZJMwwoCCpdO6/3P36D++c8aNAXbKIlRcndDOP\nKc93qT" +
       "o6itd5dwAmQ16w1rv790PbT5779sL6CmwZGqqpplpNXg0D3hyYukLSwDaV41" +
       "/5e8sfhxvu\n+8CPglBiQDIMAzSgYld776iqitESw3BfAjHUmqlxvIVBgBZd" +
       "DvMxzId2iRPIRafHQEFO154N3Xn+\nk9YzwuMSj7W5CC9BmKyKSCX/MyYhsH" +
       "7h1enDL1058ASg1DEMmXOGQoadoqrigMpt1YXEzUtz2Px2\nYrT9hY3WhyLF" +
       "zWo+bzOcogSIF1OqL5J0kgnmibhYTpALRCKcApICvktSOEj6avgKAM86NBBd" +
       "1Xnk\n5eE3znOiMERgVvKCEpYiRywM+vi4rmaTv/dyBHdW3IbSXpAOhIcTu7" +
       "fOHRwQnruP21B8KR6OCrW1\nOcG/HCVM5FNP/nnqaEt/xcIN7uP6xNhfjC2f" +
       "D5ROrphd1jBR340YWXxNDjx+NfwcPr1b8mZHdXI2\na3b+rmM/kuGHw0odpm" +
       "hmurGRkgKh5VBVqnzjslW+XXzAKlUQSDw0vO6O1l+gCm5Q4JHiYpzA11rj\n" +
       "9vM7m0Tl4/9d+Ws8QfHaEyn0PBrIqV/5eW0Vi77mu1ytNOoOD1SfWW2oQE+3" +
       "MKANQtQOz3p4Goqs\nLn4Fh7uIvDbffpFvKChLtDr18VpJgvdrLEhafjBPBI" +
       "au+j/uGhK8E0xhqxTP6jamtkupaj6EnS1l\nr7rh6V/GKyHYAd2TKCBew1HZ" +
       "qdSx3dsUVNVIUtn76Y6j175nFwUuK7zJz+lxaitsJ3ZR+r3nCpHQ\ne8fyft" +
       "Q4i9pFY4c1thNTm1PYLDhtjRcXY+iWqv3qNkv2FKNliPR6IeK61svYbrAEWR" +
       "VMBEk/4viQ\nwSdTdWDA4CxVw1SWP+CBEi3LcnXCNm2qeWhVCsVe6sXVb/18" +
       "8lK8U5azbDgHa3o+t45sOoVdrQYn\nlLXL3SCkT29Ye3xf/GLKXyTNexgKAI" +
       "74dMIpp9ovXSlhYkUFE+NU1whn/9KeJFxVj5bbd9h0akDD\n3x+QsRN38WHT" +
       "spR+U76fg0gr3Jw60JXZd25+yIMMBfMAT7E5Y0iFx2CxoKtpobfgYm2fK05V" +
       "K5Dn\nsLsL41/gVTX/I8lOXhk4O7f+lBH5WtZ3qdtuhJY3Y1Pqxp5rHjJMkl" +
       "GFzY0SiYb4gXakq35bCJmF\nUdi6R8rarEy8Lln4WpTnbmnwqakkzd+XBOQz" +
       "ju8/FbCZBlYOAAA=");
}
