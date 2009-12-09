import java.io.*;

public class MyObject implements Serializable {

  private byte [] byteArray;
  public boolean count;
  public static int DEFAULT_SIZE = 1024;

  public MyObject() {
    byteArray = new byte[DEFAULT_SIZE];
  }

  public MyObject(boolean count) {
    byteArray = new byte[DEFAULT_SIZE];
    this.count = count;
  }

  public byte [] getByteArray() {
    return byteArray;
  }

}
