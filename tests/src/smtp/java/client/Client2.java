//$ javac -cp tests/classes/ tests/src/smtp/java/client/Client2.java -d tests/classes/
//$ java -cp tests/classes/ smtp.java.client.Client2 localhost 8888 /

package smtp.java.client;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.net.*;
import java.util.*;

class Client2
{
	public static void main(String[] args) throws Exception
	{
		Charset cs = Charset.forName("UTF8");

		System.out.println("Using charset: " + cs.name());

		CharsetEncoder ce = cs.newEncoder();
		CharsetDecoder cd = cs.newDecoder();

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];

		Socket s = null;

		DataOutputStream dos = null;
		DataInputStream dis = null;

		try
		{
			s = new Socket(host, port);

			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());

			//String msg = "GET " + name + "\n\n";

			//System.out.println("Sending: " + msg);

			//ce.reset();

			//CharBuffer cb = CharBuffer.wrap(msg);
			//ByteBuffer bb = ce.encode(cb);

			//dos.write(bb.array());
			//dos.flush();

			for (int x = dis.read(); x != -1; x = dis.read())
			{
				System.out.print((char) x);
			}
		}
		finally
		{
			dos.flush();
			dos.close();

			dis.close();

			s.close();
		}
	}
}
