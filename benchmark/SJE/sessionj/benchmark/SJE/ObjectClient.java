package sessionj.benchmark.SJE;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;
import java.util.Random;

public class ObjectClient implements Client {
    final SJProtocol reqRep =
      new SJProtocol(
      ("H4sIAAAAAAAAAFvzloG1uIhBtTi1uDgzPy9Lr6SyILVYD8SFsIK9glPzUkKA" +
       "7PhkBghgZGJgqChi\nMMWjxxfITkxPdc7PzS3Ny0xOLAGqg5ghf01iLpeKtg" +
       "UTA6MPA3cuRB1IqoRB1KcgP6cyPSe/RB9s\njj5I2BpokwZe14Gl0B0INJw1" +
       "OSMzJ6WEQcMHph1qLFy7Pop2kE3KaDaByPykrNTkEpBdEEsmsgcv\nD0+fsg" +
       "QSCqIwN0N1QNQ4qm4P9XmxXx6iRhqLGn+wqfHJf8xkZC85PuFkBjmZoyC/OB" +
       "MUViUMEojA\nKC3JzNEPgMpYVxQUVBTjD5Og1OTUzLJUzEgrZKhjYCyoKADq" +
       "l0FzU0BOYnKqR35OSmpRfPLyx6dq\nXL07O8DhyJKXmAuMHSGfrMSyRP2cxL" +
       "x0/eCSosy8dKBTShgEQKJ6IFE9iJeKQZbwoEhAlAMAFraH\nkGsCAAA="));
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
       "JKoHEtWDBHwxyBJ+FAmIcgCAwchj3gIAAA=="));
    private static Random generator = new Random(System.currentTimeMillis());
    private String requestString;
    
    public ObjectClient() {
        super();
        requestString = new String("Number " + generator.nextInt() % 1024 +
                                   " is beeing send");
    }
    
    public String client(String domain, int port) {
        final SJService serv = SJService.create(clientSide, domain, port);
        Object x = null;
        SJSocket s = null;
        try {
            s = serv.request();
            SJRuntime.send(requestString, s);
            x = SJRuntime.receive(s);
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
    final public static long jlc$SourceLastModified$jl = 1259273919000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVYaWwbRRQeO4mdw5DYpKXK0bRNoImaOgVRBEQCgtUjqdOm" +
       "dnoFVelmd2xPut5d\ndmcTt0JQQKIFJKSqLZeA/imqQP3BIeAPAiRayimkIr" +
       "X9A3+KoBIU0T9QoXK8mVmv12vHiEgznp15\nb+Yd33vzJqeuoAbLRHELWxbR" +
       "tdk43Wdgi/f6zCyWqRVPj01IpoWVhCpZ1iQsTMt7/ibb34o+vjGI\nAlMopu" +
       "kjKpGsyZyp29ncZI5YBRMtM3R1X1bVqbNjxR739F6f/+rgWGcdap1CrURLU4" +
       "kSOaFrFBfo\nFIrkcX4Gm9aIomBlCkU1jJU0Nomkkv1AqGtwsEWymkRtE1sp" +
       "bOnqHCOMWbaBTX5mcTKJIrKuWdS0\nZaqbFkVtyVlpThqyKVGHksSiw0kUyh" +
       "CsKtZD6BEUTKKGjCplgXBxsqjFEN9xaD2bB/JmAmKaGUnG\nRZb6vURTKOrx" +
       "c7ga920CAmAN5zHN6e5R9ZoEEygmRFIlLTuUpibRskDaoNtwCkUdC24KRI2G" +
       "JO+V\nsniaoiV+ugmxBFRN3CyMhaJFfjK+E/isw+czj7e2hCJ/PT3xxzLwOM" +
       "isYFll8oeAaamPKYUz2MSa\njAXjNTt+dHSX3RVECIgX+YgFzcgt729LXv6o" +
       "R9B0VqHZwrE4LV+/s6v73MgPTXVMjEZDtwiDQpnm\n3KsTzspwwQB0L3Z3ZI" +
       "vx4uLHqU93HXgD/xxEjaMoJOuqnddGURPWlIQzDsM4STQsZrdkMhamo6he\n" +
       "5VMhnX+DOTJExcwcDTA2JJrj44KBEApDC0DrQ+KvmXUUtQp9EirBGo1bs4wh" +
       "WmD9jfOBAEjc5Y8e\nFaC2UVcVbE7LJy998fC6TU8dEr5g+HEOpKjXjeMZcE" +
       "EuL5l7IX7Xxb3noUCAn3ETQ5ywyIhpSvtY\nJBQeO9f94lnpFbAv6GmR/Zir" +
       "EZivZz0w3V4zUSRKYTYKIwlQMC23H7jc8dK3r58JorqqySLpTq7X\nzbykMn" +
       "8XoyPmHOdfAZj0+cFa7exfnx5/5/yX3/WXYEtRX0U0VXKyaFjh94Gpy1iBbF" +
       "Pa/vk/N/52\npOHud4OoHkIMkgyVABoQsUv9Z5RFxXAxwzBd6pKoJVOheDMF" +
       "A817FGZ9hI9bwRVN0OqgtTu4irGO\no8gDJXBXu08Hnr+uPRFac+GDljPcKM" +
       "VU1+rJiWlMReBESxCZNDGG+e9emDhy7MrBB+HwgmEIWFAU\nMuwZlcgFYLm5" +
       "PNaYBgpD1i9vD7c9u9p6j6OgieTzNpVmVAy5WVJVfR4r05Qnp6gnEfL8A8aK" +
       "zEAe\ng5Q4rcJGwhxGYA4QXCVTxJe0H31u4OULLJcY3F6LmG24pKjAJ/oCrO" +
       "+vWGTfXQzk7SW1Ifr3CgUi\nA+ndY3sOreCae7cbdD4K7ob1fKYedhqsGS7r" +
       "2Z1TQhN5+PfH3v7mWCSIglMoTKz1RJNUZixrs4iP\nKtnWt8X+D7e9eu1r+j" +
       "13bQmQTLDOQmVa2S55YuWu83PR0JvH80EUnkJt/MaUNLpdUm3m+Cm486yE\n" +
       "M5lEN5Stl99fItkMuwHX5Q8Gz7H+UCilMxgzajZu9KE/Cm0TtFYH/a0+9AcC" +
       "Bhus5QxLeb9cuCxI\nUdgwyZxEQeaQxUuOAkVNWQxWklh1wIhjUCKUIJCSNE" +
       "XP84hCiK+vcSVh+bwHWpsjSVt1Se6tlIRj\n8G44+wYTP2Rji4pbv/x8Zs64" +
       "WCieb6I7/htTkKqoDhdaybl7nrm4dXznjiFxxd5Wc4vN4ALFB6xj\no9p9Hy" +
       "46rwVZcgpZsx43BS1KUX+yuKXjY/YpRumxtFgSZYbHgP+Iv79ZY4ZjH+KCjC" +
       "X0vAG3qrls\ng/AMVoyiMTdWMSYFFLFoAXuGwJ4pbBQN2emqatoaJXnM6lnH" +
       "PNymAe6JzWDYW3xWcVWIMxU0RRQr\njqcDwpJra/CMwxiqL1Alb2tE5lWr2K" +
       "Pn/JJXmvtW3cXDtCUv6CZdtJuQnGqJ4lrTKw3L5XKOqArb\nYwPs0VvTyYL9" +
       "cDh9ckf26Imgk/5aDJbDa52ewjImc7jSFow9ZRSMKggWCYHNJ/jqOtaNFv05" +
       "tUBw\njIMzm2VesqSJwk2zxeOx3SDoyhqCJh7AWVJhJeazW2tweZjuUZ8/+9" +
       "rE1UNF5dKGyPBbnd/toCz7\n3ckV4yTLxP2CSvfLoFM5OUbpXqA+E5UZs333" +
       "QjU4fz8c3Hk18qR0erdQJFZ+167T7Pza4xfxwP0R\nuUpt2ER1Y7WK57DqXl" +
       "Slum51TbCM8ydLKRvUpe8bWDnY8hPUPQuUdFFnMoXhfaa50Oa1nvS/a70e\n" +
       "n1H88kTnOrfW5chnQX6FiFun4iVWzjRcftc0m+WCcnd2lNVbQ9AWO0haXK3e" +
       "8iDAV2EExeUjIqDy\nEp8wSR5eJMWoOrz0xI/vXEq1Cx+Kd2VvxdPOyyPelp" +
       "4IXl7rBE59etXyU4+kvp8pojtHUR1hEPyv\nCkmHFCuikqsTKIX1oEdL1tmF" +
       "BaYhsCPeFwkrNZdU/L9AvGrlFef29H9iRD/nNbb78gzD8y9jq6q3\nXPCMQ4" +
       "aJM4TLGxbFg8F/DsCbvnoIgvrQc4EfFbRPUBeSHlqII3fspX6SosYiNfs+KH" +
       "JDAf0La8Ba\nv2IRAAA=");
}
