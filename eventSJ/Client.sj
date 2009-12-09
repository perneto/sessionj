import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

public class Client {

  protocol clientSide cbegin.rec X[!{QUIT: , REC: !<int>.?(MyObject).#X}]

  private boolean timing;
  private int iterations;

  /*Use to simulate begin timing and kill load signals*/
  public static boolean beginTiming = false;
  public static boolean killLoad = false;


  /*Load Client*/
  public Client() {
    this.timing = false;
    this.iterations = 1;
  }
  
  /*Time Client*/
  public Client(int iterations) {
    this.timing = true;
    this.iterations = iterations;
  }

  public void client(String domain, int port, long time[][][], int i, int j) {
    final noalias SJService serv = SJService.create(clientSide, domain, port);
    noalias SJSocket s;
    int k = 0;
    MyObject o = null;

    try(s) {
      s = serv.request();

      s.recursion(X) {
        if(k < iterations) {
          if (beginTiming && timing) {
            time[i][j][k++] = System.nanoTime();
          }
          else if (killLoad) {
            k = iterations;
          }
          s.outbranch(REC) {
            s.send(k);
            o = (MyObject) s.receive();
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

  public static void main(String []args) {
    long [][][]timing = new long[0][0][0];
    new Client().client("", 2000, timing, 0, 0);
  }

}
