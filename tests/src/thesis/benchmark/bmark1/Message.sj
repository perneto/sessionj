package thesis.benchmark.bmark1;

import java.io.Serializable;

public abstract class Message implements Serializable 
{
	private int senderId;
	private String msg;
  private int messageSize; // Actually, the payload size 
  private byte[] payload;
  
  public Message(int senderId, String msg, int messageSize) 
  {
    this.senderId = senderId;
    this.msg = msg;
    this.messageSize = messageSize;
    this.payload = new byte[messageSize];
  }

  public int getSenderId() 
  {
  	return senderId;
  }
  
  public String getMessage() 
  {
  	return msg;
  }
  
  public int getMessageSize()
  {
  	return messageSize;
  }
  
  public byte[] getPayload() 
  {
  	return payload;
  }
  
  public abstract String toString();
}
