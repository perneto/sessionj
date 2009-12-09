
import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

// to run: sessionj -cp . -Dsessionj.transports.session=a Client // RAY: no need to specify transports anymore.
//$ bin/sessionj -cp tests/classes/ Client

public class Client {

  protocol clientSide cbegin.rec X[!{QUIT: , REC: !<int>.?(MyObject).#X}]

  private boolean timing;
  private int iterations;

  /*Use to simulate begin timing and kill load signals*/
  public boolean beginTiming;
  public boolean killLoad;


  /*Load Client*/
  public Client() {
    this.timing = false;
    this.beginTiming = false;
    this.killLoad = false;
    this.iterations = 0;
  }
  
  /*Time Client*/
  public Client(int iterations) {
    this.timing = true;
    this.iterations = iterations;
  }

  public void client(String domain, int port, long time[][], int i) 
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
  	
  	final noalias SJService serv = SJService.create(clientSide, domain, port);
    noalias SJSocket s;
    int j = 0;
    //Object o = null;
    MyObject mo = null;

    try(s) {
      s = serv.request(params);

      s.recursion(X) {
        if(!this.killLoad) {
          if (this.beginTiming && this.timing && (i < iterations)) {
            time[i][j++] = System.nanoTime();
          }
          s.outbranch(REC) {
            s.send(j);
            mo =(MyObject) s.receive();
            //mo = (MyObject) o;
            killLoad = mo.killSignal();
            beginTiming = mo.timeSignal();
            System.out.println(i + ":" + killLoad + ":" + beginTiming);
            s.recurse(X);
          }
        }
        else {
          s.outbranch(QUIT) {}
        } 
      }

    }
    catch (Exception e) {e.printStackTrace();}
  }

  public boolean getTiming() {return timing;}

  public static void main(String []args) {
    long [][]timing = new long[1][1];
    new Client().client("", 2000, timing, 1);
  }

}
