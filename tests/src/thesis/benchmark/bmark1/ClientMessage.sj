package thesis.benchmark.bmark1;

public class ClientMessage extends Message
{
  public ClientMessage(int cid, String msg, int serverMessageSize) 
  {
    super(cid, msg, serverMessageSize);
  }
  
  public String toString()
  {
  	return "ClientMessage[sid=" + getSenderId() + ",msg=" + getMessage() + ",size=" + getMessageSize() + "]"; 
  }
}
