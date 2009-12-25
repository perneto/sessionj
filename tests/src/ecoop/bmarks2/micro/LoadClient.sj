package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;

abstract public class LoadClient extends Client 
{
  /*public LoadClient(boolean debug, String host, int port, int cid, int serverMessageSize) 
  {
  	super(debug, host, port, cid, serverMessageSize);
  }*/
  
  public LoadClient(boolean debug, String host, int port, int cid, int serverMessageSize, boolean[] ack) 
  {
  	super(debug, host, port, cid, serverMessageSize, ack);
  }  
}
