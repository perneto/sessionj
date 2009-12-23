package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;

import ecoop.bmarks2.micro.*;

abstract public class TimerClient extends Client
{
  private int sessionLength;

  public TimerClient(boolean debug, String host, int port, int cid, int serverMessageSize, int sessionLength) 
  {
  	super(debug, host, port, cid, serverMessageSize);
  	
    this.sessionLength = sessionLength;    
  }

  public int getSessionLength()
  {
  	return sessionLength;
  }
}
