package ecoop.bmarks2.micro;

import java.io.*;
import java.net.*;
import java.util.*;

public class Common  
{
	public static final int REC = 1;
	public static final int QUIT = 2;

  public static final void debugPrintln(boolean debug, String m)
  {
  	if (debug)
  	{
  		System.out.println(m);
  	}
  }
  
  public static final void closeInputStream(InputStream is) throws IOException
  {
  	if (is != null)
  	{
  		is.close();
  	}
  }
  
  public static final void closeOutputStream(OutputStream os) throws IOException
  {
  	if (os != null)
  	{
  		os.flush();
  		os.close();
  	}
  }
  
  public static final void closeSocket(Socket s) throws IOException
  {
  	if (s != null)
  	{
  		s.close();
  	}
  }
  
  public static final void closeServerSocket(ServerSocket ss) throws IOException
  {
  	if (ss != null)
  	{
  		ss.close();
  	}
  }
}
