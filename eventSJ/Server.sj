import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.*;

public class Server {

  protocol recSide rec X[?{QUIT: , REC: ?(int).!<MyObject>.#X}]
  protocol rcv ?(int).!<MyObject>.@(recSide)
  protocol types {@(rcv), @(recSide)}
  protocol serverSide sbegin.rec X[?{QUIT: , REC: ?(int).!<MyObject>.#X}]
  

  public void server(int port) {

    final noalias SJSelector sel = SJRuntime.selectorFor(types);
    noalias SJServerSocket ss;
    noalias SJSocket s;

    try(ss) {
      ss = SJServerSocket.create(serverSide, port);
      try(sel) {
        sel.registerAccept(ss);
        try (s) {
          while(true) {
            s = sel.select();
            typecase(s) {
              when(@(recSide)) {
                s.recursion(X) {
                  s.inbranch() {
                    case REC:
                      sel.registerInput(s);
                    case QUIT:
                  }
                }
              }
              when(@(rcv)) {
                s.receiveInt();
                s.send(new MyObject());
                sel.registerInput(s);
              }
            }
          }
        }
        catch(Exception e){e.printStackTrace();}
      }
      catch(Exception e){e.printStackTrace();}
    }
    catch(Exception e){e.printStackTrace();}

  }


  public static void main(String [] args) {
    new Server().server(2000);
  }
}
