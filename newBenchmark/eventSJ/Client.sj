
import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

// to run: sessionj -cp . -Dsessionj.transports.session=a Client // RAY: no need to specify transports anymore.
//$ bin/sessionj -cp tests/classes/ Client

public class Client {

  protocol clientSide cbegin.rec X[!{QUIT: , REC: !<int>.?(MyObject).#X}]

  public static int port = 2000;
  public static String host = "";

  private boolean timing;
  private int iterations;

  /*Use to simulate begin timing and kill load signals*/
  public boolean beginTiming;
  public boolean killLoad;

  private int clientNum;
  private long []times;

  /*Load Client*/
  public Client(int clientNum) {
    this.timing = false;
    this.beginTiming = false;
    this.killLoad = false;
    this.iterations = 0;  
    this.clientNum = clientNum;
  }
  
  /*Time Client*/
  public Client(int clientNum, int iterations) {
    this.timing = true;
    this.beginTiming = false;
    this.killLoad = false;
    this.iterations = iterations;
    this.times = new long[iterations];
    this.clientNum = clientNum;
  }

  public void client() 
  {  	
  	SJSessionParameters params = null;
  	
  	try
  	{
  		params = SJTransportUtils.createSJSessionParameters("s", "a");
  	}
  	catch (Exception x)
  	{
  		x.printStackTrace();
  	}
  	
  	final noalias SJService serv = SJService.create(clientSide, host, port);
    noalias SJSocket s;
    int i = 0;
    MyObject mo = null;

    try(s) {
      s = serv.request(params);

      s.recursion(X) {
        if(!this.killLoad) {
          s.outbranch(REC) {
            s.send(i);
            mo =(MyObject) s.receive();
            killLoad = mo.killSignal();
            beginTiming = mo.timeSignal();
         //   System.out.println(clientNum + ":" + killLoad + ":" + beginTiming);

            if (this.beginTiming && this.timing && (i < iterations)) {
              times[i++] = System.nanoTime();
            }
            s.recurse(X);
          }
        }
        else {
          s.outbranch(QUIT) { System.out.println(clientNum + " finished");}
        }
      }

    }
    catch (Exception e) {e.printStackTrace();}

    if (timing) {
      for (int j = 0; j < iterations; j++)
        System.out.println("Client Number: " + clientNum + ".Iteration: " + j + ". Time: " + times[j] + ".");
    }
    
  }

  public static void main(String []args) {
    long [][]timing = new long[1][1];
    port = 2000;
    host = "";
    new Client(0).client();
  }

}
